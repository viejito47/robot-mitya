package ru.dzakhov;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.os.Handler;
import android.os.Message;

/**
 * �����, ����������� ���� � ���������� ������ �� ������ Windows-����������.
 * @author �������
 *
 */
public class UdpMessageReceiver implements Runnable {
	/**
	 * ������ �� ������ Handler �� RoboHeadActivity. �� �������������� ��� �����������
	 * � Android-���������� ���������: � �� Arduino, � �� Windows.
	 */
	private Handler mHandler;
	
	/**
	 * ������� ��������� �������.
	 */
	private boolean mTerminate = false;
	
	/**
	 * ����������� ������.
	 * @param handler ������ �� ������ Handler �� RoboHeadActivity.
	 */
	public UdpMessageReceiver(final Handler handler) {
		super();
		mHandler = handler;
	}
	
	/**
	 * ��������� ������� (��������� �������� terminate ��� ������ �� ������ run()).
	 */
	public final void stopRunning() {
		mTerminate = true;
	}
	
	/**
	 * ���������� ���������� Runnable.
	 */
	public final void run() {
		while (true) {
			DatagramSocket datagramSocket = null;
			try {
				Logger.d("UdpMessageReceiver: Waiting for client to connect...");
				datagramSocket = new DatagramSocket(Settings.getMessageSocketPort());
				Logger.d("UdpMessageReceiver: Connected.");
				
				// ���-������� ��� ���������� �� ��������� ������������� ���������.
				MessageUniqueFilter messageUniqueFilter = new MessageUniqueFilter();				
				
				while (true) {
					byte[] receiveData = new byte[Settings.MESSAGE_LENGTH];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					datagramSocket.receive(receivePacket);
					String receivedText = new String(receivePacket.getData());
					
					if (messageUniqueFilter.isNewMessage(receivedText)) {
						// ������� ���������� � RoboHeadActivity:
						Message message = new Message();					
						message.obj = receivedText;
						mHandler.sendMessage(message);
						Logger.d("UdpMessageReceiver receive a command to process: " + receivedText);
					}
					
					if (mTerminate) {
						break;
					}
				} // read while cycle
			} catch (Exception e) {
				Logger.d("UdpMessageReceiver error (1): " + e.getLocalizedMessage());
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
			}
	
			if ((datagramSocket != null) && (!datagramSocket.isClosed())) {
				try {
					datagramSocket.close();
				} catch (Exception e2) {
					Logger.d("UdpMessageReceiver error (serverSocket.close()): " + e2.getLocalizedMessage());
				}
			}
			datagramSocket = null;
			
			if (mTerminate) {
				break;
			}
		} // receive while cycle
	} // run
} // class
