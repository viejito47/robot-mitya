package ru.dzakhov;

/**
 * ����� ������� ��� ������ � ������� � ���������.
 * @author ������� ������
 *
 */
public final class MessageHelper {
	/**
	 * ����������� �������.
	 */
	private MessageHelper() {		
	}
	
	/**
	 * ������, ������� ����������� ������������� ��������� ���� ��� �� ������� ����� �� Settings.MESSAGE_IDENTIFIER_LENGTH.
	 */
	private static final char IDENTIFIER_PREFIX = ' ';
	
	/**
	 * ������, ������� ����������� �������� �� ��������� ���� ��� �� ������� ����� �� Settings.MESSAGE_VALUE_LENGTH.
	 */
	private static final char VALUE_PREFIX = '0';
	
	/**
	 * ��������� �������� ����� ������ ���������� ��������� �� ��������� �����.
	 * ���� ����� ��������� ��������� ������, ����� ����������. 
	 * @param text �������� �����.
	 * @param length ��������� �����.
	 * @param prefixChar ������, ����������� ������.
	 * @return ����������������� �����.
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
	 * ������ ��������� �� �������������� � ��������.
	 * @param messageIdentifier ������������� ���������.
	 * @param messageValue �������� ���������.
	 * @return ���������.
	 */
	public static String makeMessage(final String messageIdentifier, final String messageValue) {
		String identifier = correctLength(messageIdentifier, Settings.MESSAGE_IDENTIFIER_LENGTH, IDENTIFIER_PREFIX);
		String value = correctLength(messageValue, Settings.MESSAGE_VALUE_LENGTH, VALUE_PREFIX);
		return identifier.concat(value);
	}
	
	/**
	 * ���������� �������������� �� ���������.
	 * @param message ���������.
	 * @return ������������� ���������.
	 */
	public static String getMessageIdentifier(final String message) {
		int realLength = Math.min(message.length(), Settings.MESSAGE_IDENTIFIER_LENGTH);
		String result = message.substring(0, realLength);
		result = correctLength(result, Settings.MESSAGE_IDENTIFIER_LENGTH, IDENTIFIER_PREFIX);
		return result;
	}
	
	/**
	 * ���������� �������� �� ���������.
	 * @param message ���������.
	 * @return �������� �� ���������.
	 */
	public static String getMessageValue(final String message) {
		String result;
		
		// ������ ������� ������� ���������������.
		if (message.length() <= Settings.MESSAGE_IDENTIFIER_LENGTH) {
			result = "";
		} else {
			result = message.substring(Settings.MESSAGE_IDENTIFIER_LENGTH, message.length());
		}
		
		result = correctLength(result, Settings.MESSAGE_VALUE_LENGTH, VALUE_PREFIX);
		return result;
	}
}
