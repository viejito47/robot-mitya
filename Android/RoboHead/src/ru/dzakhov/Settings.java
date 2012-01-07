package ru.dzakhov;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * �������� �������� ����������.
 * @author �������
 *
 */
public class Settings extends PreferenceActivity {
	/**
	 * ����� ���������� ������.
	 */
	public static final String COMMANDSERVERSOCKETIP = "localhost";
	
	/**
	 * ���� ������.
	 */
	public static final int COMMANDSOCKETPORT = 51974;
	
	/**
	 * ����� ������, ������������ ������.
	 */
	public static final int COMMANDLENGTH = 5;
	
	/**
	 * ����� ���������, ���������� �� ������.
	 */
	public static final int MESSAGELENGTH = 5;
	
	/**
	 * � ���������� � ������� ������, � ��������� �� ������ �������������� � ����� ������� Handle.
	 * ��������� ����� ��� ��������� "�������� ������" ��� ����������� Handle.
	 */
	public static final int COMMAND = 1;
	
	/**
	 * � ���������� � ������� ������, � ��������� �� ������ �������������� � ����� ������� Handle.
	 * ��������� ����� ��� ��������� "��������� �� ������" ��� ����������� Handle.
	 */
	public static final int MESSAGE = 2;
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}
}
