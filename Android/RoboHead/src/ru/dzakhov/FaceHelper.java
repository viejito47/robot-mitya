package ru.dzakhov;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

/**
 * Выражение лица робота.
 * @author Дмитрий Дзахов
 *
 */
enum FaceType { ftOk, ftHappy, ftBlue, ftAngry, ftIll }; 

/**
 * Класс для управления лицом робота.
 * @author Дмитрий Дзахов
 *
 */
public final class FaceHelper {
	/**
	 * Контрол ImageView в котором будет отображаться анимация.
	 */
	private ImageView mImageView;

	/**
	 * Последняя кадровая анимация.
	 */
	private AnimationDrawable mAnimation = null;
	
	/**
	 * Текущая мордочка.
	 */
	private FaceType mCurrentFace = FaceType.ftOk;
	
	/**
	 * Конструктор класса.
	 * @param imageView контрол для вывода анимации.
	 */
	public FaceHelper(final ImageView imageView) {
		mImageView = imageView;
	}
	
	/**
	 * Установка выражения лица.
	 * @param face новое выражение лица.
	 * @return true если началась смена выражения лица, false если смена лица уже происходит.
	 */
	public boolean setFace(final FaceType face) {
		if (mAnimation != null) {
			mAnimation.stop();
			mImageView.clearAnimation();
			mAnimation = null;
		}

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
		
		mImageView.setBackgroundResource(resource);
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		mAnimation.start();
		mCurrentFace = face;
        return true;
	}
}
