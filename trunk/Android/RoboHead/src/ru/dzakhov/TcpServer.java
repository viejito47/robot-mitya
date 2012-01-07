package ru.dzakhov;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;	 
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
	private Flashlight mFlashlight = new Flashlight();
	
	/**
	 * Ссылка на объект Handler из RoboHeadActivity. Им обрабатываются все поступающие
	 * в Android-приложение сообщения: и от Arduino, и от Windows.
	 */
	private Handler mHandler;
	
	/**
	 * Признак остановки сервиса сокета.
	 */
	private boolean mTerminate = false;

	/**
	 * При чтении команд из потока сокета часть команды может не успеть загрузиться на момент чтения
	 * и будет дочитана на следующих итерациях чтения. Поле используется для сохранения недочитанной
	 * команды.
	 */
	private String mPreviousCommandsRest = "";
	
	/**
	 * Конструктор класса.
	 * @param handler ссылка на объект Handler из RoboHeadActivity.
	 */
	public TcpServer(final Handler handler) {
		super();
		mHandler = handler;
	}
	
	/**
	 * Остановка сервиса сокета (установка признака terminate для выхода из метода run()).
	 */
	public final void stopRun() {
		mTerminate = true;
	}
	
	/**
	 * Реализация интерфейса Runnable.
	 */
	public final void run() {
		mFlashlight.open();
		try {
			while (true) {
				Socket socket = null;
				ServerSocket serverSocket = null;
				try {
					Logger.d("TcpServer: Waiting for client to connect...");
					serverSocket = new ServerSocket(Settings.COMMANDSOCKETPORT);
					socket = serverSocket.accept();
					Logger.d("TcpServer: Connected.");
					while (true) {
						// Получить список принятых на данный момент команд:
						List<String> commandList = getCommandsFromStream(socket.getInputStream());
						if (commandList.size() > 0) {
							Logger.d("commandList.size() = " + commandList.size());
						}						
						
						// Выполнить каждую принятую команду:
						for (int i = 0; i < commandList.size(); i++) {
							String command = commandList.get(i);
							
							// Эхо-возврат команды в Windows-приложение (для отладки):
							echoCommand(socket.getOutputStream(), command);
							
							// Команды работы с фарами робота (вспышка фото) выполняются здесь,
							// остальные команды передаются в RoboHeadActivity:
							if (command.equalsIgnoreCase("FL000")) {
								Logger.d("Выключить фары");
								mFlashlight.turnLightOff();
							} else if (command.equalsIgnoreCase("FL001")) {
								Logger.d("Включить фары");
								mFlashlight.turnLightOn();
							} else {
								Message message = new Message();
								message.arg1 = Settings.COMMAND;
								message.obj = command;
								mHandler.sendMessage(message);
							}
						}
						
						if (mTerminate) {
							break;
						}
					} // read while cycle
				} catch (Exception e) {
					Logger.d("TcpServer error (1): " + e.getLocalizedMessage());
				}
		
				if (!socket.isClosed()) {
					try {
						socket.close();
					} catch (Exception e2) {
						Logger.d("TcpServer error (socket.close()): " + e2.getLocalizedMessage());
					}
				}
				socket = null;
		
				if (!serverSocket.isClosed()) {
					try {
						serverSocket.close();
					} catch (Exception e2) {
						Logger.d("TcpServer error (serverSocket.close()): " + e2.getLocalizedMessage());
					}
				}
				serverSocket = null;
				
				if (mTerminate) {
					break;
				}
			} // accept while cycle
		} finally {
			mFlashlight.release();
		}
	} // run

	/**
	 * Извлекает из входного потока пятибайтовые команды, разделённые символами #13, #10.
	 * Команда, ещё неполностью попавшая во входной поток не возвращается в выходной список команд.
	 * Попавший в выходной поток кусок команды откладывается до следующего вызова метода.
	 * @param inputStream поток ввода (поступает из сокета).
	 * @return список команд роботу.
	 * @throws IOException ошибка чтения из потока ввода (из сокета).
	 */
	public final List<String> getCommandsFromStream(final InputStream inputStream) throws IOException {
		List<String> result = new ArrayList<String>();
		
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		while (true) {
			int bytesAvailable = dataInputStream.available(); 
			if (bytesAvailable <= 0) {
				break;
			}
			
			String command = dataInputStream.readLine();
			if (command == null) { // (входной поток пуст)
				break;
			} else {
				// Если на предыдущей итерации часть команды была прочитана, дочитываю команду:
				if (!mPreviousCommandsRest.equals("")) {
					command = mPreviousCommandsRest + command;
				}
				
				// Если не вся команда прочитана, сохраняю что прочиталось до следующей итерации,
				// иначе добавляю прочитанную команду в список:
				if (command.length() < Settings.COMMANDLENGTH) {
					mPreviousCommandsRest = command;
				} else {
					result.add(command);
					mPreviousCommandsRest = "";
				}
			}
		}
		return result;
	}
	
	/**
	 * Эхо-возврат обработанной комманды. Используется для отладки. 
	 * @param outputStream поток вывода сокета.
	 * @param command текст, выводимый в поток (обработанная команда).
	 * @throws IOException 
	 */
	private void echoCommand(final OutputStream outputStream, final String command) throws IOException {
		Logger.d("TcpServer: echo command " + command);

		String outputText = command + '\n' + '\r';

		byte[] buffer = new byte[outputText.length()];
		for (int i = 0; i < outputText.length(); i++) {
			buffer[i] = (byte) outputText.charAt(i);
		}		
		
		outputStream.write(buffer);
	}	
} // class
