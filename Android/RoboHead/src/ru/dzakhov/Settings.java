package ru.dzakhov;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Активити настроек приложения.
 * @author Дмитрий
 *
 */
public class Settings extends PreferenceActivity {
	/**
	 * Адрес сервера.
	 */
	public static final String SERVERIP = "localhost";
	
	/**
	 * Порт сокета для приёма сообщений от ПК.
	 */
	public static final int MESSAGESOCKETPORT = 51974;
	
	/**
	 * Порт сокета для передачи видео в Windows-приложение.
	 */
	public static final int MEDIASOCKETPORT = 51973;
	
	/**
	 * Длина сообщений, между уровнями ПК, телефон, контроллер.
	 */
	public static final int MESSAGE_LENGTH = 5;
	
	/**
	 * Длина идентификатора в сообщении.
	 */
	public static final int MESSAGE_IDENTIFIER_LENGTH = 1;
	
	/**
	 * Длина значения в сообщении.
	 */
	public static final int MESSAGE_VALUE_LENGTH = 4;
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}
}
