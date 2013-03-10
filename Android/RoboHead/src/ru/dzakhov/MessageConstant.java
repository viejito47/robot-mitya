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
	public static final String FACETYPE_OK = "M0001";

	/**
	 * Команда управления настроением: "счастье".
	 */
	public static final String FACETYPE_HAPPY = "M0002";

	/**
	 * Команда управления настроением: "грусть".
	 */
	public static final String FACETYPE_BLUE = "M0003";

	/**
	 * Команда управления настроением: "злость".
	 */
	public static final String FACETYPE_ANGRY = "M0004";

	/**
	 * Команда управления настроением: "болезнь".
	 */
	public static final String FACETYPE_ILL = "M0005";

	/**
	 * Команда управления настроением: "виляет хвостом".
	 */
	public static final String FACETYPE_VERY_HAPPY = "M0101";

	/**
	 * Команда управления настроением: "готов играть".
	 */
	public static final String FACETYPE_READY_TO_PLAY = "M0102";

	/**
	 * Команда управления настроением: "очень грустно".
	 */
	public static final String FACETYPE_VERY_BLUE = "M0103";

	/**
	 * Команда управления настроением: "злюсь, отпрыгиваю".
	 */
	public static final String FACETYPE_ANGRY_JUMP_BACK = "M0104";
	
	/**
	 * Команда управления настроением: "меломан".
	 */
	public static final String FACETYPE_MUSIC_LOVER = "M0105";
	
	/**
	 * Сигнал попадания.
	 */
	public static final String HIT = "h0001";
	
	/**
	 * Команда выстрела.
	 */
	public static final String FIRE = "s0001";
	
	/**
	 * Начало сообщения о начале записи РобоСкрипта.
	 */
	public static final String ROBOSCRIPT_REC_STARTED = "r01";
	
	/**
	 * Начало сообщения о конце записи РобоСкрипта.
	 */
	public static final String ROBOSCRIPT_REC_STOPPED = "r02";
	
	/**
	 * Сообщение об успешно выполненной команде.
	 */
	public static final String OK = "#0000";
	
	/**
	 * Ошибка неверное сообщение.
	 */
	public static final String WRONG_MESSAGE = "#0001";
	
	/**
	 * Ошибка неизвестная команда.
	 */
	public static final String UNKNOWN_COMMAND = "#0002";
	
	/**
	 * Недопустимая команда в РобоСкрипт.
	 */
	public static final String ROBOSCRIPT_ILLEGAL_COMMAND = "#0003";
	
	/**
	 * Неверная последовательность команд в РобоСкрипт.
	 */
	public static final String ROBOSCRIPT_ILLEGAL_COMMAND_SEQUENCE = "#0004";
	
	/**
	 * Невозможно выделить необходимый объём памяти для РобоСкрипта.
	 */
	public static final String ROBOSCRIPT_NO_MEMORY = "#0005";
	
	/**
	 * Попытка выхода за границы выделенной для РобоСкрипт памяти.
	 */
	public static final String ROBOSCRIPT_OUT_OF_BOUNDS = "#0006";
	
	/**
	 * Недопустимая команда вне РобоСкрипт.
	 */
	public static final String ILLEGAL_COMMAND = "#0007";
	
	/**
	 * Закрытый конструктор класса.
	 */
	private MessageConstant() {   
	}
}
