package ru.dzakhov;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * ��������������� ����� ��� ����������� ����� ����� Android-����������� � Arduino-������� ������.
 * @author ������� ������
 *
 */
public final class BluetoothHelper {
	/**
	 * ��������� ��� ����������� ������� ��������� Bluetooth.
	 */
	public static final int REQUEST_ENABLE_BT = 1;
	
	/**
	 * MAC-����� bluetooth-������, ������������� � ����������� ������.
	 */
	// private static final String ROBOBODY_MAC = "00:12:03:31:01:22";
	
	/**
	 * Handler �������������� ��� ��������� � Android-���������� ������. ��������� ������ � ������ Initialize.
	 */
	private static Handler mMessageHandler = null;
	
	/**
	 * Bluetooth-������� ��������.
	 */
	private static BluetoothAdapter mBluetoothAdapter = null;
	
	/**
	 * Bluetooth-������, ������������ � �����������.
	 */
	private static BluetoothDevice mBluetoothDevice = null;
	
	/**
	 * �����.
	 */
	private static BluetoothSocket mBluetoothSocket = null;
	
	/**
	 * ������� ����� ���������. � ���� ��������� ��������� �� ����������� ������.
	 */
	private static InputStream mInputStream = null;
	
	/**
	 * �������� ����� ���������. � ���� ���������� ��������� ��� ����������� ������.
	 */
	private static OutputStream mOutputStream = null;
	
	/**
	 * ������� ���������� Bluetooth-��������.
	 */
	private static boolean mBluetoothAdapterIsEnabled = false;
	
	/**
	 * ������� ������������ ���������� � Bluetooth-������� ����������� ������.
	 */
	private static boolean mConnected = false;
	
	/**
	 * ������� ����, ��� ���������� ������ ��������. ��������������� ��� ��������� ������� ������������ ���������� � 
	 * �������� bluetooth-������� ������.
	 */
	//private static boolean mControllerIsTurnedOff = false;
	
	/**
	 * ��� ������ ���������, ����������� �� ����������� ������ ��������� ��������� ����� ���� ������� ��� �� ���������.
	 * ����� �������� �������� ����� ��������� ������� ���������� � ��� ����. ��� ��������� ��������� ������ ���������
	 * �� �������� ��� � �������� ������.
	 */
	private static String mPreviousMessagesRest = "";
	
	/**
	 * ����� ����� �����������, ������� ����������� ��������.
	 */
	private BluetoothHelper() {		
	}

