package ru.dzakhov;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * �������� �������� ����������.
 * @author ������� ������
 *
 */
public final class Settings extends PreferenceActivity implements OnPreferenceChangeListener {
	/**
	 * ����� �������.
	 */
	public static final String SERVERIP = "localhost";
	
	/**
	 * ���� ������ ��� ����� ��������� �� ��.
	 */
	private static int mMessageSocketPort;
	
	/**
	 * MAC-����� Bluetooth-�������� ����������� ������.
	 */
	private static String mRoboBodyMac; // "00:12:03:31:01:22"
	
	/**
	 * ���� ����� �����.
	 */
	private EditTextPreference mEditTextPreferenceMessageSocketPort;
	
	/**
	 * ���� ����� �����.
	 */
	private EditTextPreference mEditTextPreferenceRoboBodyMac;
	
	/**
	 * �������� ���� mMessageSocketPort.
	 * @return ���� ��� ������ ����������� � ��.
	 */
	public static int getMessageSocketPort() {
		return mMessageSocketPort;
	}
	
	/**
	 * �������� ���� mRoboBodyMac.
	 * @return MAC-����� Bluetooth-�������� ����������� ������.
	 */
	public static String getRoboBodyMac() {
		return mRoboBodyMac;
	}
	
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
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		
		String key;
		
		key = getString(R.string.option_message_socket_port_key);		
		mEditTextPreferenceMessageSocketPort = (EditTextPreference) this.findPreference(key);
		mEditTextPreferenceMessageSocketPort.setOnPreferenceChangeListener(this);
		
		key = getString(R.string.option_robobody_mac_key);
		mEditTextPreferenceRoboBodyMac = (EditTextPreference) this.findPreference(key);
		mEditTextPreferenceRoboBodyMac.setOnPreferenceChangeListener(this);
	}
	
	/**
	 * ������������� ��������� ���������.
	 * @param context �������� ����������.
	 */
	public static void initialize(final Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		
		String key;
		String defaultValue;
		
		key = context.getString(R.string.option_message_socket_port_key);
		defaultValue = context.getString(R.string.option_message_socket_port_default_value);
		mMessageSocketPort = Integer.parseInt(settings.getString(key, defaultValue));
		//Logger.d("Settings: " + mMessageSocketPort);
		
		key = context.getString(R.string.option_robobody_mac_key);
		defaultValue = context.getString(R.string.option_robobody_mac_default_value);
		mRoboBodyMac = settings.getString(key, defaultValue);
		//Logger.d("Settings: " + mRoboBodyMac);
	}

	/**
	 * ���������� �������� �������� ��������.
	 * @param preference ������������ �����.
	 * @param newValue ����� ��������.
	 * @return ������� �� ���������.
	 */
	public boolean onPreferenceChange(final Preference preference, final Object newValue) {
		if (preference == null) {
			return false;
		}
		
		if (preference == mEditTextPreferenceMessageSocketPort) {
			String value = (String) newValue;
			mMessageSocketPort = Integer.parseInt(value);
			//Logger.d("Settings2: " + mMessageSocketPort);
			return true;
		} else if (preference == mEditTextPreferenceRoboBodyMac) {
			mRoboBodyMac = (String) newValue;
			//Logger.d("Settings2: " + mRoboBodyMac);
			return true;
		}

		return false;
	}
}
