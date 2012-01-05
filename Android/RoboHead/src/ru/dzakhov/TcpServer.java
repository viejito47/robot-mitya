package ru.dzakhov;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader;	 
import java.io.OutputStream;
import java.net.ServerSocket;	 
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

/**
 * �����, ����������� ���� � ���������� ������ �� ������ Windows-����������.
 * @author �������
 *
 */
public class TcpServer implements Runnable {
	/**
	 * ������, ����������� ������ ������.
	 */
	private Flashlight flashlight = new Flashlight();
	
	/**
	 * ������ �� ������ Handler �� RoboHeadActivity. �� �������������� ��� �����������
	 * � Android-���������� ���������: � �� Arduino, � �� Windows.
	 */
	private Handler mHandler;
	
	/**
	 * ������� ��������� ������� ������.
	 */
	private boolean terminate = false;
	
	/**
	 * ����� ���������� ������.
	 */
	//public static final String SERVERIP = "192.168.1.1";
	public static final String SERVERIP = "localhost";
	
	/**
	 * ���� ������.
	 */
	public static final int SERVERPORT = 51974;
	
	/**
	 * ����� ���������/������.
	 */
	public static final int COMMANDLENGTH = 5;
	
	/**
	 * ����������� ������.
	 * @param handler ������ �� ������ Handler �� RoboHeadActivity.
	 */
	public TcpServer(final Handler handler) {
		super();
		mHandler = handler;
	}
	
	/**
	 * ��������� ������� ������ (��������� �������� terminate ��� ������ �� ������ run()).
	 */
	public final void stopRun() {
		terminate = true;
	}
	
	/**
	 * ���������� ���������� Runnable.
	 */
	public final void run() {
		flashlight.open();
		try {
			while (true) {
				Socket socket = null;
				ServerSocket serverSocket = null;
				try {
					Logger.d("TcpServer: Waiting for client to connect...");
					serverSocket = new ServerSocket(SERVERPORT);
					socket = serverSocket.accept();
					Logger.d("TcpServer: Connected.");
					String previousCommandsRest = "";
					while (true) {              
						//Logger.d("TcpServer: Receiving...");
		
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String commands = previousCommandsRest + in.readLine();
						
						String temp = in.readLine();
						if (temp != null) {
							Logger.d("TcpServer: ********** THE PROBLEM IS HERE: " + temp);
						}
						
						previousCommandsRest = "";
						int commandsLength = commands.length();
						Logger.d("TcpServer will execute: '" + commands + "'");
		
						int i = 0;
						while (i < commandsLength) {
							int currentCommandsLength = commandsLength - i;
	
							if (currentCommandsLength >= TcpServer.COMMANDLENGTH) {
								String command = "";
								for (int j = 0; j < TcpServer.COMMANDLENGTH; j++) {
									command += commands.charAt(i + j);
								}
	
								echoCommand(socket.getOutputStream(), command);
								
								if (command.equalsIgnoreCase("FL000")) {
									Logger.d("��������� ����");
									flashlight.turnLightOff();
								} else if (command.equalsIgnoreCase("FL001")) {
									Logger.d("�������� ����");
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
						
						if (terminate) {
							break;
						}
					} // while (true)
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
				
				if (terminate) {
					break;
				}
			} // while (true)
		} finally {
			flashlight.release();
		}
	} // run

	/**
	 * ���-������� ������������ ��������. ������������ ��� �������. 
	 * @param outputStream ����� ������ ������.
	 * @param command �����, ��������� � ����� (������������ �������).
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
