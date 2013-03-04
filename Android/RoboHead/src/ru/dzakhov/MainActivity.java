package ru.dzakhov;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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
        setSettingsClickHandler();
        setAboutClickHandler();
        
        Settings.initialize(this);
	}
	
	/**
	 * ��������� ����������� ������ "������".
	 */
	private void setRunClickHandler() {
		final Button button = (Button) findViewById(R.id.buttonRun);
		button.setOnClickListener(new OnClickListener() {
    		
    		public void onClick(final View v) {
    			BluetoothHelper.initialize(MainActivity.this);
    			if (!BluetoothHelper.getBluetoothAdapterIsEnabled()) {
    				return;
    			}
    		
    			Intent ipwebcam = new Intent().setClassName("com.pas.webcam", "com.pas.webcam.Rolling");
    			final PackageManager packageManager = getPackageManager();
    			List<ResolveInfo> list = packageManager.queryIntentActivities(ipwebcam, PackageManager.MATCH_DEFAULT_ONLY);
    			if (list.size() == 0) {
    				String errorMessage = MainActivity.this.getResources().getString(R.string.error_no_ipwebcam);
    				Logger.e(errorMessage);
    				Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    				return;
    			}
    			ipwebcam.putExtra("hidebtn1", true);
    			
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
