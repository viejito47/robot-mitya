package ru.dzakhov;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

/**
 * Прослушиватель сообщений от устройства Open Accessory (USB Host Shield + Arduino).
 * @author Дмитрий
 *
 */
public final class MessageAccessoryReceiver implements AccessoryListener {
	/**
	 * Ссылка на объект Handler из RoboHeadActivity. Им обрабатываются все поступающие
	 * в Android-приложение сообщения: и от Arduino, и от Windows.
	 */
	private Handler mHandler;
	
	/**
	 * При чтении команд из входного буфера часть команды может не успеть загрузиться на момент чтения
	 * и будет дочитана на следующих итерациях чтения. Поле используется для сохранения недочитанной
	 * команды.
	 */
	private String mPreviousMessagesRest = "";

	/**
	 * Конструктор.
	 * @param handler ссылка на объект Handler из RoboHeadActivity.
	 */
	public MessageAccessoryReceiver(final Handler handler) {
		super();
		this.mHandler = handler;
	}
	
	/**
	 * Срабатывает при получении сообщения от робота (контроллера).
	 * @param buffer данные сообщения.
	 */
	public void onAccessoryMessage(final byte[] buffer) {
		if (mHandler == null) {
			return;
		}
		
		List<String> messages = getMessagesFromBuffer(buffer);
		for (int i = 0; i < messages.size(); i++) {
			Message message = new Message();
			message.obj = messages.get(i);
			mHandler.sendMessage(message);
		}
	}
	
	/**
	 * Извлекает из входного буфера пятибайтовые сообщения, поступившие от робота.
	 * Сообщение, ещё неполностью попавшее во входной буфер не возвращается в выходной список сообщений.
	 * Попавший в выходной поток кусок сообщения откладывается до следующего вызова метода.
	 * @param buffer данные сообщения.
	 * @return список сообщений от робота.
	 */
	public List<String> getMessagesFromBuffer(final byte[] buffer) {
		List<String> result = new ArrayList<String>();
		
		String bufferText = "";
		for (int i = 0; i < buffer.length; i++) {
			bufferText += (char) buffer[i];
		}
		bufferText = mPreviousMessagesRest + bufferText;
		mPreviousMessagesRest = "";
		
		int i = 0;
		int bufferLength = bufferText.length();
		
		while (i < bufferLength) {
			int currentBufferLength = bufferLength - i;

			if (currentBufferLength >= Settings.MESSAGE_LENGTH) {
				
				String message = "";
				for (int j = 0; j < Settings.MESSAGE_LENGTH; j++) {
					message += bufferText.charAt(i + j);
				}
				
				result.add(message);
			} else {
				mPreviousMessagesRest = "";
				for (int j = 0; j < currentBufferLength; j++) {
					mPreviousMessagesRest += bufferText.charAt(i + j);
				}
			}
			
			i += Settings.MESSAGE_LENGTH;
		}

		return result;
	}
}
