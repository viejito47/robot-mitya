package ru.dzakhov;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.os.Handler;
import android.os.Message;

/**
 * Класс, реализующий приём и исполнение команд от уровня Windows-приложения.
 * @author Дмитрий
 *
 */
public final class UdpMessageReceiver extends Thread {
	/**
	 * Ссылка на объект Handler из RoboHeadActivity. Им обрабатываются все поступающие
	 * в Android-приложение сообщения: и от Arduino, и от Windows.
	 */
	private Handler mHandler;
	
	/**
	 * Конструктор класса.
	 * @param handler ссылка на объект Handler из RoboHeadActivity.
	 */
	public UdpMessageReceiver(final Handler handler) {
		super();
		mHandler = handler;
	}
	
	/**
	 * Реализация интерфейса Runnable.
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
				// Команды передаются в RoboHeadActivity:
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
