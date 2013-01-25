package ru.dzakhov;

import java.util.HashMap;

/**
 * Class to exclude repeatable messages.
 * @author Dmitry Dzakhov
 *
 */
public final class MessageUniqueFilter {
	/**
	 * Hash-table for messages. Message identifier is a key in hash-table.
	 * The message considered to be repeatable if its key already exsists in hash-table and values are equal.
	 */
	private static HashMap<String, String> messageHash = new HashMap<String, String>();
	
	/**
	 * Активность фильтра.
	 */
	private static boolean mActive = true;
	
	/**
	 * All members of the class are static. That's why constructor is privet.
	 */
	private MessageUniqueFilter() {		
	}
	
	/**
	 * Repeatable messages should be ignored. The method determine last message's value
	 * with the same identifier. If the values are equal it returns false, otherwise true.
	 * If the filter is inactive returned value is always true.
	 * @param message to be tested.
	 * @return true, if the message is not equal to the last message of the same type.
	 */
	public static boolean isNewMessage(final String message) {
		if (!mActive) {
			return true;
		}
		
		String key = MessageHelper.getMessageIdentifier(message);
		
		// The function works only with motion commands L, R, G, 
		// head orientation commands H and V and 
		// mood changing command M.
		final String controlCommands = "LRGHVM";
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
	 * Getter of the field Activity.
	 * @return value of the filter's activity.
	 */
	public static boolean getActive() {
		return mActive;
	}
	
	/**
	 * Setter of the field Activity.
	 * @param value is the new value of the filter's activity.
	 */
	public static void setActive(final boolean value) {
		mActive = value;
	}
}
