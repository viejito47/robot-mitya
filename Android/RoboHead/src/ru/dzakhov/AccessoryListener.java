package ru.dzakhov;

/**
 * �������������� ������ �� ADK.
 * @author ������� (� ��� �����)
 *
 */
public interface AccessoryListener {	
	/**
	 * �������� ���������.
	 * @param data ������ ���������.
	 */
	void onAccessoryMessage(byte[] data);
}
