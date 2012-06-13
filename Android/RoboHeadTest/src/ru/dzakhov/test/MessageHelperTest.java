package ru.dzakhov.test;

import ru.dzakhov.MessageHelper;
import junit.framework.TestCase;

/**
 * ���� ������ MessageHelper.
 * @author ������� ������
 *
 */
public final class MessageHelperTest extends TestCase {
	/**
	 * ���� ������ correctLength.
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
	 * ���� ������ makeMessage.
	 */
	public void testMakeMessage() {
		String message = MessageHelper.makeMessage("L", "74");
		assertEquals(" L074", message);
		
		message = MessageHelper.makeMessage("MOVE", "1974");
		assertEquals("MO197", message);
	}
	
	/**
	 * ���� ������ getMessageIdentifier.
	 */
	public void testGetMessageIdentifier() {
		String identifier = MessageHelper.getMessageIdentifier("DI123");
		assertEquals("DI", identifier);
		
		identifier = MessageHelper.getMessageIdentifier("D");
		assertEquals(" D", identifier);
		
		identifier = MessageHelper.getMessageIdentifier("");
		assertEquals("  ", identifier);
	}
	
	/**
	 * ���� ������ getMessageValue.
	 */
	public void testGetMessageValue() {
		String value = MessageHelper.getMessageValue("DI123");
		assertEquals("123", value);
		
		value = MessageHelper.getMessageValue("DI12");
		assertEquals("012", value);
		
		value = MessageHelper.getMessageValue("D");
		assertEquals("000", value);
		
		value = MessageHelper.getMessageValue("");
		assertEquals("000", value);
		
		value = MessageHelper.getMessageValue("DI12345");
		assertEquals("123", value);
	}
}
