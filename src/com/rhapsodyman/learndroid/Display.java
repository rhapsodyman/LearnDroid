package com.rhapsodyman.learndroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Display extends Activity {

	private ScrollView sv;
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_content);

		String fontPath = "fonts/Klinic.otf";

		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		tv = (TextView) findViewById(R.id.textView1);
		tv.setTypeface(tf);

		sv = (ScrollView) findViewById(R.id.scroll2);

		Intent intent = getIntent();

		String content = intent.getStringExtra("content");
		if (content.equals("image")) {
			ImageView image = new ImageView(this);
			image.setImageResource(R.drawable.languages2);
			sv.addView(image);
		} else

			tv.setText(Html.fromHtml(content));
	}

}
