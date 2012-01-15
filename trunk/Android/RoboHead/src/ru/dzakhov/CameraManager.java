package ru.dzakhov;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
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
	 * Порядковый номер камеры. Передаётся в Camera.Open.
	 */
	private int mCameraId = -1;
	
	/**
	 * Параметры камеры телефона.
	 */
	private Camera.Parameters mParameters = null;
	
	/**
	 * Признак включённой фары.
	 */
	private boolean mFlashlightOn = false;

	/**
	 * Сокет для передачи видеопотока.
	 */
	private DatagramSocket mDatagramSocket = null;
	
	/**
	 * Конструктор класа.
	 * @param activity активити для превью.
	 */
	public CameraManager(final Activity activity) {
		mActivity = activity;

		mSurfaceView = (SurfaceView) mActivity.findViewById(R.id.surfaceViewCamera);
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //Deprecated? Да без этой строки всё падает!
		
        CameraInfo cameraInfo = new CameraInfo();
		int numberOfCameras = Camera.getNumberOfCameras();
		mCameraId = -1;
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				mCameraId = i;
				break;
			}
		}
	}
	
	/**
	 * Открыть ресурс (камеру).
	 */
	public void open() {
		try {
			if (mDatagramSocket == null) {
				mDatagramSocket = new DatagramSocket(Settings.MEDIASOCKETPORT);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		if ((mCamera == null) && (mCameraId != -1)) {
			mCamera = Camera.open(mCameraId);
			//mCamera = Camera.open();
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
		
		if (mDatagramSocket != null) {
			mDatagramSocket.close();
			mDatagramSocket = null;
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
		if (mDatagramSocket == null) {
			return;
		}
		
		Camera.Parameters parameters = camera.getParameters();
		int format = parameters.getPreviewFormat();
		int width = parameters.getPreviewSize().width;
		int height = parameters.getPreviewSize().height;
		
		// Получить YUV изображение:
		YuvImage yuvImage = new YuvImage(data, format, width, height, null);
		// Получить Jpeg изображение:
	    Rect rect = new Rect(0, 0, width, height);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final int quality = 20;
        yuvImage.compressToJpeg(rect, quality, outputStream);
        byte[] jpegData = outputStream.toByteArray();

        int i = 0;
        int length = jpegData.length;
        final int packageSize = 512;
        while (i < length) {
            try {
            	DatagramPacket packet = new DatagramPacket(jpegData, i, packageSize, 
						new InetSocketAddress(Settings.CLIENTIP, Settings.MEDIASOCKETPORT));
				mDatagramSocket.send(packet);
			} catch (Exception e) {
				e.printStackTrace();
			}

            i += packageSize;
        }
//        Logger.d(String.valueOf(jpegData.length) + " "
//        		+ String.valueOf(format) + " " 
//        		+ String.valueOf(width) + "x"
//        		+ String.valueOf(height));
	}
}
