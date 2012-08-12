package ru.dzakhov.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import ru.dzakhov.BluetoothHelper;
import ru.dzakhov.Settings;

/**
 * Тест класса BluetoothHelper.
 * @author Дмитрий Дзахов
 *
 */
public final class BluetoothHelperTest extends TestCase {
	/**
	 * Тест 1 метода getMessagesFromStrream.
	 */
	public void testGetMessagesFromStream1() {
		String inputMessagesPortion = "L0012";
		InputStream inputStream = new ByteArrayInputStream(inputMessagesPortion.getBytes());

		try {
			List<String> messages = BluetoothHelper.getMessagesFromStream(inputStream, Settings.MESSAGE_LENGTH);
			assertEquals(1, messages.size());
			assertEquals(true, messages.get(0).equals("L0012"));
		} catch (IOException e) {
			assertEquals("Не должно быть исключения", "Есть исключение");
		}
	}

	/**
	 * Тест 2 метода getMessagesFromStrream.
	 */
	public void testGetMessagesFromStream2() {
		String inputMessagesPortion = "L0000" + "R0123";
		InputStream inputStream = new ByteArrayInputStream(inputMessagesPortion.getBytes());

		try {
			List<String> messages = BluetoothHelper.getMessagesFromStream(inputStream, Settings.MESSAGE_LENGTH);
			assertEquals(2, messages.size());
			assertEquals(true, messages.get(0).equals("L0000"));
			assertEquals(true, messages.get(1).equals("R0123"));
		} catch (IOException e) {
			assertEquals("Не должно быть исключения", "Есть исключение");
		}
	}

	/**
	 * Тест 3 метода getMessagesFromStrream.
	 */
	public void testGetMessagesFromStream3() {
		String inputMessagesPortion = "L0000R0123F00";
		InputStream inputStream = new ByteArrayInputStream(inputMessagesPortion.getBytes());

		try {
			List<String> messages = BluetoothHelper.getMessagesFromStream(inputStream, Settings.MESSAGE_LENGTH);
			assertEquals(2, messages.size());
			assertEquals(true, messages.get(0).equals("L0000"));
			assertEquals(true, messages.get(1).equals("R0123"));
	
			inputMessagesPortion = "00R0012";
			inputStream = new ByteArrayInputStream(inputMessagesPortion.getBytes());
			messages = BluetoothHelper.getMessagesFromStream(inputStream, Settings.MESSAGE_LENGTH);
			assertEquals(2, messages.size());
			assertEquals(true, messages.get(0).equals("F0000"));
			assertEquals(true, messages.get(1).equals("R0012"));
	
			inputMessagesPortion = "I000";
			inputStream = new ByteArrayInputStream(inputMessagesPortion.getBytes());
			messages = BluetoothHelper.getMessagesFromStream(inputStream, Settings.MESSAGE_LENGTH);
			assertEquals(0, messages.size());
			
			inputMessagesPortion = "0";
			inputStream = new ByteArrayInputStream(inputMessagesPortion.getBytes());
			messages = BluetoothHelper.getMessagesFromStream(inputStream, Settings.MESSAGE_LENGTH);
			assertEquals(1, messages.size());
			assertEquals(true, messages.get(0).equals("I0000"));
		} catch (IOException e) {
			assertEquals("Не должно быть исключения", "Есть исключение");
		}
	}
}
