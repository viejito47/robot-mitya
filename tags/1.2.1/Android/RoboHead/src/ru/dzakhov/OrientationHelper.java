package ru.dzakhov;

// import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
// import android.widget.TextView;

/**
 * Класс для определения ориентации головы робота.
 * @author Дзахов Дмитрий
 *
 */
public final class OrientationHelper implements SensorEventListener {
	// private Context mContext;
	
	/**
	 * Менеджер сенсоров телефона.
	 */
	private SensorManager mSensorManager;
	
	/**
	 * Датчик ориентации телефона.
	 */
	private Sensor mOrientationSensor;
	
	/**
	 * Азимут?
	 */
	private float mAzimuthAngle;
	
	/**
	 * Наклон?
	 */
	private float mPitchAngle;
	
	/**
	 * Поворот?
	 */
	private float mRollAngle;

	/**
	 * Конструктор класса.
	 * @param context контекст ативити.
	 */
	public OrientationHelper(final Context context) {
		// mContext = context;
	    mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	    mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}
	
	/**
	 * Регистрация листнера. Для onResume активити.
	 */
	public void registerListner() {
	    mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	/**
	 * Удаление регистрации листнера. Для onPause активити.
	 */
	public void unregisterListner() {
		mSensorManager.unregisterListener(this);
	}

	/**
	 * Вызывается когда меняется точность сенсора.
	 * @param sensor сенсор.
	 * @param accuracy точность сенсора.
	 */
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
	}

	/**
	 * Вызывается когда меняются показания сенсора.
	 * @param event событие с информацией об изменениях в показаниях сенсора.
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
	 * Азимут?
	 * @return значение угла в градусах.
	 */
	public float getAzimuthAngle() {
		return mAzimuthAngle;
	}
	
	/**
	 * Наклон?
	 * @return значение угла в градусах.
	 */
	public float getPitchAngle() {
		return mPitchAngle;
	}
	
	/**
	 * Поворот?
	 * @return значение угла в градусах.
	 */
	public float getRollAngle() {
		return mRollAngle;
	}
}
