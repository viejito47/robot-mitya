package ru.dzakhov;

import android.util.Log;

/**
 * Класс для вывода в журнал трассировочных данных.
 * @author Дмитрий
 *
 */
public final class Logger {
	/**
	 * Признак вывода трассировочных данных в журнал.
	 */
	static final boolean ENABLE_LOG = true;
	
	/**
	 * Тег для фильтрации в журнале.
	 */
	static final String LOG_TAG = "RoboHead";
	
	/**
	 * Закрытый конструктор.
	 */
	private Logger() { }
	
	/**
	 * Вывод подробностей.
	 * @param msg текст сообщения.
	 */
	public static void v(final String msg) {
		if (ENABLE_LOG) {
			Log.v(LOG_TAG, msg);
		}
	}
	
	/**
	 * Вывод предупреждения.
	 * @param msg текст предупреждения.
	 */
	public static void w(final String msg) {
		if (ENABLE_LOG) {
			Log.w(LOG_TAG, msg);
		}
	}
	
	/**
	 * Вывод информации.
	 * @param msg информационное сообщение.
	 */
	public static void i(final String msg) {
		if (ENABLE_LOG) {
			Log.i(LOG_TAG, msg);
		}
	}
	
	/**
	 * Вывод отладочной информации.
	 * @param msg текст ссобщения.
	 */
	public static void d(final String msg) {
		if (ENABLE_LOG) {
			Log.d(LOG_TAG, msg);
		}
	}
	
	/**
	 * Фиксация ошибки.
	 * @param msg текст ошибки.
	 */
	public static void e(final String msg) {
		Log.e(LOG_TAG, msg);
	}
}
