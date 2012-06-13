package ru.dzakhov.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ru.dzakhov.TcpServer;
import junit.framework.TestCase;

/**
 * ����� ������ TcpServer.
 * @author ������� ������
 *
 */
public final class TcpServerTest extends TestCase {
	/**
	 * ���� 1 ������ getCommandsFromStrream.
	 */
	public void testGetCommandsFromStream1() {
		String inputCommandsPortion = "LF012\r\n";
		InputStream inputStream = new ByteArrayInputStream(inputCommandsPortion.getBytes());

		TcpServer tcpServer = new TcpServer(null);		
		try {
			List<String> commands = tcpServer.getCommandsFromStream(inputStream);
			assertEquals(1, commands.size());
			assertEquals(true, commands.get(0).equals("LF012"));
		} catch (IOException e) {
			assertEquals("�� ������ ���� ����������", "���� ����������");
		}
	}

	/**
	 * ���� 2 ������ getCommandsFromStrream.
	 */
	public void testGetCommandsFromStream2() {
		String inputCommandsPortion = "LF000\r\nRF123\r\n";
		InputStream inputStream = new ByteArrayInputStream(inputCommandsPortion.getBytes());

		TcpServer tcpServer = new TcpServer(null);		
		try {
			List<String> commands = tcpServer.getCommandsFromStream(inputStream);
			assertEquals(2, commands.size());
			assertEquals(true, commands.get(0).equals("LF000"));
			assertEquals(true, commands.get(1).equals("RF123"));
		} catch (IOException e) {
			assertEquals("�� ������ ���� ����������", "���� ����������");
		}
	}

	/**
	 * ���� 3 ������ getCommandsFromStrream.
	 */
	public void testGetCommandsFromStream3() {
		String inputCommandsPortion = "LF000\r\nRF123\r\nFR0";
		InputStream inputStream = new ByteArrayInputStream(inputCommandsPortion.getBytes());

		TcpServer tcpServer = new TcpServer(null);
		try {
			List<String> commands = tcpServer.getCommandsFromStream(inputStream);
			assertEquals(2, commands.size());
			assertEquals(true, commands.get(0).equals("LF000"));
			assertEquals(true, commands.get(1).equals("RF123"));
	
			inputCommandsPortion = "00\r\nRB012";
			inputStream = new ByteArrayInputStream(inputCommandsPortion.getBytes());
			commands = tcpServer.getCommandsFromStream(inputStream);
			assertEquals(2, commands.size());
			assertEquals(true, commands.get(0).equals("FR000"));
			assertEquals(true, commands.get(1).equals("RB012"));
	
			inputCommandsPortion = "\r\nFL000\r";
			inputStream = new ByteArrayInputStream(inputCommandsPortion.getBytes());
			commands = tcpServer.getCommandsFromStream(inputStream);
			assertEquals(1, commands.size());
			assertEquals(true, commands.get(0).equals("FL000"));
			
			inputCommandsPortion = "\n";
			inputStream = new ByteArrayInputStream(inputCommandsPortion.getBytes());
			commands = tcpServer.getCommandsFromStream(inputStream);
			assertEquals(0, commands.size());
		} catch (IOException e) {
			assertEquals("�� ������ ���� ����������", "���� ����������");
		}
	}
}
