package ru.dzakhov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * ������� �������� ����������.
 * @author �������
 *
 */
public final class MainActivity extends Activity {
	/**
	 * ������� ��������.
	 */
	private Handler mHandler = new Handler();

	/**
	 * ���������� ��� �������� ��������.
	 * @param savedInstanceState ����� ���������� ��������� ����������. 
	 */
	public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setRunClickHandler();
        setTestClickHandler();
        setSettingsClickHandler();
        setAboutClickHandler();
        
        Settings.initialize(this);
	}
	
	/**
	 * ��������� ����������� ������ "������".
	 */
	private void setRunClickHandler() {
		Logger.d("MainActivity.setRunClickHandler");
		final Button button = (Button) findViewById(R.id.buttonRun);
		button.setOnClickListener(new OnClickListener() {
    		
    		public void onClick(final View v) {
    			Intent ipwebcam = 
    					new Intent()
    					.setClassName("com.pas.webcam", "com.pas.webcam.Rolling")
    					.putExtra("hidebtn1", true);
    			
    			// ������ �������� � ���������:
    			mHandler.post(new Runnable() {
    				public void run() {
    					startActivity(new Intent(MainActivity.this, RoboHeadActivity.class));
    				}
    			});
    			
    			// ������ �������� � ������������ �������� IP Webcam.
    			// � ���������, IP Webcam �� �������� � ������� ������ �� Android 4.0.3.
    			// ������� �������� ������ �������� IP Webcam ��������� �������� � ���������.
    			startActivityForResult(ipwebcam, 1);
    		}
        });
	}

	/**
	 * ��������� ����������� ������ "����".
	 */
    private void setTestClickHandler() {
		final Button button = (Button) findViewById(R.id.buttonTest);
        button.setOnClickListener(new OnClickListener() {
   		
    		public void onClick(final View v) {

    		}
        });
    }
    
	/**
	 * ��������� ����������� ������ "���������". 
	 */
    private void setSettingsClickHandler() {
		final Button button = (Button) findViewById(R.id.buttonSettings);
        button.setOnClickListener(new OnClickListener() {
       		
    		public void onClick(final View v) {
    			startActivity(new Intent(MainActivity.this, Settings.class));
    		}
        });
    }

	/**
	 * ��������� ����������� ������ "� ����������". 
	 */
    private void setAboutClickHandler() {
		final Button button = (Button) findViewById(R.id.buttonAbout);
        button.setOnClickListener(new OnClickListener() {
       		
    		public void onClick(final View v) {
    			startActivity(new Intent(MainActivity.this, About.class));
    		}
        });
    }
} // class
