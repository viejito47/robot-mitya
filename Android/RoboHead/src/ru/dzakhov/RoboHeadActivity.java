package ru.dzakhov;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * ������� �������� ����������.
 * @author ������� ������
 *
 */
public class RoboHeadActivity extends Activity {
	/**
	 * Runnable ������, ��� ����, ����������������� � ������� Windows-����������.
	 */
	private UdpMessageReceiver mUdpMessageReceiver = null;
	
	/**
	 * ������ Handler, � ������� �������� ���������� ������� �� ������ Windows-����������
	 * � ��������� �� ������ Arduino-������.
	 */
	private Handler mHandler;

	// private OrientationHelper mOrientationHelper;
	
	/**
	 * ��������� �������� � ��������� ������.
	 * @param faceResouceId ������������� �������-��������.
	 */
	private void setFace(final int faceResouceId) {
        ImageView face = (ImageView) this.findViewById(R.id.imageViewFace);
        face.setImageResource(faceResouceId);
	}
	
	/**
	 * �����, ������������ ��� �������� ��������.
	 * @param savedInstanceState ����� ���������� ��������� ����������.
	 */
	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.face);

        SoundManager.getInstance();
        SoundManager.initSounds(this);
        SoundManager.loadSounds();
        
        setFace(R.drawable.mitya_is_ok);
        
    	mHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				String message = (String) msg.obj;				
				String command = MessageHelper.getMessageIdentifier(message);
				String value = MessageHelper.getMessageValue(message);
				
				if (command.equals("F")) { // F [face] � ����� ��������
					if (value.equals("0000")) {
						setFace(R.drawable.mitya_is_ok);
					} else if (value.equals("0001")) {
						setFace(R.drawable.mitya_is_happy);
					} else if (value.equals("0002")) {
						setFace(R.drawable.mitya_is_blue);
					} else if (value.equals("0003")) {
						setFace(R.drawable.mitya_is_angry);
					} else if (value.equals("0004")) {
						setFace(R.drawable.mitya_is_ill);
					}
				} else if (command.equals("h")) { // h [hit] � ���������
					if (value.equals("0001")) {
		                new Thread() {
		                    public void run() {
								SoundManager.playSound(2, 1);
		                    }
		                } .start();
					}
					// �������� ������ �� ������ ��� �������� 0000. ��� ��������� ������������ ���
					// �������� � ���-������� ��������� �������� ��������� ��������, ��������� �� 0001.
				} else if (command.equals("E")) {
					String errorMessage = "������: ";
					if (value.equals("0001")) {
						errorMessage += "�������� ���������";
					} else if (value.equals("0002")) {
						errorMessage += "����������� �������";
					}
					Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
				} else {
					if (command.equals("f")) { // f [fire] � �������
						if (value.equals("0001")) {
			                new Thread() {
			                    public void run() {
									SoundManager.playSound(1, 1);
			                    }
			                } .start();
						}
						// �������� ������ �� ������ ��� �������� 0000. ��� ��������� ������������ ���
						// �������� � ���-������� ��������� �������� ��������� ��������, ��������� �� 0001.
					}
					sendMessageToRobot(message);
				}
			}
  		};

  		BluetoothHelper.initialize(this, mHandler);
  		
    	startUdpReceiver(mHandler);
    	
		// mOrientationHelper = new OrientationHelper(this);
	}
	
	/**
	 * ����� ���������� ��� �������� ��������.
	 */
	@Override
	protected final void onDestroy() {
		super.onDestroy();
		
		stopUdpReceiver();
		SoundManager.cleanup();
	}

	/**
	 * ����� ���������� ��� �������������� ��������.
	 */
	@Override
	protected final void onResume() {
		super.onResume();

		// mOrientationHelper.registerListner();
		BluetoothHelper.connect();
	}

	/**
	 * ����� ���������� ��� �������� �������� � ��������� �����.
	 */
	@Override
	protected final void onPause() {
		super.onPause();
		
		sendBroadcast(new Intent("com.pas.webcam.CONTROL").putExtra("action", "stop"));
		// mOrientationHelper.unregisterListner();
		BluetoothHelper.disconnect();
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public final boolean onOptionsItemSelected(final MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.test:
			selectCommand();
			return true;
		case R.id.test_record:
			recordVideo();
			return true;
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		default:
			break;
		}
		
		return false;
	}
	
	/**
	 * �������� ������ � �������� �����.
	 */
	private void recordVideo() {
/*
		String s = mOrientationHelper.getAzimuthAngle() + "  "
				+ mOrientationHelper.getPitchAngle() + "  "
				+ mOrientationHelper.getRollAngle();
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
*/
	}
	
	/**
	 * ����� ������� ��� ���������� � �����.
	 */
	private void selectCommand() {
		final CharSequence[] items = {
				"����� � ������ ������ ����� 100%", 
				"����� � ������ ������ ����� 40%", 
				"����� � ������ ������ � ������ ������� 50%", 
				"������ �� 45 �������� �����", 
				"������ �� 45 �������� ������", 
				"������ �� 30 �������� �����", 
				"������ �� 30 �������� ����",
				"���� ������",
				"���������� ����������",
				"���������� ������",
				"���������� ��������",
				"���������� ����",
				"�������",
				"�������"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��������:");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		    public void onClick(final DialogInterface dialog, final int item) {
		    	final int cDriveForward255 = 0;
		    	final int cDriveBackward100 = 1;
		    	final int cLeftForward127RightBackward127 = 2;
		    	final int cHeadHorizontal135 = 3;
		    	final int cHeadHorizontal045 = 4;
		    	final int cHeadVertical120 = 5;
		    	final int cHeadVertical060 = 6;
		    	final int cStop = 7;
		    	final int cFaceOK = 8;
		    	final int cFaceHappy = 9;
		    	final int cFaceBlue = 10;
		    	final int cFaceAngry = 11;
		    	final int cFaceIll = 12;
		    	final int cFire = 13;

		    	switch (item) {
		    	case cDriveForward255:
		    		sendMessageToRobot("D00FF");
		    		break;
		    	case cDriveBackward100:
		    		sendMessageToRobot("DFF9C");
		    		break;
		    	case cLeftForward127RightBackward127:
		    		sendMessageToRobot("L007F");
		    		sendMessageToRobot("RFF81");
		    		break;
		    	case cHeadHorizontal135:
		    		sendMessageToRobot("H0087");
		    		break;
		    	case cHeadHorizontal045:
		    		sendMessageToRobot("H002D");
		    		break;
		    	case cHeadVertical120:
		    		sendMessageToRobot("V0078");
		    		break;
		    	case cHeadVertical060:
		    		sendMessageToRobot("V003C");
		    		break;
		    	case cStop:
		    		sendMessageToRobot("D0000");
		    		break;
		    	case cFaceOK:
		    		setFace(R.drawable.mitya_is_ok);
		    		break;
		    	case cFaceHappy:
		    		setFace(R.drawable.mitya_is_happy);
		    		break;
		    	case cFaceBlue:
		    		setFace(R.drawable.mitya_is_blue);
		    		break;
		    	case cFaceAngry:
		    		setFace(R.drawable.mitya_is_angry);
		    		break;
		    	case cFaceIll:
		    		setFace(R.drawable.mitya_is_ill);
		    		break;
		    	case cFire:
					Message message = new Message();
					message.obj = "f0000";
					mHandler.sendMessage(message);
		    		break;
		    	default: 
		    		break;
		    	}
		        //Toast.makeText(getApplicationContext(), selectedCommand, Toast.LENGTH_SHORT);
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * ��������� ��������� ������.
	 * @param message ����� ���������.
	 */
	private void sendMessageToRobot(final String message) {
//		byte[] buffer = new byte[message.length()];
//		for (int i = 0; i < message.length(); i++) {
//			buffer[i] = (byte) message.charAt(i);
//		}		
//		getOpenAccessory().write(buffer);
		Logger.d("RoboHeadActivity: Sent to Robot: " + message);
		BluetoothHelper.send(message);
	}
	
	/**
	 * ������ ���� � ��������� �������.
	 * @param handler ��� �������� ������ �� ������ Windows-���������� � 
	 * ��������� �� ������.
	 */
	private void startUdpReceiver(final Handler handler) {
		if (mUdpMessageReceiver != null) {
			return;
		}
		mUdpMessageReceiver = new UdpMessageReceiver(handler);
		Thread thread = new Thread(mUdpMessageReceiver);
		thread.start();
	}	
	
	/**
	 * ��������� ���� � ��������� �������.
	 */
	private void stopUdpReceiver() {
		if (mUdpMessageReceiver != null) {
			mUdpMessageReceiver.stopRunning();
			mUdpMessageReceiver = null;
		}
	}
}