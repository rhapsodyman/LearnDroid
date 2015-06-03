package com.rhapsodyman.learndroid;

import com.rhapsodyman.learndroid.Bluetooth.ConnectedThread;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Buttons extends Activity {

	private ConnectedThread connection;
	private SharedPreferences appPreferences;
	private String speed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buttons);

		findViewById(R.id.forward).setOnTouchListener(mylistener);
		findViewById(R.id.backward).setOnTouchListener(mylistener);
		findViewById(R.id.left).setOnTouchListener(mylistener);
		findViewById(R.id.right).setOnTouchListener(mylistener);

		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		speed = appPreferences.getString("def_speed", null);

		connection = Bluetooth.getConnection();
		if (connection == null)
			toast("Couldn't get connecton");

	}

	OnTouchListener mylistener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				switch (v.getId()) {
				case R.id.forward:
					connection.write(String.format("L%sR%sw", speed, speed));
					break;

				case R.id.backward:
					connection.write(String.format("L-%sR-%sw", speed, speed));
					break;
				case R.id.left:
					connection.write(String.format("L140R%sw", speed));
					break;
				case R.id.right:
					connection.write(String.format("L%sR140w", speed));
					break;
				}

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				connection.write("L0R0w");

			}

			return true;
		}
	};

	public void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

}
