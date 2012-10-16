package ru.dzakhov;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	private SensorsSurfaceView mSensorsSurfaceView = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("RoboHead", "MainActivity.onCreate() started");
        super.onCreate(savedInstanceState);
        mSensorsSurfaceView = new SensorsSurfaceView(this);
        setContentView(mSensorsSurfaceView);
    	Log.d("RoboHead", "MainActivity.onCreate() stopped");
    }
    
    @Override
    public void onResume() {
    	Log.d("RoboHead", "MainActivity.onResume() started");
    	super.onResume();
    	Log.d("RoboHead", "MainActivity.onResume() stopped");
    }

    @Override
    public void onPause() {
    	Log.d("RoboHead", "MainActivity.onPause() started");
    	super.onPause();
    	Log.d("RoboHead", "MainActivity.onPause() stopped");
    }
}