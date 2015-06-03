package com.rhapsodyman.learndroid;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.widget.Toast;


public class SpeechRecognitionHelper {
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private static String language;
	
	
	public static void run(Activity ownerActivity, String Lang) {
		language = Lang; 

		if (isSpeechRecognitionActivityPresented(ownerActivity) == true) {

			startRecognitionActivity(ownerActivity);
		} else {

			Toast.makeText(ownerActivity, "Speech Recognition not supported",
					Toast.LENGTH_SHORT).show();
		}
	}

	private static boolean isSpeechRecognitionActivityPresented(
			Activity ownerActivity) {
		try {

			PackageManager pm = ownerActivity.getPackageManager();
			List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
					RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

			if (activities.size() != 0) {
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	private static void startRecognitionActivity(Activity ownerActivity) {

	
		Intent myintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		
		myintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...");
		myintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		//myintent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		myintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);

		
		ownerActivity.startActivityForResult(myintent,
				VOICE_RECOGNITION_REQUEST_CODE);
	}

}
