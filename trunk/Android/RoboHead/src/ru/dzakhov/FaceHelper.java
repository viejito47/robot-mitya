package ru.dzakhov;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * Выражение лица робота.
 * @author Дмитрий Дзахов
 *
 */
enum FaceType { ftOk, ftHappy, ftBlue, ftAngry, ftIll }; 

/**
 * Набор методов для управления лицом робота.
 * @author Дмитрий Дзахов
 *
 */
public final class FaceHelper {
	private static FaceType mCurrentFace = FaceType.ftOk;
	private static FaceType mPreviousFace = mCurrentFace;
	
	/**
	 * Хэндлер для приёма сообщений из другого потока.
	 */
//	private static Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(final Message msg) {
//			if (msg.obj == null) {
//				mChangingFace = false;
//				Logger.d("mChangingFace = false");
//			} else {
//				ImageView imageView = (ImageView) msg.obj;
//				imageView.setImageResource(msg.arg1);
//				Logger.d("msg.arg1 = " + msg.arg1);
//			}
//		}
//	};
	
	/**
	 * Признак установки лица. Устанавливается на время смены лица, затем сбрасывается.
	 */
	private static boolean mChangingFace;
	
	/**
	 * Закрываю конструктор статического класса.
	 */
	private FaceHelper() {		
	}
	
	public static Message createFaceChangeMessage(final ImageView imageView, final int imageResource) {
		Message message = new Message();
		message.obj = imageView;
		message.arg1 = imageResource;
		return message;
	}
	
	/**
	 * Установка выражения лица.
	 * @param imageView куда будет отбражаться картинка.
	 * @param face новое выражение лица.
	 * @return true если началась смена выражения лица, false если смена лица уже происходит.
	 */
	public static boolean setFace(final ImageView imageView, final FaceType face) {
//		// Если сейчас находимся в состоянии смены выражения лица, то выходим.
//		if (mChangingFace) {
//			return false;
//		}
//		
//    	// Если ничего менять не надо, выходим.
//    	if (face == mCurrentFace) {
//    		return false;
//    	}
//    	
//		mChangingFace = true;
//		
//		mPreviousFace = mCurrentFace;
//        mCurrentFace = face;
//		
//        // Ожидание между кадрами будет в отдельной нити. Смена картинки будет в основной нити по событию Handler.
//		new Thread() {
//            public void run() {
//        		Message message;
//        		int sendMessageDelay = 0; 
//
//        		// Если лицо не ftOk, плавно переводим его в ftOk.
//            	switch (mPreviousFace) {
//            	case ftHappy:
//            		break;
//            	case ftBlue:
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_blue_3);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_blue_2);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_blue_1);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_is_ok);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		break;
//            	case ftAngry: 
//            		break;
//            	case ftIll:
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_ill_3);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_ill_2);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_ill_1);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_is_ok);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		break;
//				default:
//					break;
//            	}
//            	
//            	// Теперь переводим лицо в новое выражение.
//            	switch (mCurrentFace) {
//            	case ftHappy:
//            		break;
//            	case ftBlue:
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_blue_1);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_blue_2);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_blue_3);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_is_blue);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		break;
//            	case ftAngry: 
//            		break;
//            	case ftIll:
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_ill_1);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_ill_2);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_turn_ill_3);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		message = createFaceChangeMessage(imageView, R.drawable.mitya_is_ill);
//            		mHandler.sendMessageDelayed(message, sendMessageDelay);
//            		sendMessageDelay += Settings.FACE_FRAME_DELAY;
//            		
//            		break;
//				default:
//					break;
//            	}
//            	
//            	// Передача сообщения, означающего завершение смены выражения лица.
//            	message = createFaceChangeMessage(null, 0);
//            	mHandler.sendMessageDelayed(message, sendMessageDelay);
//
//        		while (true) {
//            		if (!mChangingFace) {
//            			break;
//            		}            		
//            	}
//            }
//        } .start();
//
        return true;
	}
}
