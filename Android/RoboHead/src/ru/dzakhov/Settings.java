package ru.dzakhov;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Application options activity.
 * @author Dmitry Dzakhov.
 *
 */
public final class Settings extends PreferenceActivity implements OnPreferenceChangeListener {
	/**
	 * Server address.
	 */
	public static final String SERVERIP = "localhost";
	
	/**
	 * Port that receive commands and messages from PC.
	 */
	private static int mUdpReceivePort;
	
	/**
	 * Port that send messages to PC.
	 */
	private static int mUdpSendPort;
	
	/**
	 * Flag that indicates broadcast data sending to PC through Wi-Fi.
	 */
	private static boolean mUdpSendBroadcast;
	
	/**
	 * Flag that indicates local network broadcast data sending to PC through Wi-Fi.
	 */
	private static boolean mUdpSendBroadcastLocal;
	
	/**
	 * Flag that indicates peer-to-peer data sending.
	 */
	private static boolean mUdpSendP2P;
	
	/**
	 * Remote PC address - data recpient. This option is enabled when peer-to-peer flag is set.
	 */
	private static String mUdpRecipientIp;
	
	/**
	 * Robot's Bluetooth adapter MAC-address.
	 */
	private static String mRoboBodyMac; // "00:12:03:31:01:22"
	
	/**
	 * EditText for mUdpReceivePort option.
	 */
	private EditTextPreference mEditTextPreferenceUdpReceivePort;
	
	/**
	 * EditText for mUdpSendPort option.
	 */
	private EditTextPreference mEditTextPreferenceUdpSendPort;

	/**
	 * CheckBox for mUdpSendBroadcast option.
	 */
	private CheckBoxPreference mCheckBoxPreferenceUdpSendBroadcast;  

	/**
	 * CheckBox for mUdpSendBroadcastLocal option.
	 */
	private CheckBoxPreference mCheckBoxPreferenceUdpSendBroadcastLocal;  

	/**
	 * CheckBox for mUdpSendP2P option.
	 */
	private CheckBoxPreference mCheckBoxPreferenceUdpSendP2P;  
	
	/**
	 * EditText for mUdpRecipientIp option.
	 */
	private EditTextPreference mEditTextPreferenceUdpRecipientIp;
	
	/**
	 * EditText for mRoboBodyMac option.
	 */
	private EditTextPreference mEditTextPreferenceRoboBodyMac;
	
	/**
	 * mUdpReceivePort field accessor.
	 * @return port that receive commands and messages from PC.
	 */
	public static int getUdpReceivePort() {
		return mUdpReceivePort;
	}
	
	/**
	 * mUdpSendPort field accessor.
	 * @return port that send messages to PC.
	 */
	public static int getUdpSendPort() {
		return mUdpSendPort;
	}
	
	/**
	 * mUdpSendBroadcast field accessor.
	 * @return the flag that indicates broadcast data sending to PC through Wi-Fi.
	 */
	public static boolean getUdpSendBroadcast() {
		return mUdpSendBroadcast;
	}
	
	/**
	 * mUdpSendBroadcastLocal field accessor.
	 * @return the flag that indicates local network broadcast data sending to PC through Wi-Fi.
	 */
	public static boolean getUdpSendBroadcastLocal() {
		return mUdpSendBroadcastLocal;
	}
	
	/**
	 * mUdpSendP2P field accessor.
	 * @return the flag that indicates peer-to-peer data sending.
	 */
	public static boolean getUdpSendP2P() {
		return mUdpSendP2P;
	}
	
	/**
	 * mUdpRecipientIp field accessor.
	 * @return remote PC address - data recpient. This option is enabled when peer-to-peer flag is set.
	 */
	public static String getUdpRecipientIp() {
		return mUdpRecipientIp;
	}

	/**
	 * Аксессор поля mRoboBodyMac.
	 * @return MAC-адрес Bluetooth-адаптера контроллера робота.
	 */
	public static String getRoboBodyMac() {
		return mRoboBodyMac;
	}
	
