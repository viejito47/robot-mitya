package ru.dzakhov;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.ParcelFileDescriptor;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

/**
 * Класс, предоставляющий базовую функциональность для взаимодействия с устройствами
 * Open Accessory, такую как отрытие соединения, завершение, чтение, запись.
 * @author Дмитрий (в том числе)
 *
 */
public class Accessory  implements Runnable {
	/**
	 * Входной поток.
	 */
	private FileInputStream mInputStream;
	
	/**
	 * Выходной поток.
	 */
	private FileOutputStream mOutputStream;
	
	/**
	 * Дескриптор узла ввода/вывода.
	 */
	private ParcelFileDescriptor mFileDescriptor;
	
	/**
	 * Менеджер USB.
	 */
	private UsbManager mUsbManager;
	
	/**
	 * Прослушиватель Accessory.
	 */
	private AccessoryListener mListener;

	/**
	 * Признак работы нити.
	 */
	private boolean mThreadRunning;
	
	/**
	 * Конструктор класса.
	 * @param usbManager менеджер USB.
	 */
	public Accessory(final UsbManager usbManager) {
		mUsbManager = usbManager;
	}

	/**
	 * Открытие устройства Open Accessory.
	 * @param accessory UsbAccessory
	 * @return true в случае успеха.
	 */
	public final boolean open(final UsbAccessory accessory) {
		boolean result = false;
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "RoboHead");
			thread.start();
			Logger.d("accessory opened");
			result = true;
		} else {
			Logger.d("accessory open fail");
		}
		return result;
	}

	/**
	 * Завершение работы устройства Open Accessory.
	 */
	public final void close() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mFileDescriptor = null;
			mOutputStream = null;
			mThreadRunning = false;
		}
	}

	/**
	 * Проверка соединения.
	 * @return true, если есть соединение.
	 */
	public final boolean isConnected() {
		return mInputStream != null && mOutputStream != null;
	}
	

	/**
	 * Установка прослушивателя ADK.
	 * @param listner прослушиватель.
	 */
	public final void setListener(final AccessoryListener listner) {
		mListener = listner;
	}

	/**
	 * Удаление прослушивателя ADK.
	 */
	public final void removeListener() {
		mThreadRunning = false;
	}
	
	/**
	 * Метод нити для получения сообщений от ADK.
	 */
	public final void run() {
		int length = 0;
		final int bufferSize = 16384;
		byte[] buffer = new byte[bufferSize];
		byte[] pass;

		mThreadRunning = true;
		while ((length >= 0) && mThreadRunning) {
			try {
				length = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}

			pass = new byte[length];
			System.arraycopy(buffer, 0, pass, 0, length);
			if (mListener != null) {
				mListener.onAccessoryMessage(pass);
			}
		}
		mListener = null;
		mInputStream = null;
		mThreadRunning = false;
	}

	/**
	 * Запись в ADK.
	 * @param data передаваемые в ADK данные.
	 */
	public final void write(final byte[] data) {
		if (mOutputStream != null) {
			try {
				mOutputStream.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
