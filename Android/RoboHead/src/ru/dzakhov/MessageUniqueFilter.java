package ru.dzakhov;

import java.util.HashMap;

/**
 * ����� ��� ���������� ������������� ���������.
 * @author ������� ������
 *
 */
public final class MessageUniqueFilter {
	/**
	 * ���-������� ���������. ������������� ��������� - ���� � ���-�������.
	 * ��������� ��������� �������������, ���� ���� ��� ���� � ���-������� � �������� ���������.
	 */
	private static HashMap<String, String> messageHash = new HashMap<String, String>();
	
	/**
	 * ���������� �������.
	 */
	private static boolean mActive = true;
	
	/**
	 * ��� ����� ������ �����������, ������� ����������� ������.
	 */
	private MessageUniqueFilter() {		
	}
	
	/**
	 * ������������� ��������� ������ ��������������. ����� ���������� ����� ���� �������� 
	 * ���������� ��������� � ����������� ��������������� � ���� �������� ���������, ���������� false,
	 * ����� - true. ���� ������ ���������, ������������ �������� ������ true.
	 * @param message ����������� ���������.
	 * @return true, ���� �� ��������� � ��������� ���������� ���� �� ����.
	 */
	public static boolean isNewMessage(final String message) {
		if (!mActive) {
			return true;
		}
		
		String key = MessageHelper.getMessageIdentifier(message);
		
		// ������� �������� ������ ��� ������ ���������� ��������� � ���������� ������.
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
	 * ������ ���������� ������� ���������.
	 * @return �������� ����������.
	 */
	public static boolean getActive() {
		return mActive;
	}
	
	/**
	 * ������� ���������� ������� ���������.
	 * @param value ��������������� �������� ����������.
	 */
	public static void setActive(final boolean value) {
		mActive = value;
	}
}