	/**
	 * Длина сообщений, между уровнями ПК, телефон, контроллер.
	 */
	public static final int MESSAGE_LENGTH = 5;
	
	/**
	 * Длина идентификатора в сообщении.
	 */
	public static final int MESSAGE_IDENTIFIER_LENGTH = 1;
	
	/**
	 * Длина значения в сообщении.
	 */
	public static final int MESSAGE_VALUE_LENGTH = 4;
	
	/**
	 * Длительность задержки при смене кадров при изменении выражения лица.
	 */
	public static final int FACE_FRAME_DELAY = 80;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		
		String key;
		String title;
		
		key = getString(R.string.option_udp_receive_port_key);		
		mEditTextPreferenceUdpReceivePort = (EditTextPreference) this.findPreference(key);
		title = getString(R.string.option_udp_receive_port_title) + ": " + mUdpReceivePort;
		mEditTextPreferenceUdpReceivePort.setTitle(title);
		mEditTextPreferenceUdpReceivePort.setOnPreferenceChangeListener(this);
		
		key = getString(R.string.option_udp_send_port_key);		
		mEditTextPreferenceUdpSendPort = (EditTextPreference) this.findPreference(key);
		title = getString(R.string.option_udp_send_port_title) + ": " + mUdpSendPort;
		mEditTextPreferenceUdpSendPort.setTitle(title);
		mEditTextPreferenceUdpSendPort.setOnPreferenceChangeListener(this);

		key = getString(R.string.option_udp_sendtype_broadcast_key);
		mCheckBoxPreferenceUdpSendBroadcast = (CheckBoxPreference) this.findPreference(key);  
		mCheckBoxPreferenceUdpSendBroadcast.setOnPreferenceChangeListener(this);
		
		key = getString(R.string.option_udp_sendtype_broadcast_local_key);
		mCheckBoxPreferenceUdpSendBroadcastLocal = (CheckBoxPreference) this.findPreference(key);  
		mCheckBoxPreferenceUdpSendBroadcastLocal.setOnPreferenceChangeListener(this);

		key = getString(R.string.option_udp_sendtype_p2p_key);
		mCheckBoxPreferenceUdpSendP2P = (CheckBoxPreference) this.findPreference(key);  
		mCheckBoxPreferenceUdpSendP2P.setOnPreferenceChangeListener(this);
		
		key = getString(R.string.option_udp_recipient_ip_key);
		mEditTextPreferenceUdpRecipientIp = (EditTextPreference) this.findPreference(key);
		title = getString(R.string.option_udp_recipient_ip_title) + ": " + mUdpRecipientIp;
		mEditTextPreferenceUdpRecipientIp.setTitle(title);
		mEditTextPreferenceUdpRecipientIp.setOnPreferenceChangeListener(this);

