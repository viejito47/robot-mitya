package ru.dzakhov;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

/**
 * ����� ���������� ����� ������.
 * @author �������
 *
 */
public final class CameraManager implements SurfaceHolder.Callback, Camera.PreviewCallback {
	/**
	 * �������� ��� ������.
	 */
	private Activity mActivity;
	
	/**
	 * ������� GUI, ������������ ����������� � ������ � ����������� ��������� SurfaceHolder.
	 */
	private SurfaceView mSurfaceView;
	
	/**
	 * ������ ��������.
	 */
	private Camera mCamera = null;
	
	/**
	 * ��������� ������ ��������.
	 */
	private Camera.Parameters mParameters = null;
	
	/**
	 * ������� ���������� ����.
	 */
	private boolean mFlashlightOn = false;

	/**
	 * ����������� �����.
	 * @param activity �������� ��� ������.
	 */
	public CameraManager(final Activity activity) {
		mActivity = activity;

		mSurfaceView = (SurfaceView) mActivity.findViewById(R.id.surfaceViewCamera);
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //TODO Deprecated? �� ��� ���� ������ �� ������!
	}
	
	/**
	 * ������� ������ (������).
	 */
	public void open() {
		if (mCamera == null) {
			mCamera = Camera.open();
	        if (mCamera != null) {
	        	mParameters = mCamera.getParameters();
	        }
		}
	}

	/**
	 * ���������� ������ (������).
	 */
	public void release() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mParameters = null;
		}
	}
	
	/**
	 * �������� ���� ������.
	 */
	public void turnLightOn() {
		if (mParameters == null) {
			return;
		}
		if (mParameters.getFlashMode() == null) {
			return;
		}
		mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(mParameters);
	}

	/**
	 * ��������� ���� ������.
	 */
	public void turnLightOff() {
		if (mParameters == null) {
			return;
		}
		if (mParameters.getFlashMode() == null) {
			return;
		}
		mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		mCamera.setParameters(mParameters);
	}

	/**
	 * �������� ��������� ���� ������ (���������, ���� �������� � ��������).
	 */
	public void switchLight() {
		if (mParameters == null) {
			return;
		}
		if (mParameters.getFlashMode() == null) {
			return;
		}
		String mode;
		if (mFlashlightOn) {
			mode = Parameters.FLASH_MODE_OFF;
		} else {
			mode = Parameters.FLASH_MODE_TORCH;
		}
		mFlashlightOn = !mFlashlightOn;
		mParameters.setFlashMode(mode);
		mCamera.setParameters(mParameters);
	}

	/**
	 * ����� ���������� SurfaceHolder.Callback. 
	 * @param holder ��������� ���������� ����� � ������������ �� ������ � ������� ��������� surface.
	 * @param format ����� ������ ��������.
	 * @param width ����� ������ surface.
	 * @param height ����� ������ surface.
	 */
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
	}

	/**
	 * ����� ���������� SurfaceHolder.Callback.
	 * @param holder ��������� ���������� ����� � ������������ �� ������ � ������� ������ surface.
	 */
	public void surfaceCreated(final SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setPreviewCallback(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Size previewSize = mCamera.getParameters().getPreviewSize();
		float aspect = (float) previewSize.width / previewSize.height;

		int previewSurfaceWidth = mSurfaceView.getWidth();		
		LayoutParams layoutParams = mSurfaceView.getLayoutParams();
		mCamera.setDisplayOrientation(0);
		layoutParams.width = previewSurfaceWidth;
		layoutParams.height = (int) (previewSurfaceWidth / aspect);
		mSurfaceView.setLayoutParams(layoutParams);
		mCamera.startPreview();
	}

	/**
	 * ����� ���������� SurfaceHolder.Callback. 
	 * @param holder ��������� ���������� ����� � ������������ �� ������ � ������� ����� surface.
	 */
	public void surfaceDestroyed(final SurfaceHolder holder) {
	}

	/**
	 * ����� ���������� Camera.PreviewCallback.
	 * @param data ���������� �� ������.
	 * @param camera ������.
	 */
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		// TODO ��� ��� ��� �����
	}
}
