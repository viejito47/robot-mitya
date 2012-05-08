package ru.dzakhov;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * ������� �������� ����������.
 * @author �������
 *
 */
public class RoboHeadActivity extends AccessoryBaseActivity implements OnClickListener {
	/**
	 * ������-�������������� ��������� �� ���������� Open Accessory (USB Host Shield + Arduino).
	 */
	private MessageAccessoryReceiver mReceiver;
	
	/**
	 * Runnable ������, ��� ����, ����������������� � ������� Windows-����������.
	 */
	private TcpServer mTcpServer = null;
	
	/**
	 * ������ Handler, � ������� �������� ���������� ������� �� ������ Windows-����������
	 * � ��������� �� ������ Arduino-������.
	 */
	private Handler mHandler;

	/**
	 * ������, ����������� ������������ �������� � � ��������.
	 */
//dsd	private CameraManager mCameraManager;
	
	/**
	 * ��������� �������� � ��������� ������.
	 * @param faceResouceId ������������� �������-��������.
	 */
	private void setFace(final int faceResouceId) {
        ImageView face = (ImageView) this.findViewById(R.id.imageViewFace);
        face.setImageResource(faceResouceId);
	}
	
	/**
	 * ���������� � ����� onCreate.
	 * @param savedInstanceState ����� ���������� ��������� ����������.
	 */
	@Override
	protected final void afterOnCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.face);

        SoundManager.getInstance();
        SoundManager.initSounds(this);
        SoundManager.loadSounds();
        
        setFace(R.drawable.mitya_is_ok);
        
    	mHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				String command = (String) msg.obj;
				if (command.equalsIgnoreCase("MD000")) {
					setFace(R.drawable.mitya_is_ok);
				} else if (command.equalsIgnoreCase("MD001")) {
					setFace(R.drawable.mitya_is_happy);
				} else if (command.equalsIgnoreCase("MD002")) {
					setFace(R.drawable.mitya_is_blue);
				} else if (command.equalsIgnoreCase("MD003")) {
					setFace(R.drawable.mitya_is_angry);
				} else if (command.equalsIgnoreCase("MD004")) {
					setFace(R.drawable.mitya_is_ill);
//				} else if (command.equalsIgnoreCase("FL000")) {
////dsd					mCameraManager.turnLightOff();
//				} else if (command.equalsIgnoreCase("FL001")) {
////dsd					mCameraManager.turnLightOn();
				} else if (command.equalsIgnoreCase("HT000")) {
	                new Thread() {
	                    public void run() {
							SoundManager.playSound(2, 1);
	                    }
	                } .start();
				} else {
					if (command.equalsIgnoreCase("FR000")) {
		                new Thread() {
		                    public void run() {
								SoundManager.playSound(1, 1);
		                    }
		                } .start();
					}
					sendCommand(command);
				}
			}
  		};

		mReceiver = new MessageAccessoryReceiver(mHandler);
		getOpenAccessory().setListener(mReceiver);

    	startTcpServer(mHandler);
    	
//dsd    	mCameraManager = new CameraManager(this);
    }
	
	@Override
	protected final void afterOnDestroy() {
		stopTcpServer();
		SoundManager.cleanup();
	}
	
	@Override
	protected final void afterOnResume() {
//dsd		mCameraManager.open();
	}

	@Override
	protected final void afterOnPause() {
//dsd		mCameraManager.release();
	}

	/**
	 * ���������� ���������� OnClickListner.
	 * @param view ��� ��������� ����.
	 */
	public void onClick(final View view) {
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
		Toast.makeText(this, "�������� ������ � �������� �����", Toast.LENGTH_SHORT).show();

		Intent launcher = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
		Intent ipWebcam = 
			new Intent()
			.setClassName("com.pas.webcam", "com.pas.webcam.Rolling")
			.putExtra("cheats", new String[] { 
					//"set(Photo,1024,768)",         // set photo resolution to 1024x768
					"set(DisableVideo,false)",      // Disable video streaming (only photo and immediate photo)
					"reset(Port)",                 // Use default port 8080
					"set(HtmlPath,/sdcard/html/)", // Override server pages with ones in this directory 
					})
			.putExtra("hidebtn1", true)                // Hide help button
			.putExtra("caption2", "Run in background") // Change caption on "Actions..."
			.putExtra("intent2", launcher)             // And give button another purpose
		    .putExtra("returnto", new Intent().setClassName(
		    		RoboHeadActivity.this, RoboHeadActivity.class.getName())); // Set activity to return to
		startActivity(ipWebcam);
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
		    	final int cDF255 = 0;
		    	final int cDB100 = 1;
		    	final int cLF127RB127 = 2;
		    	final int cHH135 = 3;
		    	final int cHH045 = 4;
		    	final int cHV120 = 5;
		    	final int cHV060 = 6;
		    	final int cDF000 = 7;
		    	final int cFACEOK = 8;
		    	final int cFACEHAPPY = 9;
		    	final int cFACEBLUE = 10;
		    	final int cFACEANGRY = 11;
		    	final int cFACEILL = 12;
		    	final int cFIRE = 13;

		    	switch (item) {
		    	case cDF255:
		    		sendCommand("DF255");
		    		break;
		    	case cDB100:
		    		sendCommand("DB100");
		    		break;
		    	case cLF127RB127:
		    		sendCommand("LF127");
		    		sendCommand("RB127");
		    		break;
		    	case cHH135:
		    		sendCommand("HH135");
		    		break;
		    	case cHH045:
		    		sendCommand("HH045");
		    		break;
		    	case cHV120:
		    		sendCommand("HV120");
		    		break;
		    	case cHV060:
		    		sendCommand("HV060");
		    		break;
		    	case cDF000:
		    		sendCommand("DF000");
		    		break;
		    	case cFACEOK:
		    		setFace(R.drawable.mitya_is_ok);
		    		break;
		    	case cFACEHAPPY:
		    		setFace(R.drawable.mitya_is_happy);
		    		break;
		    	case cFACEBLUE:
		    		setFace(R.drawable.mitya_is_blue);
		    		break;
		    	case cFACEANGRY:
		    		setFace(R.drawable.mitya_is_angry);
		    		break;
		    	case cFACEILL:
		    		setFace(R.drawable.mitya_is_ill);
		    		break;
		    	case cFIRE:
					Message message = new Message();
					message.obj = "FR000";
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
	 * ��������� ������� ������.
	 * @param command ����� �������.
	 */
	private void sendCommand(final String command) {
		byte[] buffer = new byte[command.length()];
		for (int i = 0; i < command.length(); i++) {
			buffer[i] = (byte) command.charAt(i);
		}		
		getOpenAccessory().write(buffer);
		Logger.d("RoboHeadActivity: Sent to Robot: " + command);
	}
	
	/**
	 * ������ ���� � ��������� �������.
	 * @param handler ��� �������� ������ �� ������ Windows-���������� � 
	 * ��������� �� ������.
	 */
	private void startTcpServer(final Handler handler) {
		if (mTcpServer != null) {
			return;
		}
		mTcpServer = new TcpServer(handler);
		Thread thread = new Thread(mTcpServer);
		thread.start();
	}	
	
	/**
	 * ��������� ���� � ��������� �������.
	 */
	private void stopTcpServer() {
		if (mTcpServer != null) {
			mTcpServer.stopRun();
			mTcpServer = null;
		}
	}
}