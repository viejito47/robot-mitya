package ru.dzakhov;

import android.os.Handler;
import android.os.Message;

/**
 * �������������� ��������� �� ���������� Open Accessory (USB Host Shield + Arduino).
 * @author �������
 *
 */
public class MessageAccessoryReceiver implements AccessoryListener {
	/**
	 * ������ �� ������ Handler �� RoboHeadActivity. �� �������������� ��� �����������
	 * � Android-���������� ���������: � �� Arduino, � �� Windows.
	 */
	private Handler mHandler;
	
	/**
	 * �����������.
	 * @param handler ������ �� ������ Handler �� RoboHeadActivity.
	 */
	public MessageAccessoryReceiver(final Handler handler) {
		super();
		this.mHandler = handler;
	}
	
	/**
	 * �������� ���������.
	 * @param buffer ������ ���������.
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
