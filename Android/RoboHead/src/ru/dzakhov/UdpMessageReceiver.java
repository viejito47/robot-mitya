package ru.dzakhov;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.os.Handler;
import android.os.Message;

/**
 * �����, ����������� ���� � ���������� ������ �� ������ Windows-����������.
 * @author �������
 *
 */
public final class UdpMessageReceiver extends Thread {
	/**
	 * ������ �� ������ Handler �� RoboHeadActivity. �� �������������� ��� �����������
	 * � Android-���������� ���������: � �� Arduino, � �� Windows.
	 */
	private Handler mHandler;
	
	/**
	 * ����������� ������.
	 * @param handler ������ �� ������ Handler �� RoboHeadActivity.
	 */
	public UdpMessageReceiver(final Handler handler) {
		super();
		mHandler = handler;
	}
	
	/**
	 * ���������� ���������� Runnable.
	 */
	public void run() {
		DatagramSocket datagramSocket = null;
		try {
			final int receiveTimeout = 1000;
			datagramSocket = new DatagramSocket(Settings.getUdpReceivePort());
			datagramSocket.setSoTimeout(receiveTimeout);
		} catch (SocketException e) {
			Logger.e("UdpMessageReceiver error: " + e.getLocalizedMessage());
			return;
		}
		Logger.d("UdpMessageReceiver: started");
		
		while (!Thread.currentThread().isInterrupted()) {
			byte[] receiveData = new byte[Settings.MESSAGE_LENGTH];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				datagramSocket.receive(receivePacket);
			} catch (IOException e) {
				continue;
			}
			String receivedText = new String(receivePacket.getData());
			
			if (MessageUniqueFilter.isNewMessage(receivedText)) {
				// ������� ���������� � RoboHeadActivity:
				Message message = new Message();					
				message.obj = receivedText;
				mHandler.sendMessage(message);
				Logger.d("UdpMessageReceiver has received command to process: " + receivedText);
			}
		} // read while cycle

		if ((datagramSocket != null) && (!datagramSocket.isClosed())) {
			try {
				datagramSocket.close();
			} catch (Exception e) {
				Logger.e("UdpMessageReceiver error: " + e.getLocalizedMessage());
			}
		}
		datagramSocket = null;
		
		Logger.d("UdpMessageReceiver: stopped");
	} // run
} // class
