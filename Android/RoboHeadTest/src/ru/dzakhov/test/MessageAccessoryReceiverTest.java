package ru.dzakhov.test;

import java.util.List;

import ru.dzakhov.MessageAccessoryReceiver;

import junit.framework.TestCase;

/**
 * Тесты класса MessageAccessoryReceiver.
 * @author Дмитрий
 *
 */
public final class MessageAccessoryReceiverTest extends TestCase {
	/**
	 * Тест 1 метода onAccessoryMessage.
	 */
	public void testOnAccessoryMessage1() {
		MessageAccessoryReceiver messageAccessoryReceiver = new MessageAccessoryReceiver(null);
		String messages = "";
		List<String> messageList = messageAccessoryReceiver.getMessagesFromBuffer(messages.getBytes());
		assertEquals(0, messageList.size());
	}

	/**
	 * Тест 2 метода onAccessoryMessage.
	 */
	public void testOnAccessoryMessage2() {
		MessageAccessoryReceiver messageAccessoryReceiver = new MessageAccessoryReceiver(null);
		String messages = "h00";
		List<String> messageList = messageAccessoryReceiver.getMessagesFromBuffer(messages.getBytes());
		assertEquals(0, messageList.size());
	}

	/**
	 * Тест 3 метода onAccessoryMessage.
	 */
	public void testOnAccessoryMessage3() {
		MessageAccessoryReceiver messageAccessoryReceiver = new MessageAccessoryReceiver(null);
		String messages = "h0000";
		List<String> messageList = messageAccessoryReceiver.getMessagesFromBuffer(messages.getBytes());
		assertEquals(1, messageList.size());
		assertEquals("h0000", messageList.get(0));
	}

	/**
	 * Тест 4 метода onAccessoryMessage.
	 */
	public void testOnAccessoryMessage4() {
		MessageAccessoryReceiver messageAccessoryReceiver = new MessageAccessoryReceiver(null);
		String messages = "h0000F0001F0002";
		List<String> messageList = messageAccessoryReceiver.getMessagesFromBuffer(messages.getBytes());
		final int numOfMessages = 3;
		assertEquals(numOfMessages, messageList.size());
		assertEquals("h0000", messageList.get(0));
		assertEquals("F0001", messageList.get(1));
		assertEquals("F0002", messageList.get(2));
	}

	/**
	 * Тест 5 метода onAccessoryMessage.
	 */
	public void testOnAccessoryMessage5() {
		MessageAccessoryReceiver messageAccessoryReceiver = new MessageAccessoryReceiver(null);
		
		String messages = "h0000F00";
		List<String> messageList = messageAccessoryReceiver.getMessagesFromBuffer(messages.getBytes());
		assertEquals(1, messageList.size());
		assertEquals("h0000", messageList.get(0));
		
		messages = "01F0002";
		messageList = messageAccessoryReceiver.getMessagesFromBuffer(messages.getBytes());
		assertEquals(2, messageList.size());
		assertEquals("F0001", messageList.get(0));
		assertEquals("F0002", messageList.get(1));
	}
}
