package ru.dzakhov;

//import android.os.Handler;
//import android.os.Message;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.InputDevice.MotionRange;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * Класс для контроля касаний к лица робота.
 * @author Дмитрий Дзахов
 *
 */
public final class FaceTouchHelper implements OnTouchListener {
	/**
	 * Контрол ImageView в котором будет отображаться анимация.
	 */
	private ImageView mImageView;
	
	/**
	 * Менеджер управления лицом.
	 */
	private FaceHelper mFaceHelper;

	/**
	 * Сохранённая x-координата экрана в момент касания.
	 */
	private float mLastUniX;

	/**
	 * Сохранённая x-координата экрана в момент касания.
	 */
	//private float mLastUniY;

	/**
	 * Признак наличия тактильного контакта пользователя с экраном.
	 */
	private boolean mInTouch;

	/**
	 * Коэффициенты перевода координат касания расчитываются один раз при первом касании.
	 * Признак используется для определения первого касания.
	 */
	private boolean mCoefsCalculated;
	
	/**
	 * Коэффициент для перевода X-координаты касания в универсальные координаты.
	 * Универсальные координаты касания определены в диапазоне от 0 до 1.
	 */
	private float mCoefX;
	
	/**
	 * Коэффициент для перевода Y-координаты касания в универсальные координаты.
	 * Универсальные координаты касания определены в диапазоне от 0 до 1.
	 */
	private float mCoefY;
	
	/**
	 * Используется для определения универсальной X-координаты.
	 */
	private float mMinX;
	
	/**
	 * Используется для определения универсальной Y-координаты.
	 */
	private float mMinY;
	
	/**
	 * Суммарная длина поглаживания.
	 */
	private float mStrokeSize;
	
//	/**
//	 * Хэндлер, принимающий сообщения, сигнализирующие о необходимости что-то сделать. 
//	 */
//	private Handler mHandlerDelayedAction = new Handler() {
//		@Override
//		public void handleMessage(final Message msg) {
//			mFaceHelper.setFace(FaceType.ftOk);
//		}
//	};

	/**
	 * Конструктор класса.
	 * @param imageView контрол для вывода анимации.
	 * @param faceHelper менеджер управления лицом.
	 */
	public FaceTouchHelper(final ImageView imageView, final FaceHelper faceHelper) {
		mImageView = imageView;
		mFaceHelper = faceHelper;
		
		mImageView.setOnTouchListener(this);
	}

