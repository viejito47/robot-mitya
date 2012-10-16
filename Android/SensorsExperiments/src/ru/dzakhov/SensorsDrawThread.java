package ru.dzakhov;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.SurfaceHolder;

public class SensorsDrawThread extends Thread implements SensorEventListener {
    private boolean mRunFlag = false;
    private SurfaceHolder mSurfaceHolder;
    private long mPrevTime;
    private Paint mPaint = new Paint();
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private SensorManager mSensorManager;
    private boolean mSensorsRegistered;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticFieldSensor;
    private final float[] mAccelerometerData = new float[3];
    private final float[] mMagneticData = new float[3];
    private final float[] mRotationMatrix = new float[16];
    private final float[] mOrientationData = new float[3];
    private int mHorizontalHeadOrientation;
    private int mVerticalHeadOrientation;
    
    public SensorsDrawThread(Context context, SurfaceHolder surfaceHolder, Resources resources) {
        mSurfaceHolder = surfaceHolder;
        
    	mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    	mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void resumeThread() {
        mWidth = mSurfaceHolder.getSurfaceFrame().width();
        mHeight = mSurfaceHolder.getSurfaceFrame().height();
        mRadius = mHeight / 48;
        
        mPrevTime = System.currentTimeMillis();

    	if (!mSensorsRegistered) {
    		// «амеры количества срабатываний в секунду на HTC Sensation Android 4.0.1:
    		// SensorManager.SENSOR_DELAY_FASTEST: 49-50 раз
    		// SensorManager.SENSOR_DELAY_GAME:    49-50 раз
    		// SensorManager.SENSOR_DELAY_NORMAL:  5 раз
    		final int rate = SensorManager.SENSOR_DELAY_GAME;
	    	mSensorManager.registerListener(this, mAccelerometerSensor, rate);
	    	mSensorManager.registerListener(this, mMagneticFieldSensor, rate);
	    	mSensorsRegistered = true;
    	}
    }
    
    public void pauseThread() {
    	if (mSensorsRegistered) {
    		mSensorManager.unregisterListener(this);
    		mSensorsRegistered = false;
    	}
    }
    
    public boolean getRunning() {
    	return mRunFlag;
    }
    
    public void setRunning(boolean run) {
        mRunFlag = run;
    }

    public void run() {
        Canvas canvas;
        while (mRunFlag) {
            // получаем текущее врем€ и вычисл€ем разницу с предыдущим 
            // сохраненным моментом времени
            long now = System.currentTimeMillis();
            long elapsedTime = now - mPrevTime;
            if (elapsedTime > 40) {
                // если прошло больше 40 миллисекунд
                mPrevTime = now;

	            canvas = null;
	            try {
	                // получаем объект Canvas и выполн€ем отрисовку
	                canvas = mSurfaceHolder.lockCanvas(null);
	                if (canvas != null) {
		                synchronized (mSurfaceHolder) {
		                	canvas.drawColor(0, Mode.CLEAR);
		                	Point point = getOrientationPoint();
		                    mPaint.setColor(Color.WHITE);
		                    canvas.drawCircle(point.x, point.y, mRadius, mPaint);
		                }
	                }
   	            } 
	            finally {
	                if (canvas != null) {
	                    // отрисовка выполнена. выводим результат на экран
	                    mSurfaceHolder.unlockCanvasAndPost(canvas);
	                }
	            }
            }
        }
    }

	private Point getOrientationPoint() {
		Point result = new Point();
		result.x = (mHorizontalHeadOrientation + 180) * mWidth / 360;
		result.y = (mVerticalHeadOrientation + 180) * mHeight / 360;
		return result;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

//	private int mAccelerometerSensorChangedCounter = 0;
//	private long mAccelerometerSensorChangedStartedAt;
//	private int mMagneticFieldSensorChangedCounter = 0;
//	private long mMagneticFieldSensorChangedStartedAt;
	
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
//        	if (mAccelerometerSensorChangedCounter == 0) {
//        		mAccelerometerSensorChangedStartedAt = System.currentTimeMillis();
//        	}        	
//        	mAccelerometerSensorChangedCounter++;
//        	if (System.currentTimeMillis() - mAccelerometerSensorChangedStartedAt >= 1000) {
//        		Log.d("RoboHead", "Accelerometer calls per second: " + mAccelerometerSensorChangedCounter);
//        		mAccelerometerSensorChangedCounter = 0;
//        	}
        	
        	System.arraycopy(event.values, 0, mAccelerometerData, 0, 3);
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
//        	if (mMagneticFieldSensorChangedCounter == 0) {
//        		mMagneticFieldSensorChangedStartedAt = System.currentTimeMillis();
//        	}        	
//        	mMagneticFieldSensorChangedCounter++;
//        	if (System.currentTimeMillis() - mMagneticFieldSensorChangedStartedAt >= 1000) {
//        		Log.d("RoboHead", "MagneticField calls per second: " + mMagneticFieldSensorChangedCounter);
//        		mMagneticFieldSensorChangedCounter = 0;
//        	}
        	
        	System.arraycopy(event.values, 0, mMagneticData, 0, 3);
        } else {
        	return;
        }

        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerData, mMagneticData);
        SensorManager.getOrientation(mRotationMatrix, mOrientationData);

        mHorizontalHeadOrientation = Math.round((float)Math.toDegrees(mOrientationData[0]));
        mVerticalHeadOrientation = Math.round((float)Math.toDegrees(mOrientationData[2]));
	}
}
