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
	 * Адрес клиента.
	 */
	public static final String CLIENTIP = "192.168.1.41";
	
	/**
	 * Порт сокета для приёма команд от Windows-приложения.
	 */
	public static final int COMMANDSOCKETPORT = 51974;
	
	/**
	 * Порт сокета для передачи видео в Windows-приложение.
	 */
	public static final int MEDIASOCKETPORT = 51973;
	
	/**
	 * Длина команд, передаваемых роботу.
	 */
	public static final int COMMANDLENGTH = 5;
	
	/**
	 * Длина сообщений, приходящих от робота.
	 */
	public static final int MESSAGELENGTH = 5;
	
	/**
	 * В приложении и команды роботу, и сообщения от робота обрабатываются в одном объекте Handle.
	 * Константа задаёт тип сообщения "комманда роботу" для обработчика Handle.
	 */
	public static final int COMMAND = 1;
	
	/**
	 * В приложении и команды роботу, и сообщения от робота обрабатываются в одном объекте Handle.
	 * Константа задаёт тип сообщения "сообщение от робота" для обработчика Handle.
	 */
	public static final int MESSAGE = 2;
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}
}
