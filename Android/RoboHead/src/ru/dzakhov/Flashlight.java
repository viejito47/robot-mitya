package ru.dzakhov;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * ����� ���������� ����� ������.
 * @author �������
 *
 */
public final class Flashlight {
	/**
	 * ������ ���������.
	 */
	private Camera camera = null;
	
	/**
	 * ��������� ������ ��������.
	 */
	private Camera.Parameters parameters = null;
	
	/**
	 * ������� ���������� ����.
	 */
	private boolean flashlightOn = false;

	/**
	 * ������� ������ (������).
	 */
	public void open() {
		if (camera == null) {
			camera = Camera.open();
	        if (camera != null) {
	        	parameters = camera.getParameters();
	        }
		}
	}

	/**
	 * ���������� ������ (������).
	 */
	public void release() {
		if (camera != null) {
			camera.release();
			camera = null;
			parameters = null;
		}
	}
	
	/**
	 * �������� ���� ������.
	 */
	public void turnLightOn() {
		if (parameters.getFlashMode() == null) {
			return;
		}
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(parameters);
	}

	/**
	 * ��������� ���� ������.
	 */
	public void turnLightOff() {
		if (parameters.getFlashMode() == null) {
			return;
		}
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameters);
	}

	/**
	 * �������� ��������� ���� ������ (���������, ���� �������� � ��������).
	 */
	public void switchLight() {
		if (parameters.getFlashMode() == null) {
			return;
		}
		String mode;
		if (flashlightOn) {
			mode = Parameters.FLASH_MODE_OFF;
		} else {
			mode = Parameters.FLASH_MODE_TORCH;
		}
		flashlightOn = !flashlightOn;
		parameters.setFlashMode(mode);
		camera.setParameters(parameters);
	}
}