		key = getString(R.string.option_robobody_mac_key);
		mEditTextPreferenceRoboBodyMac = (EditTextPreference) this.findPreference(key);
		title = getString(R.string.option_robobody_mac_title) + ": " + mRoboBodyMac;
		mEditTextPreferenceRoboBodyMac.setTitle(title);
		mEditTextPreferenceRoboBodyMac.setOnPreferenceChangeListener(this);
	}
	
	/**
	 * Инициализация некоторых установок.
	 * @param context контекст приложения.
	 */
	public static void initialize(final Context context) {
		if (context == null) {
			return;
		}
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		
		String key;
		String defaultValue;
		
		key = context.getString(R.string.option_udp_receive_port_key);
		defaultValue = context.getString(R.string.option_udp_receive_port_default_value);
		mUdpReceivePort = Integer.parseInt(settings.getString(key, defaultValue));
		
		key = context.getString(R.string.option_udp_send_port_key);
		defaultValue = context.getString(R.string.option_udp_send_port_default_value);
		mUdpSendPort = Integer.parseInt(settings.getString(key, defaultValue));

		key = context.getString(R.string.option_udp_sendtype_broadcast_key);
		defaultValue = context.getString(R.string.option_udp_sendtype_broadcast_default_value);
		mUdpSendBroadcast = settings.getBoolean(key, Boolean.parseBoolean(defaultValue));
		
		key = context.getString(R.string.option_udp_sendtype_broadcast_local_key);
		defaultValue = context.getString(R.string.option_udp_sendtype_broadcast_local_default_value);
		mUdpSendBroadcastLocal = settings.getBoolean(key, Boolean.parseBoolean(defaultValue));

		key = context.getString(R.string.option_udp_sendtype_p2p_key);
		defaultValue = context.getString(R.string.option_udp_sendtype_p2p_default_value);
		mUdpSendP2P = settings.getBoolean(key, Boolean.parseBoolean(defaultValue));

		key = context.getString(R.string.option_udp_recipient_ip_key);
		defaultValue = context.getString(R.string.option_udp_recipient_ip_default_value);
		mUdpRecipientIp = settings.getString(key, defaultValue);
		
		key = context.getString(R.string.option_robobody_mac_key);
		defaultValue = context.getString(R.string.option_robobody_mac_default_value);
		mRoboBodyMac = settings.getString(key, defaultValue);
	}

	/**
	 * Обработчик листнера изенений настроек.
	 * @param preference изменившаяся опция.
	 * @param newValue новое значение.
	 * @return принять ли изменения.
	 */
	public boolean onPreferenceChange(final Preference preference, final Object newValue) {
		if (preference == null) {
			return false;
		}
		
		if (preference == mEditTextPreferenceUdpReceivePort) {
			String value = (String) newValue;
			mUdpReceivePort = Integer.parseInt(value);
			mEditTextPreferenceUdpReceivePort.setTitle(R.string.option_udp_receive_port_title);
			mEditTextPreferenceUdpReceivePort.setTitle(mEditTextPreferenceUdpReceivePort.getTitle() + ": " + newValue);
			return true;
		} else if (preference == mEditTextPreferenceUdpSendPort) {
			String value = (String) newValue;
			mUdpSendPort = Integer.parseInt(value);
			mEditTextPreferenceUdpSendPort.setTitle(R.string.option_udp_send_port_title);
			mEditTextPreferenceUdpSendPort.setTitle(mEditTextPreferenceUdpSendPort.getTitle() + ": " + newValue);
			return true;
		} else if (preference == mEditTextPreferenceUdpRecipientIp) {
			mUdpRecipientIp = (String) newValue;
			mEditTextPreferenceUdpRecipientIp.setTitle(R.string.option_udp_recipient_ip_title);
			mEditTextPreferenceUdpRecipientIp.setTitle(mEditTextPreferenceUdpRecipientIp.getTitle() + ": " + newValue);
			return true;
		} else if (preference == mEditTextPreferenceRoboBodyMac) {
			mRoboBodyMac = (String) newValue;
			mEditTextPreferenceRoboBodyMac.setTitle(R.string.option_robobody_mac_title);
			mEditTextPreferenceRoboBodyMac.setTitle(mEditTextPreferenceRoboBodyMac.getTitle() + ": " + newValue);
			return true;
		} else if (preference == mCheckBoxPreferenceUdpSendBroadcast) {
			mUdpSendBroadcast = (Boolean) newValue;
			mUdpSendP2P = !mUdpSendBroadcast;
			mCheckBoxPreferenceUdpSendBroadcast.setChecked(mUdpSendBroadcast);
			mCheckBoxPreferenceUdpSendP2P.setChecked(mUdpSendP2P);
		} else if (preference == mCheckBoxPreferenceUdpSendP2P) {
			mUdpSendP2P = (Boolean) newValue;
			mUdpSendBroadcast = !mUdpSendP2P;
			mCheckBoxPreferenceUdpSendBroadcast.setChecked(mUdpSendBroadcast);
			mCheckBoxPreferenceUdpSendP2P.setChecked(mUdpSendP2P);
		} else if (preference == mCheckBoxPreferenceUdpSendBroadcastLocal) {
			mUdpSendBroadcastLocal = (Boolean) newValue;
			mCheckBoxPreferenceUdpSendBroadcastLocal.setChecked(mUdpSendBroadcastLocal);
		}

		return false;
	}
}
