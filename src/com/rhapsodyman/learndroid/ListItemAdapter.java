package com.rhapsodyman.learndroid;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListItemAdapter extends ArrayAdapter<ListItem> {
	private final Context context;
	private final ListItem[] values;

	public ListItemAdapter(Context context, ListItem[] values) {
		super(context, R.layout.row_view, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.row_view, parent, false);
		}
		String fontPath = "fonts/Agilis.otf";

		Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);

		TextView textView = (TextView) rowView.findViewById(R.id.text);
		textView.setTypeface(tf);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

		textView.setText(values[position].getTitle());

		int resID = context.getResources().getIdentifier(
				values[position].getImage(), "drawable",
				context.getPackageName());
		imageView.setImageResource(resID);
		return rowView;
	}
}
