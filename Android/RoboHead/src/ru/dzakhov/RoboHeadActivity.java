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
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Главная активити приложения.
 * @author Дмитрий Дзахов
 *
 */
public class RoboHeadActivity extends Activity {
	/**
	 * Runnable object for message receiving. Used in the thread that interacts with windows-application level.
	 */
	private UdpMessageReceiver mUdpMessageReceiver = null;
	
	/**
	 * Runnable object for message sending. Used in the thread that interacts with windows-application level.
	 */
	private UdpMessageSender mUdpMessageSender = null;
	
	/**
	 * Объект Handler, с помощью которого передаются команды от уровня Windows-приложения
	 * и сообщения от уровня Arduino-скетча.
	 */
	private Handler mHandler;
	
	// private OrientationHelper mOrientationHelper;
	
	/**
	 * Класс-контроллер для управления лицом робота.
	 */
	private FaceHelper mFaceHelper;

	/**
	 * Главный ImageView - лицо.
	 */
	private ImageView mFaceImageView;
	
	/**
	 * Текущая мордочка для тестирования.
	 */
	private FaceType testFace = FaceType.ftOk;
	
	/**
	 * Режим записи РобоСкрипта. В этом режиме команды, выполняемые на уровне RoboHead
	 * (мимика, воспроизведение звука) не выполняются, а просто передаются на уровень robo_body.
	 * Выполняться они будут когда будет запущено воспроизведение РобоСкрипта на уровне robo_body
	 * и команды начнут поступать в RoboHead из robo_body.
	 */
	private boolean mRecordingRoboScriptMode = false;
	
