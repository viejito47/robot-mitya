package ru.dzakhov;

import android.app.Activity;
import android.os.Bundle;

/**
 * �������� ��� ������ ���������� � ����������.
 * @author �������
 *
 */
public class About extends Activity {
	/**
	 * ���������� ��� ������ ��������.
	 * @param savedInstanceState ����� ���������� ��������� ����������. 
	 */
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}
}
