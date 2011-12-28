package ru.dzakhov;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

//2.3.4
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
//3.1
//import android.hardware.usb.UsbAccessory;
//import android.hardware.usb.UsbManager;

/**
 * ������� ��������, ���������� �������������� � ��������� (USB Host Shield + Arduino).
 * @author ������� (� ��� �����)
 *
 */
public abstract class AccessoryBaseActivity extends Activity {
	/**
	 * ������-�������������.
	 */
	private static final String ACTION_USB_PERMISSION = "ru.dzakhov.RoboHead.action.USB_PERMISSION";

	/**
	 * mPermissionIntent.
	 */
	private PendingIntent mPermissionIntent;
	
	/**
	 * mPermissionRequestPending.
	 */
	private boolean mPermissionRequestPending;
	
	/**
	 * �������� USB.
	 */
	private UsbManager mUsbManager;
	
	/**
	 * USB-����.
	 */
	private UsbAccessory mUsbAccessory;
	
	/**
	 * ������ ��� �������������� � ����������� Open Accessory.
	 */
	private Accessory mOpenAccessory;
	
	/**
	 * ������ ������� �������������� � ����������� Open Accessory.
	 * @return ������ ��� �������������� � ����������� Open Accessory.
	 */
	protected final Accessory getOpenAccessory() {
		return mOpenAccessory;
	}

	/**
	 * ���������� � ����������� � USB-����. ���������������� � RoboHeadActivity.
	 */
	protected void onUsbAtached() { };

	/**
	 * ���������� �� ���������� �� USB-�����. ���������������� � RoboHeadActivity.
	 */
	protected void onUsbDetached() { };

	/**
	 * BroadcastReceiver, ����������� ������ �� USB.
	 */
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		/**
		 * ���������� ��� ����� ������.
		 */
		@Override
		public void onReceive(final Context context, final Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent); //2.3.4
					// UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY); //3.1
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (mOpenAccessory.open(accessory)) {
							mUsbAccessory = accessory;
							onUsbAtached();
						}
					} else {
						Logger.d("permission denied for accessory " + accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent); //2.3.4
				//UsbAccessory accessory = (UsbAccessory)intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY); //3.1
				if (accessory != null && accessory.equals(mUsbAccessory)) {
					mOpenAccessory.close();
					onUsbDetached();
				}
			}
		}
	};

	/**
	 * �����, ������������ ��� �������� ��������.
	 * @param savedInstanceState ����� ���������� ��������� ����������.
	 */
	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUsbManager = UsbManager.getInstance(this); //2.3.4
		//mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE); //3.1
		mOpenAccessory = new Accessory(mUsbManager);
		
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		// ��. onRetainNonConfigurationInstance.
		if (getLastNonConfigurationInstance() != null) {
			mUsbAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			mOpenAccessory.open(mUsbAccessory);
			onUsbAtached();
		}
		
		afterOnCreate(savedInstanceState);
	}
	
	/**
	 * ����� ������������� � ������ RoboHeadActivity.
	 * @param savedInstanceState ����� ���������� ��������� ����������.
	 */
	protected abstract void afterOnCreate(final Bundle savedInstanceState);

	/**
	 * ���������� �������� ��� ������������ ��������.
	 * @return ��������� ������ mUsbAccessory, ����� ������������ � onCreate � �������
	 * getLastNonConfigurationInstance().
	 */
	@Override
	public final Object onRetainNonConfigurationInstance() {
		if (mUsbAccessory != null) {
			return mUsbAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}

	/**
	 * ����� ���������� ��� �������������� ��������.
	 */
	@Override
	protected final void onResume() {
		super.onResume();

		//Intent intent = getIntent();
		if (mOpenAccessory.isConnected()) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		//UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		UsbAccessory accessory;
		if (accessories == null) {
			accessory = null;
		} else {
			accessory = accessories[0];
		}
		
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				if (mOpenAccessory.open(accessory)) {
					mUsbAccessory = accessory;
					onUsbAtached();
				}
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory, mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Logger.d("mAccessory is null");
		}
		
		afterOnResume();
	}

	/**
	 * ����� ������������� � ������ RoboHeadActivity.
	 */
	protected abstract void afterOnResume();

	/**
	 * ����� ���������� ��� �������� �������� � ��������� �����.
	 */
	@Override
	protected final void onPause() {
		mOpenAccessory.close();
		mUsbAccessory = null;
		super.onPause();
		
		afterOnPause();
	}

	/**
	 * ����� ������������� � ������ RoboHeadActivity.
	 */
	protected abstract void afterOnPause();

	/**
	 * ����� ���������� ��� �������� ��������.
	 */
	@Override
	protected final void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
		
		afterOnDestroy();
	}

	/**
	 * ����� ������������� � ������ RoboHeadActivity.
	 */
	protected abstract void afterOnDestroy();
}
