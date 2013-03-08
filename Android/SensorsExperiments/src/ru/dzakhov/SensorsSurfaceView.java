package ru.dzakhov;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SensorsSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SensorsDrawThread mDrawThread;
	
	public SensorsSurfaceView(Context context) {
	    super(context);
		Log.d("RoboHead", "SensorsSurfaceView constructor started");
	    getHolder().addCallback(this);
        mDrawThread = new SensorsDrawThread(context, getHolder(), getResources());
		Log.d("RoboHead", "SensorsSurfaceView constructor stopped");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("RoboHead", "SensorsSurfaceView.surfaceCreated() started");

		if (mDrawThread == null) {
			mDrawThread = new SensorsDrawThread(getContext(), getHolder(), getResources());
		}
		
		mDrawThread.setRunning(true);
        mDrawThread.start();
        mDrawThread.resumeThread();
		
		Log.d("RoboHead", "SensorsSurfaceView.surfaceCreated() stopped");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("RoboHead", "SensorsSurfaceView.surfaceDestroyed() started");
		boolean retry = true;
		// завершаем работу потока
		mDrawThread.pauseThread();
		mDrawThread.setRunning(false);
		while (retry) {
		    try {
		        mDrawThread.join();
		        retry = false;
		    } catch (InterruptedException e) {
		        // если не получилось, то будем пытаться еще и еще
		    }
		}
		mDrawThread = null;
		Log.d("RoboHead", "SensorsSurfaceView.surfaceDestroyed() stopped");
	}
}
