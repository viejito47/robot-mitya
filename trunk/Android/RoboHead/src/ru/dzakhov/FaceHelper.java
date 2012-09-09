package ru.dzakhov;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * ¬ыражение лица робота.
 * @author ƒмитрий ƒзахов
 *
 */
enum FaceType { ftOk, ftHappy, ftBlue, ftAngry, ftIll, ftReadyToPlay }; 

/**
 *  ласс дл€ управлени€ лицом робота.
 * @author ƒмитрий ƒзахов
 *
 */
public final class FaceHelper {
	/**
	 *  онтрол ImageView в котором будет отображатьс€ анимаци€.
	 */
	private ImageView mImageView;

	/**
	 * ѕоследн€€ кадрова€ анимаци€.
	 */
	private AnimationDrawable mAnimation = null;
	
	/**
	 * “екуща€ мордочка.
	 */
	private FaceType mCurrentFace = FaceType.ftOk;
	
	/**
	 * ’эндлер, принимающий пустые сообщени€, сигнализирующие о необходимости запустить автоматическую анимацию.
	 * јвтоматическую, потому что она запускаетс€ не по команде, а сама, при простое робота. 
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
				case 0: // веро€тность 20%
					resource = R.drawable.idle_action_ok_2;
					break;
				case 1: // веро€тность 20%
					resource = R.drawable.idle_action_ok_3;
					break;
				default: // веро€тность 60%
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
	 * ’эндлер, принимающий сообщени€, сигнализирующие о необходимости что-то сделать. 
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
	 *  онструктор класса.
	 * @param imageView контрол дл€ вывода анимации.
	 */
	public FaceHelper(final ImageView imageView) {
		mImageView = imageView;
		
		// Thread дл€ генерации Idle-событий. —обыти€ генерируютс€ с переменной периодичностью.
		// ѕромежутки составл€ют 4 сек + random(4 сек).
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
	 * ѕолучение текущего состо€ни€ лица.
	 * @return тип установленного выражени€ лица.
	 */
	public FaceType getFace() {
		return mCurrentFace;
	}
	
	/**
	 * ”становка выражени€ лица.
	 * @param face новое выражение лица.
	 * @return true если началась смена выражени€ лица, false если смена лица уже происходит.
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
	 * «апуск анимации.
	 * @param resource ресурс AnimationList.
	 */
	private void startAnimation(final int resource) {
		if (mAnimation != null) {
			mAnimation.stop();
			mAnimation = null;
			System.gc();
		}
		
		mImageView.setBackgroundResource(resource);
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		mAnimation.start();
	}
}
