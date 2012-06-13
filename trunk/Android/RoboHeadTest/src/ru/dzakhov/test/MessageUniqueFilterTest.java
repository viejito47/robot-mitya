package ru.dzakhov.test;

import ru.dzakhov.MessageUniqueFilter;
import junit.framework.TestCase;

/**
 * Тесты класса CommandFilter.
 * @author Дмитрий Дзахов
 *
 */
public final class MessageUniqueFilterTest extends TestCase {
	/**
	 * Тест метода isNewMessage.
	 */
	public void testIsNewMessage() {
		MessageUniqueFilter filter = new MessageUniqueFilter();
		assertEquals(true, filter.isNewMessage("aa123"));
		assertEquals(false, filter.isNewMessage("aa123"));
		assertEquals(true, filter.isNewMessage("bb123"));
		assertEquals(true, filter.isNewMessage("bb111"));
		assertEquals(true, filter.isNewMessage("bb123"));
		assertEquals(false, filter.isNewMessage("aa123"));
	}
}
