package ru.dzakhov;

/**
 * Статический класс для централизованного хранения текстов сообщений языка. 
 * @author Дмитрий
 *
 */
public final class MessageConstant {
	/**
	 * Команда виляния хвостом. Движение равномерное.
	 */
	public static final String WAG_TAIL = "t0001";
	
	/**
	 * Команда виляния хвостом. Угол изменяется по синусоиде.
	 */
	public static final String WAG_TAIL_SIN = "t0002";

	/**
	 * Команда управления настроением: "нормальное настроение".
	 */
	public static final String FACETYPE_OK = "F0001";

	/**
	 * Команда управления настроением: "счастье".
	 */
	public static final String FACETYPE_HAPPY = "F0002";

	/**
	 * Команда управления настроением: "грусть".
	 */
	public static final String FACETYPE_BLUE = "F0003";

	/**
	 * Команда управления настроением: "злость".
	 */
	public static final String FACETYPE_ANGRY = "F0004";

	/**
	 * Команда управления настроением: "болезнь".
	 */
	public static final String FACETYPE_ILL = "F0005";

	/**
	 * Команда управления настроением: "готов играть".
	 */
	public static final String FACETYPE_READY_TO_PLAY = "F0102";

	/**
	 * Команда управления настроением: "очень грустно".
	 */
	public static final String FACETYPE_VERY_BLUE = "F0103";

	/**
	 * Команда управления настроением: "злюсь, отпрыгиваю".
	 */
	public static final String FACETYPE_ANGRY_JUMP_BACK = "F0104";
	
	/**
	 * Команда управления настроением: "меломан".
	 */
	public static final String FACETYPE_MUSIC_LOVER = "F0105";
	
	/**
	 * Сигнал попадания.
	 */
	public static final String HIT = "h0001";
	
	/**
	 * Команда выстрела.
	 */
	public static final String FIRE = "f0001";
	
	/**
	 * Начало сообщения о начале записи РобоСкрипта.
	 */
	public static final String ROBOSCRIPT_REC_STARTED = "r01";
	
	/**
	 * Начало сообщения о конце записи РобоСкрипта.
	 */
	public static final String ROBOSCRIPT_REC_STOPPED = "r02";
	
	/**
	 * Ошибка неверное сообщение.
	 */
	public static final String WRONG_MESSAGE = "E0001";
	
	/**
	 * Ошибка неизвестная команда.
	 */
	public static final String UNKNOWN_COMMAND = "E0002";
	
	/**
	 * Недопустимая команда в РобоСкрипт.
	 */
	public static final String ROBOSCRIPT_ILLEGAL_COMMAND = "E0003";
	
	/**
	 * Неверная последовательность команд в РобоСкрипт.
	 */
	public static final String ROBOSCRIPT_ILLEGAL_COMMAND_SEQUENCE = "E0004";
	
	/**
	 * Невозможно выделить необходимый объём памяти для РобоСкрипта.
	 */
	public static final String ROBOSCRIPT_NO_MEMORY = "E0005";
	
	/**
	 * Попытка выхода за границы выделенной для РобоСкрипт памяти.
	 */
	public static final String ROBOSCRIPT_OUT_OF_BOUNDS = "E0006";
	
	/**
	 * Недопустимая команда вне РобоСкрипт.
	 */
	public static final String ILLEGAL_COMMAND = "E0007";
	
	/**
	 * Закрытый конструктор класса.
	 */
	private MessageConstant() {   
	}
}
