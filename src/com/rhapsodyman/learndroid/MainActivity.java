package com.rhapsodyman.learndroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.rhapsodyman.learndroid.Bluetooth;
import com.rhapsodyman.learndroid.Bluetooth.ConnectedThread;
import com.rhapsodyman.learndroid.FragmentPreferences;

import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private Toolbar toolbar;
	private ViewPager pager;
	private ViewPagerAdapter adapter;
	private SlidingTabLayout tabs;
	private CharSequence Titles[] = { "DRIVING", "INFO", "ABOUT" };
	private int Numboftabs = 3;

	private Bluetooth bluetooth;

	private static Handler mHandler;
	private static final String TAG = "bluetooth";
	private String MAC_address;
	private String UUID = "00001101-0000-1000-8000-00805f9b34fb";
	public ConnectedThread mBluetoothConnection;

	private SharedPreferences appPreferences;
	private String temperature;
	private boolean startTemperature = false;

	public void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayShowHomeEnabled(true);

		// Creating The ViewPagerAdapter and Passing Fragment Manager, Titles
		// fot the Tabs and Number Of Tabs.
		adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles,
				Numboftabs);

		// Assigning ViewPager View and setting the adapter
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		// Assiging the Sliding Tab Layout View
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		tabs.setDistributeEvenly(true);

		// Setting the ViewPager For the SlidingTabsLayout
		tabs.setViewPager(pager);

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Bluetooth.SOCKET_CONNECTED: {
					mBluetoothConnection = (ConnectedThread) msg.obj;
					break;
				}
				case Bluetooth.DATA_RECEIVED: {

					if (startTemperature == true) {

						if (isOnline()) {
							temperature = (String) msg.obj;
							toast("tmp =  " + temperature);

							new TemperatureThread().start();

						} else {
							toast("Please enable wifi and try again");
							startTemperature = false;
						}

					}
					break;
				}

				case Bluetooth.ERROR_EXIT: {
					errorExit("Fatal error", "unable to connect");
				}

				}
			}
		};

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		MAC_address = appPreferences.getString("MAC_address", null);

		bluetooth = Bluetooth.getInstance(this, mHandler, MAC_address, UUID);

	}

	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");

		if (requestCode == Bluetooth.REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(),
						"BlueTooth is now Enabled", Toast.LENGTH_LONG).show();

				bluetooth.connect();
			}
			if (resultCode == RESULT_CANCELED) {
				errorExit("Bluetooth", "Error occured while enabling");

				finish();
			}
		}
	}

	private void errorExit(String title, String message) {

		Toast.makeText(getBaseContext(), title + " - " + message,
				Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	protected void onDestroy() {

		if (Bluetooth.isSocketCreated) {
			toast("Closing conection");
			mBluetoothConnection.cancel();
			Bluetooth.isSocketCreated = false;
		}

		super.onDestroy();

	}

	private class TemperatureThread extends Thread {

		public TemperatureThread() {
		}

		public void run() {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet("http://simple1.gear.host/"
					+ "?param=" + temperature);
			try {
				HttpResponse response = client.execute(request);
				if (response.getEntity() != null) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					while (reader.readLine() != null) {
					}
					reader.close();
				}
				return;
			} catch (Exception e) {
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
		case R.id.bluetooth:
			bluetooth.checkBTState();
			break;
		case R.id.settings:
			startActivity(new Intent(this, FragmentPreferences.class));
			break;

		case R.id.auton_mode:
			mBluetoothConnection.write("a");
			break;

		case R.id.control_mode:
			mBluetoothConnection.write("p");
			break;

		case R.id.start_temp:
			startTemperature = true;
			break;

		case R.id.stop_temp:
			startTemperature = false;
			break;

		}

		return super.onOptionsItemSelected(item);
	}
}