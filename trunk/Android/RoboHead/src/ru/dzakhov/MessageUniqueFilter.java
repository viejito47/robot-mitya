package ru.dzakhov;

import java.util.HashMap;

/**
 * Класс для отсеивания повторяющихся сообщений.
 * @author Дмитрий Дзахов
 *
 */
public final class MessageUniqueFilter {
	/**
	 * Хэш-таблица сообщений. Идентификатор сообщения - ключ в хэш-таблице.
	 * Сообщение считается повторяющимся, если ключ уже есть в хэш-таблице и значения совпадают.
	 */
	private static HashMap<String, String> messageHash = new HashMap<String, String>();
	
	/**
	 * Активность фильтра.
	 */
	private static boolean mActive = true;
	
	/**
	 * Все члены класса статические, поэтому конструктор закрыт.
	 */
	private MessageUniqueFilter() {		
	}
	
	/**
	 * Повторяющиеся сообщения должны игнорироваться. Метод определяет каким было значение 
	 * последнего сообщения с аналогичным идентификатором и если значения совпадают, возвращает false,
	 * иначе - true. Если фильтр неактивен, возвращаемое значение всегда true.
	 * @param message проверяемое сообщение.
	 * @return true, если не совпадает с последним сообщением того же типа.
	 */
	public static boolean isNewMessage(final String message) {
		if (!mActive) {
			return true;
		}
		
		String key = MessageHelper.getMessageIdentifier(message);
		
		// Функция работает только для команд управления движением и ориентации головы.
		final String controlCommands = "LRGHV";
		if (controlCommands.indexOf(key) < 0) {
			return true;
		}
		
		String value = MessageHelper.getMessageValue(message);
		
		String lastValue = messageHash.get(key);
		if (lastValue == null) {
			messageHash.put(key, value);
			return true;
		} else {
			if (lastValue.equals(value)) {
				return false;
			} else {
				messageHash.put(key, value);
				return true;
			}
		}
	}
	
	/**
	 * Геттер активности фильтра сообщений.
	 * @return значение активности.
	 */
	public static boolean getActive() {
		return mActive;
	}
	
	/**
	 * Мутатор активности фильтра сообщений.
	 * @param value устанавливаемое значение активности.
	 */
	public static void setActive(final boolean value) {
		mActive = value;
	}
}
