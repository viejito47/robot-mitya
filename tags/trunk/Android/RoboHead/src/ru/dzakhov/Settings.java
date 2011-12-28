package ru.dzakhov;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Активити настроек приложения.
 * @author Дмитрий
 *
 */
public class Settings extends PreferenceActivity {
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
	}
}
