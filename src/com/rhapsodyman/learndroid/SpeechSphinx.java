package com.rhapsodyman.learndroid;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rhapsodyman.learndroid.Bluetooth.ConnectedThread;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class SpeechSphinx extends Activity implements RecognitionListener {

	private ConnectedThread connection;
	private SharedPreferences appPreferences;

	private String speed;
	private String recogResult;

	/* Named searches allow to quickly reconfigure the decoder */
	private static final String KWS_SEARCH = "keyword";
	private static final String DRIVE_SEARCH = "drive";

	/* Keyword we are looking for to activate comands recognition */
	private static final String KEYPHRASE = "wakeup";

	private SpeechRecognizer recognizer;
	private TextView status, result;

	public void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);

		setContentView(R.layout.speech_sphinx);

		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		// language = appPreferences.getString("language", null);
		speed = appPreferences.getString("def_speed", null);

		connection = Bluetooth.getConnection();
		if (connection == null)
			toast("Couldn't get connecton");

		result = (TextView) findViewById(R.id.speech);

		findViewById(R.id.btnSpeak).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switchSearch(DRIVE_SEARCH);

			}
		});

		// Recognizer initialization is a time-consuming and it involves IO,
		// so we execute it in async task

		new AsyncTask<Void, Void, Exception>() {
			@Override
			protected Exception doInBackground(Void... params) {
				try {
					Assets assets = new Assets(getApplicationContext());
					File assetDir = assets.syncAssets();
					setupRecognizer(assetDir);
				} catch (IOException e) {
					return e;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Exception result) {
				if (result != null) {
					status.setText("Failed to init recognizer " + result);
				} else {
					switchSearch(KWS_SEARCH);
				}
			}
		}.execute();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		recognizer.cancel();
		recognizer.shutdown();
	}

	/**
	 * In partial result we get quick updates about current hypothesis. In
	 * keyword spotting mode we can react here, in other modes we need to wait
	 * for final result in onResult.
	 */
	@Override
	public void onPartialResult(Hypothesis hypothesis) {

		if (hypothesis == null) {

			return;
		}

		String text = hypothesis.getHypstr();
		if (text.equals(KEYPHRASE))
			switchSearch(DRIVE_SEARCH);

		else
			result.setText(text);
	}

	/**
	 * This callback is called when we stop the recognizer.
	 */
	@Override
	public void onResult(Hypothesis hypothesis) {

		result.setText("");
		if (hypothesis != null) {
			recogResult = hypothesis.getHypstr();
			makeText(getApplicationContext(), recogResult, Toast.LENGTH_SHORT)
					.show();

			if (recogResult.equals(KEYPHRASE))
				return;

			new Thread(new Runnable() {

				@Override
				public void run() {

					List<String> strings = Arrays.asList(new String[] {
							"forward", "backward", "left", "right" });

					int index = strings.indexOf(recogResult);

					switch (index) {
					case 0:
						connection.write(String.format("L%sR%sw", speed, speed));
						break;
					case 1:
						connection.write(String.format("L-%sR-%sw", speed,
								speed));
						break;

					case 2:
						connection.write(String.format("L140R%sw", speed));
						break;

					case 3:
						connection.write(String.format("L%sR140w", speed));
						break;

					}

					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {

					}
					connection.write("L0R0w");

				}

			}).start();
		}
	}

	@Override
	public void onBeginningOfSpeech() {
	}

	/**
	 * We stop recognizer here to get a final result
	 */
	@Override
	public void onEndOfSpeech() {
		if (!recognizer.getSearchName().equals(KWS_SEARCH))
			switchSearch(KWS_SEARCH);
	}

	private void switchSearch(String searchName) {
		recognizer.stop();

		// If we are not spotting, start listening with timeout (5000 ms or 5
		// seconds).
		if (searchName.equals(KWS_SEARCH))
			recognizer.startListening(searchName);
		else
			recognizer.startListening(searchName, 3000);

	}

	private void setupRecognizer(File assetsDir) throws IOException {

		File modelsDir = new File(assetsDir, "models");
		recognizer = defaultSetup()
				.setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
				.setDictionary(new File(modelsDir, "dict/en.dic"))
				.setRawLogDir(assetsDir).setKeywordThreshold(1e-12f)
				.getRecognizer();
		recognizer.addListener(this);

		File driveGrammar = new File(modelsDir, "grammar/drive_en.gram");
		recognizer.addGrammarSearch(DRIVE_SEARCH, driveGrammar);

		// Create keyword-activation search.
		recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

	}

	@Override
	public void onError(Exception error) {
		status.setText(error.getMessage());
	}

	@Override
	public void onTimeout() {
		switchSearch(KWS_SEARCH);
	}
}
