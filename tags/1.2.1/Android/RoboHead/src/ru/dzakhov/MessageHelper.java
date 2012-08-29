package ru.dzakhov;

/**
 * Набор методов для сборки и разбора с сообщений.
 * @author Дмитрий Дзахов
 *
 */
public final class MessageHelper {
	/**
	 * Конструктор спрятан.
	 */
	private MessageHelper() {		
	}
	
	/**
	 * Символ, которым дополняется идентификатор сообщения если ему не хватает длины до Settings.MESSAGE_IDENTIFIER_LENGTH.
	 */
	private static final char IDENTIFIER_PREFIX = ' ';
	
	/**
	 * Символ, которым дополняется значение из сообщения если ему не хватает длины до Settings.MESSAGE_VALUE_LENGTH.
	 */
	private static final char VALUE_PREFIX = '0';
	
	/**
	 * Дополняет заданный текст справа указанными символами до указанной длины.
	 * Если длина превышает указанный размер, текст обрезается. 
	 * @param text исходный текст.
	 * @param length требуемая длина.
	 * @param prefixChar символ, добавляемый справа.
	 * @return скорректированный текст.
	 */
	public static String correctLength(final String text, final int length, final char prefixChar) {
		String result = text;
		int sourceLength = text.length();
		if (sourceLength > length) {
			result = text.substring(0, length);
		} else if (sourceLength < length) {
			int charsToAdd = length - sourceLength;
			for (int i = 0; i < charsToAdd; i++) {
				result = prefixChar + result;
			}
		}
		
		return result;
	}
	
	/**
	 * Сборка сообщения по идентификатору и значению.
	 * @param messageIdentifier идентификатор сообщения.
	 * @param messageValue значение сообщения.
	 * @return сообщение.
	 */
	public static String makeMessage(final String messageIdentifier, final String messageValue) {
		String identifier = correctLength(messageIdentifier, Settings.MESSAGE_IDENTIFIER_LENGTH, IDENTIFIER_PREFIX);
		String value = correctLength(messageValue, Settings.MESSAGE_VALUE_LENGTH, VALUE_PREFIX);
		return identifier.concat(value);
	}
	
	/**
	 * Извлечение идентификатора из сообщения.
	 * @param message сообщение.
	 * @return идентификатор сообщения.
	 */
	public static String getMessageIdentifier(final String message) {
		int realLength = Math.min(message.length(), Settings.MESSAGE_IDENTIFIER_LENGTH);
		String result = message.substring(0, realLength);
		result = correctLength(result, Settings.MESSAGE_IDENTIFIER_LENGTH, IDENTIFIER_PREFIX);
		return result;
	}
	
	/**
	 * Извлечение значения из сообщения.
	 * @param message сообщение.
	 * @return значение из сообщения.
	 */
	public static String getMessageValue(final String message) {
		String result;
		
		// Первые символы считаем идентификатором.
		if (message.length() <= Settings.MESSAGE_IDENTIFIER_LENGTH) {
			result = "";
		} else {
			result = message.substring(Settings.MESSAGE_IDENTIFIER_LENGTH, message.length());
		}
		
		result = correctLength(result, Settings.MESSAGE_VALUE_LENGTH, VALUE_PREFIX);
		return result;
	}
}
