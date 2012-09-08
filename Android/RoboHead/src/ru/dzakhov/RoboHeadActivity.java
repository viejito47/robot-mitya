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
import android.view.WindowManager.LayoutParams;
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
	 * �����-���������� ��� ���������� ����� ������.
	 */
	private FaceHelper mFaceHelper;

	/**
	 * ������� ImageView - ����.
	 */
	private ImageView mFaceImageView;
	
	/**
	 * ������� �������� ��� ������������.
	 */
	private FaceType testFace = FaceType.ftOk;
	
	/**
	 * �����, ������������ ��� �������� ��������.
	 * @param savedInstanceState ����� ���������� ��������� ����������.
	 */
	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.face);
		getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
        SoundManager.getInstance();
        SoundManager.initSounds(this);
        SoundManager.loadSounds();
        
        mFaceImageView = (ImageView) this.findViewById(R.id.imageViewFace);
        mFaceHelper = new FaceHelper(mFaceImageView);        
        mFaceHelper.setFace(FaceType.ftOk);
        
    	mHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				String message = (String) msg.obj;				
				String command = MessageHelper.getMessageIdentifier(message);
				String value = MessageHelper.getMessageValue(message);

				if (command.equals("F")) { // F [face] � ����� ��������
					if (value.equals("0001")) {
				        mFaceHelper.setFace(FaceType.ftOk);
					} else if (value.equals("0002")) {
				        mFaceHelper.setFace(FaceType.ftHappy);
					} else if (value.equals("0003")) {
				        mFaceHelper.setFace(FaceType.ftBlue);
					} else if (value.equals("0004")) {
				        mFaceHelper.setFace(FaceType.ftAngry);
					} else if (value.equals("0005")) {
				        mFaceHelper.setFace(FaceType.ftIll);
					} else if (value.equals("0102")) {
				        mFaceHelper.setFace(FaceType.ftReadyToPlay);
						sendMessageToRobot(message);
					} else if (value.equals("0103")) {
				        mFaceHelper.setFace(FaceType.ftBlue);
						sendMessageToRobot(message);
					}
					Logger.d(message);
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
		case R.id.test_control:
			selectCommand();
			return true;
		case R.id.test_animation:
	        if (testFace == FaceType.ftOk) {
	        	testFace = FaceType.ftReadyToPlay;
	        } else if (testFace == FaceType.ftReadyToPlay) {
	        	testFace = FaceType.ftAngry;
	        } else if (testFace == FaceType.ftAngry) {
	        	testFace = FaceType.ftBlue;
	        } else if (testFace == FaceType.ftBlue) {
	        	testFace = FaceType.ftHappy;
	        } else if (testFace == FaceType.ftHappy) {
	        	testFace = FaceType.ftIll;
	        } else if (testFace == FaceType.ftIll) {
	        	testFace = FaceType.ftOk;
	        }
			mFaceHelper.setFace(testFace);
			return true;
		default:
			break;
		}
		
		return false;
	}
	
	/**
	 * ����� ������� ��� ���������� � �����.
	 */
	private void selectCommand() {
		final CharSequence[] items = {
				"���� ������",
				"���� ���.",
				"���� ����.",
				"�������",
				"����� � ������ ������ ����� 100%", 
				"����� � ������ ������ ����� 40%", 
				"����� � ������ ������ � ������ ������� 50%", 
				"������ �� 45 �������� �����", 
				"������ �� 45 �������� ������", 
				"������ �� 30 �������� �����", 
				"������ �� 30 �������� ����"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��������:");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		    public void onClick(final DialogInterface dialog, final int item) {
		    	final int cStop = 0;
		    	final int cLightsOn = 1;
		    	final int cLightsOff = 2;
		    	final int cFire = 3;
		    	final int cDriveForward255 = 4;
		    	final int cDriveBackward100 = 5;
		    	final int cLeftForward127RightBackward127 = 6;
		    	final int cHeadHorizontal135 = 7;
		    	final int cHeadHorizontal045 = 8;
		    	final int cHeadVertical120 = 9;
		    	final int cHeadVertical060 = 10;

		    	switch (item) {
		    	case cStop:
		    		sendMessageToRobot("D0000");
		    		break;
		    	case cLightsOn:
		    		sendMessageToRobot("I0001");
		    		break;
		    	case cLightsOff:
		    		sendMessageToRobot("I0000");
		    		break;
		    	case cFire:
					Message message = new Message();
					message.obj = "f0001";
					mHandler.sendMessage(message);
		    		break;
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