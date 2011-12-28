package ru.dzakhov;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.ParcelFileDescriptor;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

/**
 * �����, ��������������� ������� ���������������� ��� �������������� � ������������
 * Open Accessory, ����� ��� ������� ����������, ����������, ������, ������.
 * @author ������� (� ��� �����)
 *
 */
public class Accessory  implements Runnable {
	/**
	 * ������� �����.
	 */
	private FileInputStream mInputStream;
	
	/**
	 * �������� �����.
	 */
	private FileOutputStream mOutputStream;
	
	/**
	 * ���������� ���� �����/������.
	 */
	private ParcelFileDescriptor mFileDescriptor;
	
	/**
	 * �������� USB.
	 */
	private UsbManager mUsbManager;
	
	/**
	 * �������������� Accessory.
	 */
	private AccessoryListener mListener;

	/**
	 * ������� ������ ����.
	 */
	private boolean mThreadRunning;
	
	/**
	 * ����������� ������.
	 * @param usbManager �������� USB.
	 */
	public Accessory(final UsbManager usbManager) {
		mUsbManager = usbManager;
	}

	/**
	 * �������� ���������� Open Accessory.
	 * @param accessory UsbAccessory
	 * @return true � ������ ������.
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
	 * ���������� ������ ���������� Open Accessory.
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
	 * �������� ����������.
	 * @return true, ���� ���� ����������.
	 */
	public final boolean isConnected() {
		return mInputStream != null && mOutputStream != null;
	}
	

	/**
	 * ��������� �������������� ADK.
	 * @param listner ��������������.
	 */
	public final void setListener(final AccessoryListener listner) {
		mListener = listner;
	}

	/**
	 * �������� �������������� ADK.
	 */
	public final void removeListener() {
		mThreadRunning = false;
	}
	
	/**
	 * ����� ���� ��� ��������� ��������� �� ADK.
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
	 * ������ � ADK.
	 * @param data ������������ � ADK ������.
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