	/**
	 * ��������� ������������� bluetooth-�������� ��������. ������ ���������� ������ ���� ���, ��������, � onCreate ������� ��������.
	 * @param parentActivity ������������ ��������.
	 * @param messageHandler handler, �������������� ��� ��������� ������.
	 */
	public static void initialize(final Activity parentActivity, final Handler messageHandler) {
		mMessageHandler = messageHandler;
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(parentActivity, "� ���������� ����������� Bluetooth-�������", Toast.LENGTH_LONG).show();
			return;
		}

		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapterIsEnabled = true;
		} else {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			parentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	/**
	 * ���������� � bluetooth-������� ������. ���� �� ����������� ������ � �������� �� ��������� ���������.
	 * @return true, ���� ���������� ���������.
	 */
	public static boolean connect() {
		if (!mBluetoothAdapterIsEnabled) {
			if (mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapterIsEnabled = true;
			} else {
				return false;
			}
		}
		
	    new Thread(new Runnable() {
	        public void run() {
	        	//mBluetoothAdapter.cancelDiscovery();
	        	
				while (true) {
	    			if (!mConnected) { 
	    			    // ��� - ������������ ����� ������������ ��������, �� ��������� ������ ���� ��������� � ������.
	    				// createRfcommSocketToServiceRecord(), � ���������, �� ��������
	    				try {
	    					mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(Settings.getRoboBodyMac());
	    					Method m = mBluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
	    					mBluetoothSocket = (BluetoothSocket) m.invoke(mBluetoothDevice, Integer.valueOf(1));
	    					
	    					// ���� ���������� ������ ����������, connect() �������� ���������� � �������� ������ 
	    					// ����������, �������� �� ��������� �����!
	    					mBluetoothSocket.connect();
	    					
	    					mInputStream = mBluetoothSocket.getInputStream();
	    					mOutputStream = mBluetoothSocket.getOutputStream();
	    					mConnected = true;
	    				} catch (Exception e) {
	    					Logger.e("Bluetooth connection error: " + e.getMessage());
//	    					try {
//								Thread.sleep(10000);
//							} catch (InterruptedException e1) {
//								e1.printStackTrace();
//							}
	    					//mControllerIsTurnedOff = true;
	    					
	    					//continue;
	    					break;
	    				}
	    			}
	    			
	    			try {
		    			if (mConnected) {
		    				while (true) {
		    					// �������� ������ �������� �� ������ ������ ������:
		    					List<String> messageList = getMessagesFromStream(mInputStream, Settings.MESSAGE_LENGTH);
		    					
		    					if (mMessageHandler != null) {		    					
			    					// ��������� ������ �������� �������:
			    					for (int i = 0; i < messageList.size(); i++) {
			    						String messageText = messageList.get(i);
			    						
			    						// ������� ���������� � RoboHeadActivity:
			    						Message message = new Message();
			    						message.obj = messageText;
			    						mMessageHandler.sendMessage(message);
			    					}
		    					}
		    				}
		    			}
	    			} catch (Exception e) {
	    				Logger.e(e.getMessage());
	    				disconnect();
	    			}
	    		} // while
	        }
	    }).start();	    
			
		return true;
	}
	
	/**
	 * ������ bluetooth-����������.
	 */
	public static void disconnect() {
		if (mConnected) {
			try {
				if (mBluetoothSocket != null) {
					mBluetoothSocket.close();
					mBluetoothSocket = null;
				}
			} catch (IOException e) {
				mBluetoothSocket = null;
			}
		}
		mConnected = false;
	}
	
	/**
	 * �������� ���������. ���� ����� ��������� �������� ����� ���������� ���������.
	 * @param message ��������� ��� ������������������ ��������� ��� ������������.
	 */
	public static void send(final String message) {
		if (mConnected) {
			if (mBluetoothSocket != null) {
				try {
					mOutputStream.write(message.getBytes());
				} catch (IOException e) {
					Logger.e("Error sending message \"" + message + "\"");
				}
			}
		}
	}

	/**
	 * ��������� �� �������� ������ ������������ �������, ���������� ��������� #13, #10.
	 * �������, ��� ����������� �������� �� ������� ����� �� ������������ � �������� ������ ������.
	 * �������� � �������� ����� ����� ������� ������������� �� ���������� ������ ������.
	 * @param inputStream ����� ����� (��������� �� ������).
	 * @param messageLength ����� ��������� (���������).
	 * @return ������ ������ ������.
	 * @throws IOException ������ ������ �� ������ ����� (�� ������).
	 */
	public static List<String> getMessagesFromStream(final InputStream inputStream, final int messageLength) throws IOException {
		List<String> result = new ArrayList<String>();
		
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		
		int bytesAvailable = dataInputStream.available(); 
		if (bytesAvailable <= 0) {
			return result;
		}
		
		byte[] buffer = new byte[bytesAvailable];
		dataInputStream.readFully(buffer);
		String messages = new String(buffer);
		
		// ���� �� ���������� �������� ����� ������� ���� ���������, ��������� �������:
		if (!mPreviousMessagesRest.equals("")) {
			messages = mPreviousMessagesRest + messages;
		}

		while (messages.length() >= messageLength) {
			String newMessage = messages.substring(0, messageLength);
			result.add(newMessage);
			messages = messages.substring(messageLength);
		}
		mPreviousMessagesRest = messages;
		
		return result;
	}
}
