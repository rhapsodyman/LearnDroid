package com.rhapsodyman.learndroid;

import java.util.ArrayList;

import com.rhapsodyman.learndroid.Bluetooth.ConnectedThread;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Speech extends Activity {

	private ConnectedThread connection;
	private String[] english = { "forward", "backward", "left", "right" };
	private String language;
	private ArrayList<String> matches;
	private String[] russian = { "вперед", "назад", "налево", "направо" };
	private SharedPreferences appPreferences;

	private String speed;
	private TextView speechOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.speech_activ);

		//getActionBar().hide();
		speechOut = (TextView) findViewById(R.id.speech);
		findViewById(R.id.btnSpeak).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SpeechRecognitionHelper.run(Speech.this, language);

			}
		});

		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		language = appPreferences.getString("language", null);
		speed = appPreferences.getString("def_speed", null);

		connection = Bluetooth.getConnection();
		if (connection == null)
			toast("Couldn't get connecton");

	}

	public void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case SpeechRecognitionHelper.VOICE_RECOGNITION_REQUEST_CODE: {

			if (resultCode == RESULT_OK && null != data) {

				matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				StringBuilder sb = new StringBuilder();
				for (String str : matches) {
					sb.append(str + " ");
				}

				speechOut.setText(sb);
				parseSpeechResults();

			}
			break;
		}

		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	public void parseSpeechResults() {
		boolean foundCommand = false;
		String[] pattern = null;

		if (language.equals("ru-RU"))
			pattern = russian;

		else if (language.equals("en-US"))
			pattern = english;

		for (String item : matches) {
			if (item.equals(pattern[0])) {
				connection.write(String.format("L%sR%sw", speed, speed));
				foundCommand = true;
				break;
			} else if (item.equals(pattern[1])) {
				connection.write(String.format("L-%sR-%sw", speed, speed));
				foundCommand = true;
				break;

			} else if (item.equals(pattern[2])) {
				connection.write(String.format("L140R%sw", speed));
				foundCommand = true;
				break;

			} else if (item.equals(pattern[3])) {
				connection.write(String.format("L%sR140w", speed));
				foundCommand = true;
				break;
			}
		}

		if (foundCommand == true) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {

			}
			stop();
		}
	}

	public void stop() {
		connection.write("L0R0w");
	}

}
