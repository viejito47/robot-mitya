package ru.dzakhov;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * Класс управления фарой робота.
 * @author Дмитрий
 *
 */
public final class Flashlight {
	/**
	 * Камера ателефона.
	 */
	private Camera camera = null;
	
	/**
	 * Параметры камеры телефона.
	 */
	private Camera.Parameters parameters = null;
	
	/**
	 * Признак включённой фары.
	 */
	private boolean flashlightOn = false;

	/**
	 * Открыть ресурс (камеру).
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
	 * Освободить ресурс (камеру).
	 */
	public void release() {
		if (camera != null) {
			camera.release();
			camera = null;
			parameters = null;
		}
	}
	
	/**
	 * Включить фары робота.
	 */
	public void turnLightOn() {
		if (parameters.getFlashMode() == null) {
			return;
		}
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(parameters);
	}

	/**
	 * Выключить фары робота.
	 */
	public void turnLightOff() {
		if (parameters.getFlashMode() == null) {
			return;
		}
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(parameters);
	}

	/**
	 * Изменить состояние фары робота (выключить, если включено и наоборот).
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
