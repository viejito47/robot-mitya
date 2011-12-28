package ru.dzakhov;

import android.app.Activity;
import android.os.Bundle;

/**
 * Активити для вывода информации о приложении.
 * @author Дмитрий
 *
 */
public class About extends Activity {
	/**
	 * Вызывается при старте активити.
	 * @param savedInstanceState ранее сохранённое состояние экземпляра. 
	 */
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}
}
