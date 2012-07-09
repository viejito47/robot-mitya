package ru.dzakhov;

// import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
// import android.widget.TextView;

/**
 * ����� ��� ����������� ���������� ������ ������.
 * @author ������ �������
 *
 */
public final class OrientationHelper implements SensorEventListener {
	// private Context mContext;
	
	/**
	 * �������� �������� ��������.
	 */
	private SensorManager mSensorManager;
	
	/**
	 * ������ ���������� ��������.
	 */
	private Sensor mOrientationSensor;
	
	/**
	 * ������?
	 */
	private float mAzimuthAngle;
	
	/**
	 * ������?
	 */
	private float mPitchAngle;
	
	/**
	 * �������?
	 */
	private float mRollAngle;

	/**
	 * ����������� ������.
	 * @param context �������� �������.
	 */
	public OrientationHelper(final Context context) {
		// mContext = context;
	    mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	    mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}
	
	/**
	 * ����������� ��������. ��� onResume ��������.
	 */
	public void registerListner() {
	    mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	/**
	 * �������� ����������� ��������. ��� onPause ��������.
	 */
	public void unregisterListner() {
		mSensorManager.unregisterListener(this);
	}

	/**
	 * ���������� ����� �������� �������� �������.
	 * @param sensor ������.
	 * @param accuracy �������� �������.
	 */
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
	}

	/**
	 * ���������� ����� �������� ��������� �������.
	 * @param event ������� � ����������� �� ���������� � ���������� �������.
	 */
	public void onSensorChanged(final SensorEvent event) {
		mAzimuthAngle = event.values[0];
		mPitchAngle = event.values[1];
		mRollAngle = event.values[2];
		
/*
		TextView textViewAzimuthAngle = (TextView) ((Activity) mContext).findViewById(R.id.textViewAzimuthAngle);
	    textViewAzimuthAngle.setText(String.format("%.1f", mAzimuthAngle));

	    TextView textViewPitchAngle = (TextView) ((Activity) mContext).findViewById(R.id.textViewPitchAngle);
	    textViewPitchAngle.setText(String.format("%.1f", mPitchAngle));

	    TextView textViewRollAngle = (TextView) ((Activity) mContext).findViewById(R.id.textViewRollAngle);
	    textViewRollAngle.setText(String.format("%.1f", mRollAngle));
*/
	}

	/**
	 * ������?
	 * @return �������� ���� � ��������.
	 */
	public float getAzimuthAngle() {
		return mAzimuthAngle;
	}
	
	/**
	 * ������?
	 * @return �������� ���� � ��������.
	 */
	public float getPitchAngle() {
		return mPitchAngle;
	}
	
	/**
	 * �������?
	 * @return �������� ���� � ��������.
	 */
	public float getRollAngle() {
		return mRollAngle;
	}
}
