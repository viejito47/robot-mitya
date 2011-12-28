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
 * Базовая активити, реализуюет взаимодействие с аксессори (USB Host Shield + Arduino).
 * @author Дмитрий (в том числе)
 *
 */
public abstract class AccessoryBaseActivity extends Activity {
	/**
	 * Строка-идентификатор.
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
	 * Менеджер USB.
	 */
	private UsbManager mUsbManager;
	
	/**
	 * USB-порт.
	 */
	private UsbAccessory mUsbAccessory;
	
	/**
	 * Объект для взаимодействия с устройством Open Accessory.
	 */
	private Accessory mOpenAccessory;
	
	/**
	 * Геттер объекта взаимодействия с устройством Open Accessory.
	 * @return Объект для взаимодействия с устройством Open Accessory.
	 */
	protected final Accessory getOpenAccessory() {
		return mOpenAccessory;
	}

	/**
	 * Уведомляет о подключении к USB-хост. Переопределяется в RoboHeadActivity.
	 */
	protected void onUsbAtached() { };

	/**
	 * Уведомляет об отключении от USB-хоста. Переопределяется в RoboHeadActivity.
	 */
	protected void onUsbDetached() { };

	/**
	 * BroadcastReceiver, принимающий данные по USB.
	 */
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		/**
		 * Вызывается при приёме данных.
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
	 * Метод, вызывающийся при создании активити.
	 * @param savedInstanceState ранее сохранённое состояние экземпляра.
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

		// см. onRetainNonConfigurationInstance.
		if (getLastNonConfigurationInstance() != null) {
			mUsbAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			mOpenAccessory.open(mUsbAccessory);
			onUsbAtached();
		}
		
		afterOnCreate(savedInstanceState);
	}
	
	/**
	 * Метод перегружается в классе RoboHeadActivity.
	 * @param savedInstanceState ранее сохранённое состояние экземпляра.
	 */
	protected abstract void afterOnCreate(final Bundle savedInstanceState);

	/**
	 * Вызывается системой при пересоздании активити.
	 * @return сохраняет ссылку mUsbAccessory, чтобы восстановить в onCreate с помощью
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
	 * Метод вызывается при восстановлении активити.
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
	 * Метод перегружается в классе RoboHeadActivity.
	 */
	protected abstract void afterOnResume();

	/**
	 * Метод вызывается при переходе активити в состояние паузы.
	 */
	@Override
	protected final void onPause() {
		mOpenAccessory.close();
		mUsbAccessory = null;
		super.onPause();
		
		afterOnPause();
	}

	/**
	 * Метод перегружается в классе RoboHeadActivity.
	 */
	protected abstract void afterOnPause();

	/**
	 * Метод вызывается при закрытии активити.
	 */
	@Override
	protected final void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
		
		afterOnDestroy();
	}

	/**
	 * Метод перегружается в классе RoboHeadActivity.
	 */
	protected abstract void afterOnDestroy();
}
