package ru.dzakhov;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * ��������� ���� ������.
 * @author ������� ������
 *
 */
enum FaceType { ftOk, ftHappy, ftBlue, ftAngry, ftIll }; 

/**
 * ����� ������� ��� ���������� ����� ������.
 * @author ������� ������
 *
 */
public final class FaceHelper {
	private static FaceType mCurrentFace = FaceType.ftOk;
	private static FaceType mPreviousFace = mCurrentFace;
	
	/**
	 * ������� ��� ����� ��������� �� ������� ������.
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
	 * ������� ��������� ����. ��������������� �� ����� ����� ����, ����� ������������.
	 */
	private static boolean mChangingFace;
	
	/**
	 * �������� ����������� ������������ ������.
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
	 * ��������� ��������� ����.
	 * @param imageView ���� ����� ����������� ��������.
	 * @param face ����� ��������� ����.
	 * @return true ���� �������� ����� ��������� ����, false ���� ����� ���� ��� ����������.
	 */
	public static boolean setFace(final ImageView imageView, final FaceType face) {
//		// ���� ������ ��������� � ��������� ����� ��������� ����, �� �������.
//		if (mChangingFace) {
//			return false;
//		}
//		
//    	// ���� ������ ������ �� ����, �������.
//    	if (face == mCurrentFace) {
//    		return false;
//    	}
//    	
//		mChangingFace = true;
//		
//		mPreviousFace = mCurrentFace;
//        mCurrentFace = face;
//		
//        // �������� ����� ������� ����� � ��������� ����. ����� �������� ����� � �������� ���� �� ������� Handler.
//		new Thread() {
//            public void run() {
//        		Message message;
//        		int sendMessageDelay = 0; 
//
//        		// ���� ���� �� ftOk, ������ ��������� ��� � ftOk.
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
//            	// ������ ��������� ���� � ����� ���������.
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
//            	// �������� ���������, ����������� ���������� ����� ��������� ����.
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
