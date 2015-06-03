package com.rhapsodyman.learndroid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.rhapsodyman.learndroid.Bluetooth.ConnectedThread;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Programming extends ActionBarActivity implements OnClickListener {
	private TextView commands;
	private SeekBar countBar, timeBar;
	private TextView timeText, countText;
	private DBHelper helper;
	private DBrecord[] values;
	private SharedPreferences appPreferences;
	private String speed;
	private ConnectedThread connection;
	private Toolbar toolbar;

	private List<String> timeList = Arrays.asList("backward", "left", "right",
			"forward");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programming);
		
		
		toolbar = (Toolbar) findViewById(R.id.toolbar_pr);
		setSupportActionBar(toolbar);

		commands = (TextView) findViewById(R.id.displaytext);
		timeBar = (SeekBar) findViewById(R.id.timebar);
		countBar = (SeekBar) findViewById(R.id.countbar);

		countText = (TextView) findViewById(R.id.counttext);
		timeText = (TextView) findViewById(R.id.timetext);

		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.load).setOnClickListener(this);
		findViewById(R.id.execute).setOnClickListener(this);

		LinearLayout butt = (LinearLayout) findViewById(R.id.buttonslayout);

		for (int i = 0; i < butt.getChildCount(); i++) {
			butt.getChildAt(i).setOnLongClickListener(listener);

		}

		timeText.setText(String.format("%s%s/%s", "(sec) ",
				timeBar.getProgress(), timeBar.getMax()));
		countText.setText(String.format("%s%s/%s", "¹     ",
				countBar.getProgress(), countBar.getMax()));

		timeBar.setOnSeekBarChangeListener(seekbarlistener);
		countBar.setOnSeekBarChangeListener(seekbarlistener);

		helper = new DBHelper(this);

		connection = Bluetooth.getConnection();
		if (connection == null)
			toast("Couldn't get connecton");

		appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		speed = appPreferences.getString("def_speed", null);

	}

	public void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	final OnLongClickListener listener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			String resname = getResources().getResourceEntryName(v.getId());

			if (timeList.contains(resname)) {
				commands.append(String.format("%s %s\n", resname,
						timeBar.getProgress()));

			} else if (resname.equals("repeat")) {

				commands.append(String.format("%s %s\n{\n", resname,
						countBar.getProgress()));
			}

			else if (resname.equals("brace")) {
				commands.append("}\n");

			}

			return true;
		}
	};

	final OnSeekBarChangeListener seekbarlistener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (seekBar == timeBar)
				timeText.setText(String.format("%s%s/%s", "(sec) ", progress,
						seekBar.getMax()));
			else
				countText.setText(String.format("%s%s/%s", "¹     ", progress,
						seekBar.getMax()));
		}
	};

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.save:
			if (checkForErrors(commands.getText().toString()) == true) {
				toast("Error, Unmatched braces");
				break;

			}
			AlertDialog.Builder adb = new AlertDialog.Builder(this);

			adb.setTitle("Save program");
			adb.setMessage("Enter program name");
			final EditText input = new EditText(this);
			adb.setView(input);
			adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String name = input.getText().toString();
					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					helper.addRecord(name, dateFormat.format(date), commands
							.getText().toString());
				}
			});

			AlertDialog alertDialog = adb.create();
			alertDialog.show();

			break;
		case R.id.load:
			values = helper.getRecords();
			if (values == null)
				Toast.makeText(getApplicationContext(), "No records found",
						Toast.LENGTH_LONG).show();

			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Select Program");
				ListView itemlist = new ListView(this);

				DBrecordAdapter adapter = new DBrecordAdapter(this, values);
				itemlist.setAdapter(adapter);

				builder.setView(itemlist);
				final AlertDialog dialog = builder.create();

				itemlist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long rowId) {

						commands.setText(values[position].getContent());
						dialog.dismiss();
					}
				});
				dialog.show();

			}
			break;
		case R.id.execute:
			if (checkForErrors(commands.getText().toString()) == true) {
				toast("Error, Unmatched braces");
				break;

			}

			final ParseExecute pe = new ParseExecute(connection, speed);

			String result = pe.parse(commands.getText().toString());

			AlertDialog.Builder dbuilder = new AlertDialog.Builder(this);

			dbuilder.setTitle("Programm Results");

			LayoutInflater li = LayoutInflater.from(this);
			View prompt = li.inflate(R.layout.parseres, null);
			final TextView parseres = (TextView) prompt
					.findViewById(R.id.parseres);

			parseres.setText(result);
			dbuilder.setView(prompt);
			dbuilder.setPositiveButton("Execute",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							new Thread(new Runnable() {
								public void run() {
									pe.execute(parseres.getText().toString());
								}
							}).start();

						}
					});
			dbuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

			AlertDialog resDialog = dbuilder.create();
			resDialog.show();
			break;

		}
	}

	public boolean checkForErrors(String source) {
		char[] array = source.toCharArray();

		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '{')
				sum += 1;
			if (array[i] == '}')
				sum -= 1;

		}

		if (sum == 0)
			return false;
		else
			return true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Drop Table");
		menu.add(0, 2, 1, "New Program");
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle() == "Drop Table")
			helper.dropTable();
		else if (item.getTitle() == "New Program") {

			commands.setText("");

		}
		return super.onOptionsItemSelected(item);

	}
}
