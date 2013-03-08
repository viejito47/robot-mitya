package ru.dzakhov.test;

import ru.dzakhov.MessageHelper;
import junit.framework.TestCase;

/**
 * Тест класса MessageHelper.
 * @author Дмитрий Дзахов
 *
 */
public final class MessageHelperTest extends TestCase {
	/**
	 * Тест метода correctLength.
	 */
	public void testCorrectLength() {
		String valueToTest = MessageHelper.correctLength("DIMA", 6, '-');
		assertEquals("--DIMA", valueToTest);
		
		valueToTest = MessageHelper.correctLength("DIMA", 2, '-');
		assertEquals("DI", valueToTest);
		
		valueToTest = MessageHelper.correctLength("DIMA", 0, '-');
		assertEquals("", valueToTest);
		
		valueToTest = MessageHelper.correctLength("DIMA", 4, '-');
		assertEquals("DIMA", valueToTest);
	}
	
	/**
	 * Тест метода makeMessage.
	 */
	public void testMakeMessage() {
		String message = MessageHelper.makeMessage("L", "74");
		assertEquals("L0074", message);
		
		message = MessageHelper.makeMessage("MOVE", "19741001");
		assertEquals("M1974", message);
	}
	
	/**
	 * Тест метода getMessageIdentifier.
	 */
	public void testGetMessageIdentifier() {
		String identifier = MessageHelper.getMessageIdentifier("DI123");
		assertEquals("D", identifier);
		
		identifier = MessageHelper.getMessageIdentifier("D");
		assertEquals("D", identifier);
		
		identifier = MessageHelper.getMessageIdentifier("");
		assertEquals(" ", identifier);
	}
	
	/**
	 * Тест метода getMessageValue.
	 */
	public void testGetMessageValue() {
		String value = MessageHelper.getMessageValue("G1234");
		assertEquals("1234", value);
		
		value = MessageHelper.getMessageValue("G123");
		assertEquals("0123", value);
		
		value = MessageHelper.getMessageValue("G");
		assertEquals("0000", value);
		
		value = MessageHelper.getMessageValue("");
		assertEquals("0000", value);
		
		value = MessageHelper.getMessageValue("G12345");
		assertEquals("1234", value);
	}
	
	/**
	 * Тест метода skipFirstBrokenMessage.
	 */
	public void testSkipFirstBrokenMessage() {
		String value = MessageHelper.skipFirstBrokenMessage("G1234I00");
		assertEquals("G1234I00", value);
		
		value = MessageHelper.skipFirstBrokenMessage("1234I00");
		assertEquals("I00", value);
		
		value = MessageHelper.skipFirstBrokenMessage("1234I");
		assertEquals("I", value);
		
		value = MessageHelper.skipFirstBrokenMessage("1234");
		assertEquals("", value);
		
		value = MessageHelper.skipFirstBrokenMessage("");
		assertEquals("", value);
		
		value = MessageHelper.skipFirstBrokenMessage("I");
		assertEquals("I", value);
		
		value = MessageHelper.skipFirstBrokenMessage("A");
		assertEquals("", value);
	}

	/**
	 * Тест метода getFirstMessagePosition.
	 */
	public void testGetFirstMessagePosition() {
		int value = MessageHelper.getFirstMessagePosition("G1234I00");
		assertEquals(0, value);
		
		value = MessageHelper.getFirstMessagePosition("1234I00");
		assertEquals(4, value);
		
		value = MessageHelper.getFirstMessagePosition("1234I");
		assertEquals(4, value);
		
		value = MessageHelper.getFirstMessagePosition("1234");
		assertEquals(-1, value);
		
		value = MessageHelper.getFirstMessagePosition("");
		assertEquals(-1, value);
		
		value = MessageHelper.getFirstMessagePosition("I");
		assertEquals(0, value);
		
		value = MessageHelper.getFirstMessagePosition("A");
		assertEquals(-1, value);
	}
}
