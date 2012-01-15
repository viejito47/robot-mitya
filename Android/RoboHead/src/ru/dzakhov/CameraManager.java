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
	 * ���������� ����� ������. ��������� � Camera.Open.
	 */
	private int mCameraId = -1;
	
	/**
	 * ��������� ������ ��������.
	 */
	private Camera.Parameters mParameters = null;
	
	/**
	 * ������� ���������� ����.
	 */
	private boolean mFlashlightOn = false;

	/**
	 * ����� ��� �������� �����������.
	 */
	private DatagramSocket mDatagramSocket = null;
	
	/**
	 * ����������� �����.
	 * @param activity �������� ��� ������.
	 */
	public CameraManager(final Activity activity) {
		mActivity = activity;

		mSurfaceView = (SurfaceView) mActivity.findViewById(R.id.surfaceViewCamera);
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //Deprecated? �� ��� ���� ������ �� ������!
		
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
	 * ������� ������ (������).
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
		
		if (mDatagramSocket != null) {
			mDatagramSocket.close();
			mDatagramSocket = null;
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
		if (mDatagramSocket == null) {
			return;
		}
		
		Camera.Parameters parameters = camera.getParameters();
		int format = parameters.getPreviewFormat();
		int width = parameters.getPreviewSize().width;
		int height = parameters.getPreviewSize().height;
		
		// �������� YUV �����������:
		YuvImage yuvImage = new YuvImage(data, format, width, height, null);
		// �������� Jpeg �����������:
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