	/**
	 * Метод, вызывающийся при создании активити.
	 * @param savedInstanceState ранее сохранённое состояние экземпляра.
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
        
		new FaceTouchHelper(mFaceImageView, mFaceHelper);
		
    	mHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				String message = (String) msg.obj;				
				String command = MessageHelper.getMessageIdentifier(message);
				String value = MessageHelper.getMessageValue(message);

				if (command.equals("M")) { // M [mood] – смена мордочки
					if (!mRecordingRoboScriptMode) {
						if (message.equals(MessageConstant.FACETYPE_OK)) {
					        mFaceHelper.setFace(FaceType.ftOk);
					        mUdpMessageSender.send(message);
						} else if (message.equals(MessageConstant.FACETYPE_HAPPY)) {
					        mFaceHelper.setFace(FaceType.ftHappy);
					        mUdpMessageSender.send(message);
						} else if (message.equals(MessageConstant.FACETYPE_BLUE)) {
					        mFaceHelper.setFace(FaceType.ftBlue);
					        mUdpMessageSender.send(message);
						} else if (message.equals(MessageConstant.FACETYPE_ANGRY)) {
					        mFaceHelper.setFace(FaceType.ftAngry);
					        mUdpMessageSender.send(message);
						} else if (message.equals(MessageConstant.FACETYPE_ILL)) {
					        mFaceHelper.setFace(FaceType.ftIll);
					        mUdpMessageSender.send(message);
						} else if (message.equals(MessageConstant.FACETYPE_VERY_HAPPY)) {
							sendMessageToRobot(message);
						} else if (message.equals(MessageConstant.FACETYPE_READY_TO_PLAY)) {
							sendMessageToRobot(message);
						} else if (message.equals(MessageConstant.FACETYPE_VERY_BLUE)) {
							sendMessageToRobot(message);
						} else if (message.equals(MessageConstant.FACETYPE_ANGRY_JUMP_BACK)) {
							sendMessageToRobot(message);
						} else if (message.equals(MessageConstant.FACETYPE_MUSIC_LOVER)) {
							sendMessageToRobot(message);
						}
					} else {
						sendMessageToRobot(message);
					}
				} else if (command.equals("I")) { // I [instruction] – команда
			        mUdpMessageSender.send(message);
					if (value.equals("0002")) { // turn off the screen
						WindowManager.LayoutParams params = getWindow().getAttributes();
						params.screenBrightness = 0;
						getWindow().setAttributes(params);
					} else if (value.equals("0003")) { // turn on the screen
						WindowManager.LayoutParams params = getWindow().getAttributes();
						params.screenBrightness = -1;
						getWindow().setAttributes(params);
					} else {
						sendMessageToRobot(message);
					}
				} else if (command.equals("=")) {
			        mUdpMessageSender.send(message);
					sendMessageToRobot(message);
				} else if (command.equals("~")) {
					mUdpMessageSender.send(message);
				} else if (command.equals("*")) { // * [hit] – попадание
					if (message.equals(MessageConstant.HIT)) {
				        mUdpMessageSender.send(message);
		                new Thread() {
		                    public void run() {
								SoundManager.playSound(SoundManager.SCREAM, 1);
		                    }
		                } .start();
					}
					// Осознано ничего не делаем для значения 0000. Это сообщение используется для
					// фиксации в хэш-таблице последних принятых сообщений значения, отличного от 0001.
				} else if (command.equals("r")) {
					if (message.startsWith(MessageConstant.ROBOSCRIPT_REC_STARTED)) {
						// Начало записи РобоСкрипта. Сообщение приходит от ПК.
						mRecordingRoboScriptMode = true;
						MessageUniqueFilter.setActive(false);
						sendMessageToRobot(message);
					} else if (message.startsWith(MessageConstant.ROBOSCRIPT_REC_STOPPED)) {
						// Конец записи РобоСкрипта. Сообщение приходит от робота. 
						// При ошибках в РобоСкрипте тоже приходит.
						mRecordingRoboScriptMode = false;
						MessageUniqueFilter.setActive(true);
					} else {
						// Запуск РобоСкрипта на выполнение. Сообщение приходит от ПК.
						sendMessageToRobot(message);
					}
				} else if (message.equals("Z0000")) {
					// Конец записи РобоСкрипта.
					mRecordingRoboScriptMode = false;
					MessageUniqueFilter.setActive(true);
			        mUdpMessageSender.send(message);
					sendMessageToRobot(message);
				} else if (message.equals("#0000")) {
					// Ничего не делаем.
					Logger.d("OK");
				} else if (command.equals("#")) {
					if (!message.equals("#0000")) {
						// В ПК отправляем только ошибки.
						mUdpMessageSender.send(message);
					}
					String errorMessage = "Ошибка: ";
					if (message.equals(MessageConstant.WRONG_MESSAGE)) {
						errorMessage += "неверное сообщение";
					} else if (message.equals(MessageConstant.UNKNOWN_COMMAND)) {
						errorMessage += "неизвестная команда";
					} else if (message.equals(MessageConstant.ROBOSCRIPT_ILLEGAL_COMMAND)) {
						errorMessage += "недопустимая команда в РобоСкрипт";
					} else if (message.equals(MessageConstant.ROBOSCRIPT_ILLEGAL_COMMAND_SEQUENCE)) {
						errorMessage += "неверная последовательность команд в РобоСкрипт";
					} else if (message.equals(MessageConstant.ROBOSCRIPT_NO_MEMORY)) {
						errorMessage += "невозможно выделить необходимый объём памяти для РобоСкрипта";
					} else if (message.equals(MessageConstant.ROBOSCRIPT_OUT_OF_BOUNDS)) {
						errorMessage += "попытка выхода за границы выделенной для РобоСкрипт памяти";
					} else if (message.equals(MessageConstant.ILLEGAL_COMMAND)) {
						errorMessage += "недопустимая команда вне РобоСкрипт";
					} else if (message.equals(MessageConstant.BROKEN_COMMAND)) {
						errorMessage += "пропущен символ команды, команда потеряна";
					} else if (message.equals(MessageConstant.WRONG_VOLTAGE_DEVIDER)) {
						errorMessage += "неверный номер делителя напряжения";
					}
					Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
					Logger.e(errorMessage);
				} else {
					if (!mRecordingRoboScriptMode) {
						if (command.equals("s")) { // s [shoot] – выстрел
							if (message.equals(MessageConstant.FIRE)) {
						        mUdpMessageSender.send(message);
				                new Thread() {
				                    public void run() {
										SoundManager.playSound(SoundManager.GUN, 1);
				                    }
				                } .start();
							}
							// Осознано ничего не делаем для значения 0000. Это сообщение используется для
							// фиксации в хэш-таблице последних принятых сообщений значения, отличного от 0001.
						}
					}
					sendMessageToRobot(message);
				}
			}
  		};

//  		BluetoothHelper.initialize(this, mHandler);
    	
		// mOrientationHelper = new OrientationHelper(this);
	}
	
	/**
	 * Метод вызывается при закрытии активити.
	 */
	@Override
	protected final void onDestroy() {
		super.onDestroy();
		SoundManager.cleanup();
	}

