package ru.dzakhov;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * ��������� ���� ������.
 * @author ������� ������
 *
 */
enum FaceType { ftOk, ftHappy, ftBlue, ftAngry, ftIll, ftReadyToPlay }; 

/**
 * ����� ��� ���������� ����� ������.
 * @author ������� ������
 *
 */
public final class FaceHelper {
	/**
	 * ������� ImageView � ������� ����� ������������ ��������.
	 */
	private ImageView mImageView;

	/**
	 * ��������� �������� ��������.
	 */
	private AnimationDrawable mAnimation = null;
	
	/**
	 * ������� ��������.
	 */
	private FaceType mCurrentFace = FaceType.ftOk;
	
	/**
	 * �������, ����������� ������ ���������, ��������������� � ������������� ��������� �������������� ��������.
	 * ��������������, ������ ��� ��� ����������� �� �� �������, � ����, ��� ������� ������. 
	 */
	private Handler mHandlerIdleAction = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			int resource;
			
			switch (mCurrentFace) {
			case ftOk:
				final int variansCount = 5;
				int choise = (int) Math.round((variansCount - 1) * Math.random());
				switch (choise) {
				case 0: // ����������� 20%
					resource = R.drawable.idle_action_ok_2;
					break;
				case 1: // ����������� 20%
					resource = R.drawable.idle_action_ok_3;
					break;
				default: // ����������� 60%
					resource = R.drawable.idle_action_ok_1;
					break;
				}				
				break;
			default:
				return;
			}
			
			startAnimation(resource);
		}
	};
	
	/**
	 * �������, ����������� ���������, ��������������� � ������������� ���-s�� �������. 
	 */
	private Handler mHandlerDelayedAction = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (mCurrentFace) {
			case ftReadyToPlay:
				setFace(FaceType.ftOk);
				break;
			default:
				return;
			}
		}
	};

	/**
	 * ����������� ������.
	 * @param imageView ������� ��� ������ ��������.
	 */
	public FaceHelper(final ImageView imageView) {
		mImageView = imageView;
		
		new Thread() {
			public void run() {
				try {
					final int constDelay = 4000;
					final int variantMaxDelay = 4000;
					
					while (true) {					
						int variantDelay = (int) (variantMaxDelay * Math.random());
						Thread.sleep(constDelay + variantDelay);
						mHandlerIdleAction.sendEmptyMessage(0);
					}
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
			}
		} .start();
	}
	
	/**
	 * ��������� ��������� ����.
	 * @param face ����� ��������� ����.
	 * @return true ���� �������� ����� ��������� ����, false ���� ����� ���� ��� ����������.
	 */
	public boolean setFace(final FaceType face) {
		int resource = 0;

		switch (mCurrentFace) {
		case ftAngry:
			if (face == FaceType.ftAngry) {
				resource = R.drawable.face_angry_to_angry;
			} else if (face == FaceType.ftBlue) {
				resource = R.drawable.face_angry_to_blue;
			} else if (face == FaceType.ftHappy) {
				resource = R.drawable.face_angry_to_happy;
			} else if (face == FaceType.ftIll) {
				resource = R.drawable.face_angry_to_ill;
			} else if (face == FaceType.ftOk) {
				resource = R.drawable.face_angry_to_ok;
			} else if (face == FaceType.ftReadyToPlay) {
				resource = R.drawable.face_angry_to_ready_to_play;
			} else {
				return false;
			}
			break;
		case ftBlue:
			if (face == FaceType.ftAngry) {
				resource = R.drawable.face_blue_to_angry;
			} else if (face == FaceType.ftBlue) {
				resource = R.drawable.face_blue_to_blue;
			} else if (face == FaceType.ftHappy) {
				resource = R.drawable.face_blue_to_happy;
			} else if (face == FaceType.ftIll) {
				resource = R.drawable.face_blue_to_ill;
			} else if (face == FaceType.ftOk) {
				resource = R.drawable.face_blue_to_ok;
			} else if (face == FaceType.ftReadyToPlay) {
				resource = R.drawable.face_blue_to_ready_to_play;
			} else {
				return false;
			}
			break;
		case ftHappy:
			if (face == FaceType.ftAngry) {
				resource = R.drawable.face_happy_to_angry;
			} else if (face == FaceType.ftBlue) {
				resource = R.drawable.face_happy_to_blue;
			} else if (face == FaceType.ftHappy) {
				resource = R.drawable.face_happy_to_happy;
			} else if (face == FaceType.ftIll) {
				resource = R.drawable.face_happy_to_ill;
			} else if (face == FaceType.ftOk) {
				resource = R.drawable.face_happy_to_ok;
			} else if (face == FaceType.ftReadyToPlay) {
				resource = R.drawable.face_happy_to_ready_to_play;
			} else {
				return false;
			}
			break;
		case ftIll:
			if (face == FaceType.ftAngry) {
				resource = R.drawable.face_ill_to_angry;
			} else if (face == FaceType.ftBlue) {
				resource = R.drawable.face_ill_to_blue;
			} else if (face == FaceType.ftHappy) {
				resource = R.drawable.face_ill_to_happy;
			} else if (face == FaceType.ftIll) {
				resource = R.drawable.face_ill_to_ill;
			} else if (face == FaceType.ftOk) {
				resource = R.drawable.face_ill_to_ok;
			} else if (face == FaceType.ftReadyToPlay) {
				resource = R.drawable.face_ill_to_ready_to_play;
			} else {
				return false;
			}
			break;
		case ftReadyToPlay:
			if (face == FaceType.ftAngry) {
				resource = R.drawable.face_ready_to_play_to_angry;
			} else if (face == FaceType.ftBlue) {
				resource = R.drawable.face_ready_to_play_to_blue;
			} else if (face == FaceType.ftHappy) {
				resource = R.drawable.face_ready_to_play_to_happy;
			} else if (face == FaceType.ftIll) {
				resource = R.drawable.face_ready_to_play_to_ill;
			} else if (face == FaceType.ftOk) {
				resource = R.drawable.face_ready_to_play_to_ok;
			} else if (face == FaceType.ftReadyToPlay) {
				resource = R.drawable.face_ready_to_play_to_ready_to_play;
			} else {
				return false;
			}
			break;
		case ftOk:
			if (face == FaceType.ftAngry) {
				resource = R.drawable.face_ok_to_angry;
			} else if (face == FaceType.ftBlue) {
				resource = R.drawable.face_ok_to_blue;
			} else if (face == FaceType.ftHappy) {
				resource = R.drawable.face_ok_to_happy;
			} else if (face == FaceType.ftIll) {
				resource = R.drawable.face_ok_to_ill;
			} else if (face == FaceType.ftOk) {
				resource = R.drawable.face_ok_to_ok;
			} else if (face == FaceType.ftReadyToPlay) {
				resource = R.drawable.face_ok_to_ready_to_play;
			} else {
				return false;
			}
			break;
		default:
			return false;
		}
		
		mCurrentFace = face;
		startAnimation(resource);
		
		if (face == FaceType.ftReadyToPlay) {
			final int readyToPlayPause = 5000;
			mHandlerDelayedAction.sendEmptyMessageDelayed(0, readyToPlayPause);
		}
		
        return true;
	}
	
	/**
	 * ������ ��������.
	 * @param resource ������ AnimationList.
	 */
	private void startAnimation(final int resource) {
		if (mAnimation != null) {
			mAnimation.stop();
			mAnimation = null;
		}

		mImageView.setBackgroundResource(resource);
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		mAnimation.start();
	}
}
