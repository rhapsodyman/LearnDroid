package com.rhapsodyman.learndroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DBrecordAdapter extends ArrayAdapter<DBrecord> {
	private final Context context;
	private final DBrecord[] values;

	public DBrecordAdapter(Context context, DBrecord[] values) {
		super(context,0, values);
		this.context = context;
		this.values = values;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = convertView;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.dblistitem, parent, false);
		}
		TextView name = (TextView) rowView.findViewById(R.id.recordname);
		TextView date = (TextView) rowView.findViewById(R.id.recorddate);
		
		name.setText(values[position].getName());
		date.setText(values[position].getDate());
		
		return rowView;
	}
}
