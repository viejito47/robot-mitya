package ru.dzakhov.test;

import ru.dzakhov.MessageUniqueFilter;
import junit.framework.TestCase;

/**
 * ����� ������ CommandFilter.
 * @author ������� ������
 *
 */
public final class MessageUniqueFilterTest extends TestCase {
	/**
	 * ���� ������ isNewMessage.
	 */
	public void testIsNewMessage1() {
		assertEquals(true, MessageUniqueFilter.isNewMessage("R1234"));
		assertEquals(false, MessageUniqueFilter.isNewMessage("R1234"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("L1234"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("L1111"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("L1234"));
		assertEquals(false, MessageUniqueFilter.isNewMessage("R1234"));
	}
	
	/**
	 * ���� ������ isNewMessage.
	 */
	public void testIsNewMessage2() {
		assertEquals(true, MessageUniqueFilter.isNewMessage("F0001"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("L0190"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("F0002"));
	}
	
	/**
	 * ���� ������ isNewMessage.
	 */
	public void testIsNewMessage3() {
		assertEquals(true, MessageUniqueFilter.isNewMessage("F0001"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("LFF42"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("F0002"));
	}

	/**
	 * ���� ������ isNewMessage.
	 */
	public void testIsNewMessageCaseSensitive() {
		assertEquals(true, MessageUniqueFilter.isNewMessage("H0000"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("LFF43"));
		assertEquals(true, MessageUniqueFilter.isNewMessage("h0000"));
	}
}
