package ru.dzakhov;

import java.io.BufferedReader; 
import java.io.InputStreamReader;	 
import java.net.ServerSocket;	 
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

/**
 * Класс, реализующий приём и исполнение команд от уровня Windows-приложения.
 * @author Дмитрий
 *
 */
public class TcpServer implements Runnable {
	/**
	 * Объект, управляющий фарами робота.
	 */
	private Flashlight flashlight = new Flashlight();
	
	/**
	 * Ссылка на объект Handler из RoboHeadActivity. Им обрабатываются все поступающие
	 * в Android-приложение сообщения: и от Arduino, и от Windows.
	 */
	private Handler mHandler;
	
	/**
	 * Адрес серверного сокета.
	 */
	//public static final String SERVERIP = "192.168.1.1";
	public static final String SERVERIP = "localhost";
	
	/**
	 * Порт сокета.
	 */
	public static final int SERVERPORT = 51974;
	
	/**
	 * Длина сообщений/команд.
	 */
	public static final int COMMANDLENGTH = 5;
	
	/**
	 * Конструктор класса.
	 * @param handler ссылка на объект Handler из RoboHeadActivity.
	 */
	public TcpServer(final Handler handler) {
		super();
		mHandler = handler;
	}
	
	/**
	 * Реализация интерфейса Runnable.
	 */
	public final void run() {
		flashlight.open();
		try {
			Socket client = null;
			ServerSocket serverSocket = null;
			try {
				Logger.d("TcpServer: Waiting...");
				serverSocket = new ServerSocket(SERVERPORT);
				client = serverSocket.accept();
				String previousCommandsRest = "";
				while (true) {              
					//Logger.d("TcpServer: Receiving...");
	
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String commands = previousCommandsRest + in.readLine();
					previousCommandsRest = "";
					int commandsLength = commands.length();
					Logger.d("TcpServer исполнит: '" + commands + "'");
	
					int i = 0;
					while (i < commandsLength) {
						int currentCommandsLength = commandsLength - i;

						if (currentCommandsLength >= TcpServer.COMMANDLENGTH) {
							String command = "";
							for (int j = 0; j < TcpServer.COMMANDLENGTH; j++) {
								command += commands.charAt(i + j);
							}
	
							if (command.equalsIgnoreCase("FL000")) {
								Logger.d("Выключить фары");
								flashlight.turnLightOff();
							} else if (command.equalsIgnoreCase("FL001")) {
								Logger.d("Включить фары");
								flashlight.turnLightOn();
							} else {
								Message message = new Message();
								message.obj = command;
								mHandler.sendMessage(message);
							}
							
							i += TcpServer.COMMANDLENGTH;
						} else {
							previousCommandsRest = "";
							for (int j = i; j < commandsLength; j++) {
								previousCommandsRest += commands.charAt(i + j);
							}
							i = commandsLength;
						}
					}					
				}
			} catch (Exception e) {
				Logger.d("TcpServer error: " + e.getLocalizedMessage());
			}
	
			if (!client.isClosed()) {
				try {
					client.close();
				} catch (Exception e2) {
					Logger.d("TcpServer error: " + e2.getLocalizedMessage());
				}
			}
			if (client.isClosed()) {
				Logger.d("TcpServer: socket is closed");
			} else {
				Logger.d("TcpServer: socket is not closed");
			}
	
			if (!serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (Exception e2) {
					Logger.d("TcpServer error: " + e2.getLocalizedMessage());
				}
			}
			if (serverSocket.isClosed()) {
				Logger.d("TcpServer: socket is closed");
			} else {
				Logger.d("TcpServer: socket is not closed");
			}
		} finally {
			flashlight.release();
		}
	} // run
} // class