	/**
	 * Обработчик косаний лица.
	 * @param v источник касания.
	 * @param event сработавшее событие.
	 * @return true, если действие распознано и обработано.
	 */
	public boolean onTouch(final View v, final MotionEvent event) {
		if (!mCoefsCalculated) {
			InputDevice touchScreen = event.getDevice();
			MotionRange motionRange = touchScreen.getMotionRange(0);
			mCoefX = 1 / (motionRange.getMax() - motionRange.getMin());
			mMinX = motionRange.getMin();
			motionRange = touchScreen.getMotionRange(1);
			mCoefY = 1 / (motionRange.getMax() - motionRange.getMin());
			mMinY = motionRange.getMin();
			mCoefsCalculated = true;
		}
		
		float uniX = getUniX(event.getX());
		float uniY = getUniY(event.getY());

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isInHairArea(uniX, uniY)) {
				mLastUniX = uniX;
				mInTouch = true;
			} else if (isNoseArea(uniX, uniY)) {
				pushNose();
			} else if (isEyeArea(uniX, uniY)) {
				pushEye();
			}
			break;
		case MotionEvent.ACTION_UP:
			mInTouch = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mInTouch) {
				mStrokeSize += Math.abs(uniX - mLastUniX);
				mLastUniX = uniX;
				final float maxStrokeSize = 0.35f;
				if (mStrokeSize > maxStrokeSize) {
					mStrokeSize = 0;
					mInTouch = false;
					
//					// Сделать счастливое лицо:
//					mFaceHelper.setFace(FaceType.ftHappy);
//					final int happyFaceDuration = 5000;
//					mHandlerDelayedAction.sendEmptyMessageDelayed(0, happyFaceDuration);
//					
					// Повилять хвостом:
//					BluetoothHelper.send(MessageConstant.WAG_TAIL_SIN);
					BluetoothHelper.send(MessageConstant.FACETYPE_VERY_HAPPY);
				}
			} else {
				mInTouch = false;
			}
			break;
		default:
			break;
		}
		
		return true;
	}
	
	/**
	 * Преобразование x-координаты касания в универсальную координату.
	 * @param x координата.
	 * @return универсальную x-координату.
	 */
	private float getUniX(final float x) {
		if (mCoefsCalculated) {
			return mMinX + mCoefX * x;
		} else {
			return 0;
		}
	}
	
	/**
	 * Преобразование y-координаты касания в универсальную координату.
	 * @param y координата.
	 * @return универсальную y-координату.
	 */
	private float getUniY(final float y) {
		if (mCoefsCalculated) {
			return mMinY + mCoefY * y;
		} else {
			return 0;
		}
	}
	
	/**
	 * Определение зоны поглаживания.
	 * @param uniX координата.
	 * @param uniY координата.
	 * @return true, если координаты соответствуют зоне поглаживания.
	 */
	private boolean isInHairArea(final float uniX, final float uniY) {
		final float foreHeadMaxY = 0.0926850957509333f;
		return (mFaceHelper.getFace() == FaceType.ftOk) && (uniY < foreHeadMaxY);		
	}
	
	/**
	 * Определение зоны носа.
	 * @param uniX координата.
	 * @param uniY координата.
	 * @return true, если координаты соответствуют зоне носа.
	 */
	private boolean isNoseArea(final float uniX, final float uniY) {
		final float minX = 0.4f;
		final float maxX = 0.6f;
		final float minY = 0.4f;
		final float maxY = 0.6f;
		
		return (uniX >= minX) && (uniX < maxX) && (uniY >= minY) && (uniY < maxY);
	}
	
	/**
	 * Действие при нажатии на нос.
	 */
	private void pushNose() {
		SoundManager.playSound(SoundManager.BEEP, 1);
	}

	/**
	 * Определение зоны глаз.
	 * @param uniX координата.
	 * @param uniY координата.
	 * @return true, если координаты соответствуют зоне глаз.
	 */
	private boolean isEyeArea(final float uniX, final float uniY) {
		// Левый глаз:
		final float minLX = 0.2502606882168926f;
		final float maxLX = 0.3023983315954119f;
		final float minLY = 0.2040816326530612f;
		final float maxLY = 0.2968460111317254f;
		
		// Правый глаз:
		final float minRX = 0.6256517205422315f;
		final float maxRX = 0.7299270072992701f;
		final float minRY = 0.1484230055658627f;
		final float maxRY = 0.3153988868274583f;

		boolean isLeftEyeArea = (uniX >= minLX) && (uniX < maxLX) && (uniY >= minLY) && (uniY < maxLY);
		boolean isRightEyeArea = (uniX >= minRX) && (uniX < maxRX) && (uniY >= minRY) && (uniY < maxRY);
		return (mFaceHelper.getFace() == FaceType.ftOk) && (isLeftEyeArea || isRightEyeArea);
	}
	
	/**
	 * Действие при тычке в глаз.
	 */
	private void pushEye() {
		// Злое лицо на 5 секунд:
//		mFaceHelper.setFace(FaceType.ftAngry);
//		final int happyFaceDuration = 5000;
//		mHandlerDelayedAction.sendEmptyMessageDelayed(0, happyFaceDuration);
		
		// Порычать секунду:
		final int vibrateDuration = 800;
		SoundManager.vibrate(vibrateDuration);

		// Отскочить назад:
		BluetoothHelper.send(MessageConstant.FACETYPE_ANGRY_JUMP_BACK);
	}
}
