package ru.dzakhov;

import android.os.Handler;
import android.os.Message;

/**
 * Прослушиватель сообщений от устройства Open Accessory (USB Host Shield + Arduino).
 * @author Дмитрий
 *
 */
public class MessageAccessoryReceiver implements AccessoryListener {
	/**
	 * Ссылка на объект Handler из RoboHeadActivity. Им обрабатываются все поступающие
	 * в Android-приложение сообщения: и от Arduino, и от Windows.
	 */
	private Handler mHandler;
	
	/**
	 * Конструктор.
	 * @param handler ссылка на объект Handler из RoboHeadActivity.
	 */
	public MessageAccessoryReceiver(final Handler handler) {
		super();
		this.mHandler = handler;
	}
	
	/**
	 * Получено сообщение.
	 * @param buffer данные сообщения.
	 */
	public final void onAccessoryMessage(final byte[] buffer) {
		int i = 0;
		int bufferLength = buffer.length;
		
		while (i < bufferLength) {
			int currentBufferLength = bufferLength - i;

			if (currentBufferLength >= TcpServer.COMMANDLENGTH) {
				String command = "";
				for (int j = 0; j < TcpServer.COMMANDLENGTH; j++) {
					command += (char) buffer[i + j];
				}
				
				Message message = new Message();
				message.obj = command;
				mHandler.sendMessage(message);
			}
			
			i += TcpServer.COMMANDLENGTH;
		}
	}
}
