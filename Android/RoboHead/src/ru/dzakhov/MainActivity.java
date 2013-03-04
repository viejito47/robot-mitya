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
 * Главная активити приложения.
 * @author Дмитрий
 *
 */
public final class MainActivity extends Activity {
	/**
	 * Хэндлер активити.
	 */
	private Handler mHandler = new Handler();

	/**
	 * Вызывается при создании активити.
	 * @param savedInstanceState ранее сохранённое состояние экземпляра. 
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
	 * Установка обработчика кнопки "Запуск".
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
    			
    			// Запуск активити с мордочкой:
    			mHandler.post(new Runnable() {
    				public void run() {
    					startActivity(new Intent(MainActivity.this, RoboHeadActivity.class));
    				}
    			});
    			
    			// Запуск активити с отображением картинки IP Webcam.
    			// К сожалению, IP Webcam не работает в фоновом режиме на Android 4.0.3.
    			// Поэтому пришлось поверх активити IP Webcam открывать активити с мордочкой.
    			startActivityForResult(ipwebcam, 1);
    		}
        });
	}

	/**
	 * Установка обработчика кнопки "Настройки". 
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
	 * Установка обработчика кнопки "О приложении". 
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
