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
 * �����, ����������� ���� � ���������� ������ �� ������ Windows-����������.
 * @author �������
 *
 */
public class TcpServer implements Runnable {
	/**
	 * ������, ����������� ������ ������.
	 */
	private Flashlight mFlashlight = new Flashlight();
	
	/**
	 * ������ �� ������ Handler �� RoboHeadActivity. �� �������������� ��� �����������
	 * � Android-���������� ���������: � �� Arduino, � �� Windows.
	 */
	private Handler mHandler;
	
	/**
	 * ������� ��������� ������� ������.
	 */
	private boolean mTerminate = false;

	/**
	 * ��� ������ ������ �� ������ ������ ����� ������� ����� �� ������ ����������� �� ������ ������
	 * � ����� �������� �� ��������� ��������� ������. ���� ������������ ��� ���������� ������������
	 * �������.
	 */
	private String mPreviousCommandsRest = "";
	
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
		mTerminate = true;
	}
	
	/**
	 * ���������� ���������� Runnable.
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
						// �������� ������ �������� �� ������ ������ ������:
						List<String> commandList = getCommandsFromStream(socket.getInputStream());
						if (commandList.size() > 0) {
							Logger.d("commandList.size() = " + commandList.size());
						}						
						
						// ��������� ������ �������� �������:
						for (int i = 0; i < commandList.size(); i++) {
							String command = commandList.get(i);
							
							// ���-������� ������� � Windows-���������� (��� �������):
							echoCommand(socket.getOutputStream(), command);
							
							// ������� ������ � ������ ������ (������� ����) ����������� �����,
							// ��������� ������� ���������� � RoboHeadActivity:
							if (command.equalsIgnoreCase("FL000")) {
								Logger.d("��������� ����");
								mFlashlight.turnLightOff();
							} else if (command.equalsIgnoreCase("FL001")) {
								Logger.d("�������� ����");
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
	 * ��������� �� �������� ������ ������������ �������, ���������� ��������� #13, #10.
	 * �������, ��� ����������� �������� �� ������� ����� �� ������������ � �������� ������ ������.
	 * �������� � �������� ����� ����� ������� ������������� �� ���������� ������ ������.
	 * @param inputStream ����� ����� (��������� �� ������).
	 * @return ������ ������ ������.
	 * @throws IOException ������ ������ �� ������ ����� (�� ������).
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
			if (command == null) { // (������� ����� ����)
				break;
			} else {
				// ���� �� ���������� �������� ����� ������� ���� ���������, ��������� �������:
				if (!mPreviousCommandsRest.equals("")) {
					command = mPreviousCommandsRest + command;
				}
				
				// ���� �� ��� ������� ���������, �������� ��� ����������� �� ��������� ��������,
				// ����� �������� ����������� ������� � ������:
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
