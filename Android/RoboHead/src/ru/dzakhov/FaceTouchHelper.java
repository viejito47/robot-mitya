package ru.dzakhov;

import android.os.Handler;
import android.os.Message;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.InputDevice.MotionRange;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * ����� ��� �������� ������� � ���� ������.
 * @author ������� ������
 *
 */
public final class FaceTouchHelper implements OnTouchListener {
	/**
	 * ������� ImageView � ������� ����� ������������ ��������.
	 */
	private ImageView mImageView;
	
	/**
	 * �������� ���������� �����.
	 */
	private FaceHelper mFaceHelper;

	/**
	 * ���������� x-���������� ������ � ������ �������.
	 */
	private float mLastUniX;

	/**
	 * ���������� x-���������� ������ � ������ �������.
	 */
	//private float mLastUniY;

	/**
	 * ������� ������� ����������� �������� ������������ � �������.
	 */
	private boolean mInTouch;

	/**
	 * ������������ �������� ��������� ������� ������������� ���� ��� ��� ������ �������.
	 * ������� ������������ ��� ����������� ������� �������.
	 */
	private boolean mCoefsCalculated;
	
	/**
	 * ����������� ��� �������� X-���������� ������� � ������������� ����������.
	 * ������������� ���������� ������� ���������� � ��������� �� 0 �� 1.
	 */
	private float mCoefX;
	
	/**
	 * ����������� ��� �������� Y-���������� ������� � ������������� ����������.
	 * ������������� ���������� ������� ���������� � ��������� �� 0 �� 1.
	 */
	private float mCoefY;
	
	/**
	 * ������������ ��� ����������� ������������� X-����������.
	 */
	private float mMinX;
	
	/**
	 * ������������ ��� ����������� ������������� Y-����������.
	 */
	private float mMinY;
	
	/**
	 * ��������� ����� ������������.
	 */
	private float mStrokeSize;
	
	/**
	 * �������, ����������� ���������, ��������������� � ������������� ���-�� �������. 
	 */
	private Handler mHandlerDelayedAction = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			mFaceHelper.setFace(FaceType.ftOk);
		}
	};

	/**
	 * ����������� ������.
	 * @param imageView ������� ��� ������ ��������.
	 * @param faceHelper �������� ���������� �����.
	 */
	public FaceTouchHelper(final ImageView imageView, final FaceHelper faceHelper) {
		mImageView = imageView;
		mFaceHelper = faceHelper;
		
		mImageView.setOnTouchListener(this);
	}

	/**
	 * ���������� ������� ����.
	 * @param v �������� �������.
	 * @param event ����������� �������.
	 * @return true, ���� �������� ���������� � ����������.
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
					
					// ������� ���������� ����:
					mFaceHelper.setFace(FaceType.ftHappy);
					final int happyFaceDuration = 5000;
					mHandlerDelayedAction.sendEmptyMessageDelayed(0, happyFaceDuration);
					
					// �������� �������:
					BluetoothHelper.send(MessageConstant.WAG_TAIL_SIN);
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
	 * �������������� x-���������� ������� � ������������� ����������.
	 * @param x ����������.
	 * @return ������������� x-����������.
	 */
	private float getUniX(final float x) {
		if (mCoefsCalculated) {
			return mMinX + mCoefX * x;
		} else {
			return 0;
		}
	}
	
	/**
	 * �������������� y-���������� ������� � ������������� ����������.
	 * @param y ����������.
	 * @return ������������� y-����������.
	 */
	private float getUniY(final float y) {
		if (mCoefsCalculated) {
			return mMinY + mCoefY * y;
		} else {
			return 0;
		}
	}
	
	/**
	 * ����������� ���� ������������.
	 * @param uniX ����������.
	 * @param uniY ����������.
	 * @return true, ���� ���������� ������������� ���� ������������.
	 */
	private boolean isInHairArea(final float uniX, final float uniY) {
		final float foreHeadMaxY = 0.0926850957509333f;
		return (mFaceHelper.getFace() == FaceType.ftOk) && (uniY < foreHeadMaxY);		
	}
	
	/**
	 * ����������� ���� ����.
	 * @param uniX ����������.
	 * @param uniY ����������.
	 * @return true, ���� ���������� ������������� ���� ����.
	 */
	private boolean isNoseArea(final float uniX, final float uniY) {
		final float minX = 0.4f;
		final float maxX = 0.6f;
		final float minY = 0.4f;
		final float maxY = 0.6f;
		
		return (uniX >= minX) && (uniX < maxX) && (uniY >= minY) && (uniY < maxY);
	}
	
	/**
	 * �������� ��� ������� �� ���.
	 */
	private void pushNose() {
		SoundManager.playSound(SoundManager.BEEP, 1);
	}

	/**
	 * ����������� ���� ����.
	 * @param uniX ����������.
	 * @param uniY ����������.
	 * @return true, ���� ���������� ������������� ���� ����.
	 */
	private boolean isEyeArea(final float uniX, final float uniY) {
		// ����� ����:
		final float minLX = 0.2502606882168926f;
		final float maxLX = 0.3023983315954119f;
		final float minLY = 0.2040816326530612f;
		final float maxLY = 0.2968460111317254f;
		
		// ������ ����:
		final float minRX = 0.6256517205422315f;
		final float maxRX = 0.7299270072992701f;
		final float minRY = 0.1484230055658627f;
		final float maxRY = 0.3153988868274583f;

		boolean isLeftEyeArea = (uniX >= minLX) && (uniX < maxLX) && (uniY >= minLY) && (uniY < maxLY);
		boolean isRightEyeArea = (uniX >= minRX) && (uniX < maxRX) && (uniY >= minRY) && (uniY < maxRY);
		return (mFaceHelper.getFace() == FaceType.ftOk) && (isLeftEyeArea || isRightEyeArea);
	}
	
	/**
	 * �������� ��� ����� � ����.
	 */
	private void pushEye() {
		// ���� ���� �� 5 ������:
		mFaceHelper.setFace(FaceType.ftAngry);
		final int happyFaceDuration = 5000;
		mHandlerDelayedAction.sendEmptyMessageDelayed(0, happyFaceDuration);
		
		// �������� �������:
		final int vibrateDuration = 800;
		SoundManager.vibrate(vibrateDuration);

		// ��������� �����:
		BluetoothHelper.send(MessageConstant.FACETYPE_ANGRY_JUMP_BACK);
	}
}
