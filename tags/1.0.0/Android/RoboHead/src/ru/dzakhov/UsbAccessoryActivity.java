package ru.dzakhov;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

/**
 * This Activity does nothing but receive USB_DEVICE_ATTACHED events from the
 * USB service and springboards to the main Gallery activity.
 * @author Не Дмитрий
 *
 */
public final class UsbAccessoryActivity extends Activity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Intent intent = DemoKitLaunch.createIntent(this);
		Intent intent = new Intent(this, RoboHeadActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
		finish();
	}
}
