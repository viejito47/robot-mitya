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
	private HashMap<String, String> messageHash = new HashMap<String, String>();
	
	/**
	 * ������������� ��������� ������ ��������������. ����� ���������� ����� ���� �������� 
	 * ���������� ��������� � ����������� ��������������� � ���� �������� ���������, ���������� false,
	 * ����� - true.
	 * @param message ����������� ���������.
	 * @return true, ���� �� ��������� � ��������� ���������� ���� �� ����.
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
