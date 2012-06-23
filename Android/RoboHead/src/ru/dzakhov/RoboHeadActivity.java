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
 * Главная активити приложения.
 * @author Дмитрий
 *
 */
public class RoboHeadActivity extends AccessoryBaseActivity implements OnClickListener {
	/**
	 * Объект-прослушиватель сообщений от устройства Open Accessory (USB Host Shield + Arduino).
	 */
	private MessageAccessoryReceiver mReceiver;
	
	/**
	 * Runnable объект, для нити, взаимодействующей с уровнем Windows-приложения.
	 */
	private UdpMessageReceiver mUdpMessageReceiver = null;
	
	/**
	 * Объект Handler, с помощью которого передаются команды от уровня Windows-приложения
	 * и сообщения от уровня Arduino-скетча.
	 */
	private Handler mHandler;

	/**
	 * Установка картинки с мордочкой робота.
	 * @param faceResouceId идентификатор ресурса-картинки.
	 */
	private void setFace(final int faceResouceId) {
        ImageView face = (ImageView) this.findViewById(R.id.imageViewFace);
        face.setImageResource(faceResouceId);
	}
	
	/**
	 * Вызывается в конце onCreate.
	 * @param savedInstanceState ранее сохранённое состояние экземпляра.
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
				String message = (String) msg.obj;				
				String command = MessageHelper.getMessageIdentifier(message);
				String value = MessageHelper.getMessageValue(message);
				
				if (command.equals("F")) { // F [face] – смена мордочки
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
				} else if (command.equals("h")) { // h [hit] – попадание
					if (value.equals("0001")) {
		                new Thread() {
		                    public void run() {
								SoundManager.playSound(2, 1);
		                    }
		                } .start();
					}
					// Осознано ничего не делаем для значения 0000. Это сообщение используется для
					// фиксации в хэш-таблице последних принятых сообщений значения, отличного от 0001.
				} else {
					if (command.equals("f")) { // f [fire] – выстрел
						if (value.equals("0001")) {
			                new Thread() {
			                    public void run() {
									SoundManager.playSound(1, 1);
			                    }
			                } .start();
						}
						// Осознано ничего не делаем для значения 0000. Это сообщение используется для
						// фиксации в хэш-таблице последних принятых сообщений значения, отличного от 0001.
					}
					sendMessageToRobot(message);
				}
			}
  		};

		mReceiver = new MessageAccessoryReceiver(mHandler);
		getOpenAccessory().setListener(mReceiver);

    	startUdpReceiver(mHandler);
    }
	
	@Override
	protected final void afterOnDestroy() {
		stopUdpReceiver();
		SoundManager.cleanup();
	}
	
	@Override
	protected final void afterOnResume() {
	}

	@Override
	protected final void afterOnPause() {
		sendBroadcast(new Intent("com.pas.webcam.CONTROL").putExtra("action", "stop"));
	}

	/**
	 * Реализация интерфейса OnClickListner.
	 * @param view где произведён клик.
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
	 * Проверка записи и передачи видео.
	 */
	private void recordVideo() {
		Toast.makeText(this, "Проверка записи и передачи видео", Toast.LENGTH_SHORT).show();

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
	 * Выбор команды для выполнения в тесте.
	 */
	private void selectCommand() {
		final CharSequence[] items = {
				"Левый и правый моторы вперёд 100%", 
				"Левый и правый моторы назад 40%", 
				"Левый и правый моторы в разные стороны 50%", 
				"Голова на 45 градусов влево", 
				"Голова на 45 градусов вправо", 
				"Голова на 30 градусов вверх", 
				"Голова на 30 градусов вниз",
				"Стоп моторы",
				"Настроение нормальное",
				"Настроение весёлое",
				"Настроение грустное",
				"Настроение злое",
				"Заболел",
				"Выстрел"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Действие:");
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
	 * Отправить сообщение роботу.
	 * @param message текст сообщения.
	 */
	private void sendMessageToRobot(final String message) {
		byte[] buffer = new byte[message.length()];
		for (int i = 0; i < message.length(); i++) {
			buffer[i] = (byte) message.charAt(i);
		}		
		getOpenAccessory().write(buffer);
		Logger.d("RoboHeadActivity: Sent to Robot: " + message);
	}
	
	/**
	 * Запуск нити с серверным сокетом.
	 * @param handler для передачи команд от уровня Windows-приложения и 
	 * сообщений от робота.
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
	 * Остановка нити с серверным сокетом.
	 */
	private void stopUdpReceiver() {
		if (mUdpMessageReceiver != null) {
			mUdpMessageReceiver.stopRunning();
			mUdpMessageReceiver = null;
		}
	}
}