package ru.dzakhov;

import java.util.HashMap;

/**
 *  ласс дл€ отсеивани€ повтор€ющихс€ сообщений.
 * @author ƒмитрий ƒзахов
 *
 */
public final class MessageUniqueFilter {
	/**
	 * ’эш-таблица сообщений. »дентификатор сообщени€ - ключ в хэш-таблице.
	 * —ообщение считаетс€ повтор€ющимс€, если ключ уже есть в хэш-таблице и значени€ совпадают.
	 */
	private HashMap<String, String> messageHash = new HashMap<String, String>();
	
	/**
	 * ѕовтор€ющиес€ сообщени€ должны игнорироватьс€. ћетод определ€ет каким было значение 
	 * последнего сообщени€ с аналогичным идентификатором и если значени€ совпадают, возвращает false,
	 * иначе - true.
	 * @param message провер€емое сообщение.
	 * @return true, если не совпадает с последним сообщением того же типа.
	 */
	public boolean isNewMessage(final String message) {
		String key = MessageHelper.getMessageIdentifier(message);
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
}
