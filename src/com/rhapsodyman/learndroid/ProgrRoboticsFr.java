package com.rhapsodyman.learndroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ProgrRoboticsFr extends Fragment {
	private String[] titles;
	private String[] images;
	private String[] contents;
	private ListItem[] items;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.about_prog, container, false);
		titles = getResources().getStringArray(R.array.titles);
		images = getResources().getStringArray(R.array.images);
		contents = getResources().getStringArray(R.array.item_content);
		
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		int count = titles.length;
		items = new ListItem[count];
		
		for (int i = 0; i < count; i++) {
			items[i] = new ListItem();
			items[i].setTitle(titles[i]);
			items[i].setImage(images[i]);

		}

		final ListView listview = (ListView) ((MainActivity) getActivity())
				.findViewById(R.id.listview);
		final ListItemAdapter adapter = new ListItemAdapter(
				((MainActivity) getActivity()), items);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				String text = contents[position];
				String content = null;
				if (position == 2) {
					content = "image";
				} else {
					content = text;
				}
				Intent intent = new Intent(getActivity(), Display.class);
				intent.putExtra("content", content);
				startActivity(intent);

			}
		});
		listview.setDivider(null);

	}
}
