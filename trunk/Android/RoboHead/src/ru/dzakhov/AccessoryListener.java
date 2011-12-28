package ru.dzakhov;

/**
 * Прослушиватель данных от ADK.
 * @author Дмитрий (в том числе)
 *
 */
public interface AccessoryListener {	
	/**
	 * Получено сообщение.
	 * @param data данные сообщения.
	 */
	void onAccessoryMessage(byte[] data);
}
