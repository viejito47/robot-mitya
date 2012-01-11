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
 * Класс управления фарой робота.
 * @author Дмитрий
 *
 */
public final class CameraManager implements SurfaceHolder.Callback, Camera.PreviewCallback {
	/**
	 * Активити для превью.
	 */
	private Activity mActivity;
	
	/**
	 * Элемент GUI, отображающий изобрадение с камеры и реализующий интерфейс SurfaceHolder.
	 */
	private SurfaceView mSurfaceView;
	
	/**
	 * Камера телефона.
	 */
	private Camera mCamera = null;
	
	/**
	 * Параметры камеры телефона.
	 */
	private Camera.Parameters mParameters = null;
	
	/**
	 * Признак включённой фары.
	 */
	private boolean mFlashlightOn = false;

	/**
	 * Конструктор класа.
	 * @param activity активити для превью.
	 */
	public CameraManager(final Activity activity) {
		mActivity = activity;

		mSurfaceView = (SurfaceView) mActivity.findViewById(R.id.surfaceViewCamera);
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //TODO Deprecated? Да без этой строки всё падает!
	}
	
	/**
	 * Открыть ресурс (камеру).
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
	 * Освободить ресурс (камеру).
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
	 * Включить фары робота.
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
	 * Выключить фары робота.
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
	 * Изменить состояние фары робота (выключить, если включено и наоборот).
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
	 * Метод интерфейса SurfaceHolder.Callback. 
	 * @param holder интерфейс управления окном с изображением от камеры в котором изменился surface.
	 * @param format новый формат пикселей.
	 * @param width новая ширина surface.
	 * @param height новая высота surface.
	 */
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
	}

	/**
	 * Метод интерфейса SurfaceHolder.Callback.
	 * @param holder интерфейс управления окном с изображением от камеры в котором создан surface.
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
	 * Метод интерфейса SurfaceHolder.Callback. 
	 * @param holder интерфейс управления окном с изображением от камеры в котором удалён surface.
	 */
	public void surfaceDestroyed(final SurfaceHolder holder) {
	}

	/**
	 * Метод интерфейса Camera.PreviewCallback.
	 * @param data видеопоток от камеры.
	 * @param camera камера.
	 */
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		// TODO Вот тут мой поток
	}
}