	/**
	 * Метод вызывается при восстановлении активити.
	 */
	@Override
	protected final void onResume() {
		super.onResume();

    	startUdpReceiver(mHandler);
    	startUdpSender();

    	// mOrientationHelper.registerListner();
  		BluetoothHelper.initialize(this);
		BluetoothHelper.start(mHandler);
	}

	/**
	 * Метод вызывается при переходе активити в состояние паузы.
	 */
	@Override
	protected final void onPause() {
		super.onPause();
		
		sendBroadcast(new Intent("com.pas.webcam.CONTROL").putExtra("action", "stop"));
		// mOrientationHelper.unregisterListner();

		stopUdpReceiver();
		stopUdpSender();
		
		BluetoothHelper.stop();
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
	 * Выбор команды для выполнения в тесте.
	 */
	private void selectCommand() {
		final CharSequence[] items = {
				"Стоп моторы",
				"Фары вкл.",
				"Фары выкл.",
				"Выстрел",
				"Левый и правый моторы вперёд 100%", 
				"Левый и правый моторы назад 40%", 
				"Левый и правый моторы в разные стороны 50%", 
				"Голова на 45 градусов влево", 
				"Голова на 45 градусов вправо", 
				"Голова на 30 градусов вверх", 
				"Голова на 30 градусов вниз"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Действие:");
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
		    		sendMessageToRobot("G0000");
		    		break;
		    	case cLightsOn:
		    		sendMessageToRobot("I0001");
		    		break;
		    	case cLightsOff:
		    		sendMessageToRobot("I0000");
		    		break;
		    	case cFire:
					Message message = new Message();
					message.obj = "s0001";
					mHandler.sendMessage(message);
		    		break;
		    	case cDriveForward255:
		    		sendMessageToRobot("G00FF");
		    		break;
		    	case cDriveBackward100:
		    		sendMessageToRobot("GFF9C");
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
	 * Отправить сообщение роботу.
	 * @param message текст сообщения.
	 */
	private void sendMessageToRobot(final String message) {
		Logger.d("Sent to robot: " + message);
		BluetoothHelper.send(message);
	}
	
	/**
	 * Start message receiving thread.
	 * @param handler для передачи команд от уровня Windows-приложения и 
	 * сообщений от робота.
	 */
	private void startUdpReceiver(final Handler handler) {
		if (mUdpMessageReceiver != null) {
			return;
		}
		mUdpMessageReceiver = new UdpMessageReceiver(handler);
		mUdpMessageReceiver.start();
	}	
	
	/**
	 * Остановка нити с серверным сокетом.
	 */
	private void stopUdpReceiver() {
		if (mUdpMessageReceiver != null) {
			mUdpMessageReceiver.interrupt();
			mUdpMessageReceiver = null;
		}
	}
	
	/**
	 * Start message sending thread.
	 */
	private void startUdpSender() {
		if (mUdpMessageSender != null) {
			return;
		}
		mUdpMessageSender = new UdpMessageSender(this);
		mUdpMessageSender.start();
	}	
	
	/**
	 * Stop message sending thread.
	 */
	private void stopUdpSender() {
		if (mUdpMessageSender != null) {
			mUdpMessageSender.interrupt();
			mUdpMessageSender = null;
		}
	}
}
