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
enum FaceType { ftOk, ftHappy, ftBlue, ftAngry, ftIll }; 

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
	 *  онструктор класса.
	 * @param imageView контрол дл€ вывода анимации.
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
	 * ”становка выражени€ лица.
	 * @param face новое выражение лица.
	 * @return true если началась смена выражени€ лица, false если смена лица уже происходит.
	 */
	public boolean setFace(final FaceType face) {
		int resource = 0;

		switch (mCurrentFace) {
		case ftAngry:
			switch (face) {
			case ftAngry:
				resource = R.drawable.face_angry_to_angry;
				break;
			case ftBlue:
				resource = R.drawable.face_angry_to_blue;
				break;
			case ftHappy:
				resource = R.drawable.face_angry_to_happy;
				break;
			case ftIll:
				resource = R.drawable.face_angry_to_ill;
				break;
			case ftOk:
				resource = R.drawable.face_angry_to_ok;
				break;
			default:
				return false;
			}
			break;
		case ftBlue:
			switch (face) {
			case ftAngry:
				resource = R.drawable.face_blue_to_angry;
				break;
			case ftBlue:
				resource = R.drawable.face_blue_to_blue;
				break;
			case ftHappy:
				resource = R.drawable.face_blue_to_happy;
				break;
			case ftIll:
				resource = R.drawable.face_blue_to_ill;
				break;
			case ftOk:
				resource = R.drawable.face_blue_to_ok;
				break;
			default:
				return false;
			}
			break;
		case ftHappy:
			switch (face) {
			case ftAngry:
				resource = R.drawable.face_happy_to_angry;
				break;
			case ftBlue:
				resource = R.drawable.face_happy_to_blue;
				break;
			case ftHappy:
				resource = R.drawable.face_happy_to_happy;
				break;
			case ftIll:
				resource = R.drawable.face_happy_to_ill;
				break;
			case ftOk:
				resource = R.drawable.face_happy_to_ok;
				break;
			default:
				return false;
			}
			break;
		case ftIll:
			switch (face) {
			case ftAngry:
				resource = R.drawable.face_ill_to_angry;
				break;
			case ftBlue:
				resource = R.drawable.face_ill_to_blue;
				break;
			case ftHappy:
				resource = R.drawable.face_ill_to_happy;
				break;
			case ftIll:
				resource = R.drawable.face_ill_to_ill;
				break;
			case ftOk:
				resource = R.drawable.face_ill_to_ok;
				break;
			default:
				return false;
			}
			break;
		case ftOk:
			switch (face) {
			case ftAngry:
				resource = R.drawable.face_ok_to_angry;
				break;
			case ftBlue:
				resource = R.drawable.face_ok_to_blue;
				break;
			case ftHappy:
				resource = R.drawable.face_ok_to_happy;
				break;
			case ftIll:
				resource = R.drawable.face_ok_to_ill;
				break;
			case ftOk:
				resource = R.drawable.face_ok_to_ok;
				break;
			default:
				return false;
			}
			break;
		default:
			return false;
		}
		
		mCurrentFace = face;
		startAnimation(resource);
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
		}

		mImageView.setBackgroundResource(resource);
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		mAnimation.start();
	}
}
