package ru.dzakhov;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

/**
 * �������������� ��������� �� ���������� Open Accessory (USB Host Shield + Arduino).
 * @author �������
 *
 */
public final class MessageAccessoryReceiver implements AccessoryListener {
	/**
	 * ������ �� ������ Handler �� RoboHeadActivity. �� �������������� ��� �����������
	 * � Android-���������� ���������: � �� Arduino, � �� Windows.
	 */
	private Handler mHandler;
	
	/**
	 * ��� ������ ������ �� �������� ������ ����� ������� ����� �� ������ ����������� �� ������ ������
	 * � ����� �������� �� ��������� ��������� ������. ���� ������������ ��� ���������� ������������
	 * �������.
	 */
	private String mPreviousMessagesRest = "";

	/**
	 * �����������.
	 * @param handler ������ �� ������ Handler �� RoboHeadActivity.
	 */
	public MessageAccessoryReceiver(final Handler handler) {
		super();
		this.mHandler = handler;
	}
	
	/**
	 * ����������� ��� ��������� ��������� �� ������ (�����������).
	 * @param buffer ������ ���������.
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
	 * ��������� �� �������� ������ ������������ ���������, ����������� �� ������.
	 * ���������, ��� ����������� �������� �� ������� ����� �� ������������ � �������� ������ ���������.
	 * �������� � �������� ����� ����� ��������� ������������� �� ���������� ������ ������.
	 * @param buffer ������ ���������.
	 * @return ������ ��������� �� ������.
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
