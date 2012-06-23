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
		MessageUniqueFilter filter = new MessageUniqueFilter();
		assertEquals(true, filter.isNewMessage("a1234"));
		assertEquals(false, filter.isNewMessage("a1234"));
		assertEquals(true, filter.isNewMessage("b1234"));
		assertEquals(true, filter.isNewMessage("b1111"));
		assertEquals(true, filter.isNewMessage("b1234"));
		assertEquals(false, filter.isNewMessage("a1234"));
	}
	
	/**
	 * ���� ������ isNewMessage.
	 */
	public void testIsNewMessage2() {
		MessageUniqueFilter filter = new MessageUniqueFilter();
		assertEquals(true, filter.isNewMessage("F0001"));
		assertEquals(true, filter.isNewMessage("L0190"));
		assertEquals(true, filter.isNewMessage("F0002"));
	}
	
	/**
	 * ���� ������ isNewMessage.
	 */
	public void testIsNewMessage3() {
		MessageUniqueFilter filter = new MessageUniqueFilter();
		assertEquals(true, filter.isNewMessage("F0001"));
		assertEquals(true, filter.isNewMessage("LFF42"));
		assertEquals(true, filter.isNewMessage("F0002"));
	}

	/**
	 * ���� ������ isNewMessage.
	 */
	public void testIsNewMessageCaseSensitive() {
		MessageUniqueFilter filter = new MessageUniqueFilter();
		assertEquals(true, filter.isNewMessage("H0000"));
		assertEquals(true, filter.isNewMessage("LFF42"));
		assertEquals(true, filter.isNewMessage("h0000"));
	}
}
