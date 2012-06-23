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
	 * ����� �������.
	 */
	public static final String SERVERIP = "localhost";
	
	/**
	 * ���� ������ ��� ����� ��������� �� ��.
	 */
	public static final int MESSAGESOCKETPORT = 51974;
	
	/**
	 * ���� ������ ��� �������� ����� � Windows-����������.
	 */
	public static final int MEDIASOCKETPORT = 51973;
	
	/**
	 * ����� ���������, ����� �������� ��, �������, ����������.
	 */
	public static final int MESSAGE_LENGTH = 5;
	
	/**
	 * ����� �������������� � ���������.
	 */
	public static final int MESSAGE_IDENTIFIER_LENGTH = 1;
	
	/**
	 * ����� �������� � ���������.
	 */
	public static final int MESSAGE_VALUE_LENGTH = 4;
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}
}
