package com.rhapsodyman.learndroid;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutFr extends Fragment {
	private String[] links;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.about, container, false);

		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.links);
		links = getResources().getStringArray(R.array.links);

		for (String str : links) {
			TextView tv = new TextView(getActivity());
			tv.setAutoLinkMask(Linkify.ALL);

			tv.setTextSize(16);
			//tv.setTextColor(Color.WHITE);
			tv.setText(str);
			tv.setPadding(0, 5, 0, 5);
			
			layout.addView(tv);

		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((MainActivity)getActivity()).findViewById(R.id.facebook).setOnClickListener(myclick);
		((MainActivity)getActivity()).findViewById(R.id.github).setOnClickListener(myclick);
		
	}
	
	OnClickListener myclick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.facebook:
				Intent facebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100003530070157"));
				startActivity(facebIntent);
				break;

			case R.id.github:
				Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rhapsodyman"));
				startActivity(gitIntent);
				break;
			}
			
			
		}
	};
}
