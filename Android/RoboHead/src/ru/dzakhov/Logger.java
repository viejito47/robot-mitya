package ru.dzakhov;

import android.util.Log;

/**
 * ����� ��� ������ � ������ �������������� ������.
 * @author �������
 *
 */
public final class Logger {
	/**
	 * ������� ������ �������������� ������ � ������.
	 */
	static final boolean ENABLE_LOG = true;
	
	/**
	 * ��� ��� ���������� � �������.
	 */
	static final String LOG_TAG = "RoboHead";
	
	/**
	 * �������� �����������.
	 */
	private Logger() { }
	
	/**
	 * ����� ������������.
	 * @param msg ����� ���������.
	 */
	public static void v(final String msg) {
		if (ENABLE_LOG) {
			Log.v(LOG_TAG, msg);
		}
	}
	
	/**
	 * ����� ��������������.
	 * @param msg ����� ��������������.
	 */
	public static void w(final String msg) {
		if (ENABLE_LOG) {
			Log.w(LOG_TAG, msg);
		}
	}
	
	/**
	 * ����� ����������.
	 * @param msg �������������� ���������.
	 */
	public static void i(final String msg) {
		if (ENABLE_LOG) {
			Log.i(LOG_TAG, msg);
		}
	}
	
	/**
	 * ����� ���������� ����������.
	 * @param msg ����� ���������.
	 */
	public static void d(final String msg) {
		if (ENABLE_LOG) {
			Log.d(LOG_TAG, msg);
		}
	}
	
	/**
	 * �������� ������.
	 * @param msg ����� ������.
	 */
	public static void e(final String msg) {
		Log.e(LOG_TAG, msg);
	}
}
