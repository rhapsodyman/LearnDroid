package com.rhapsodyman.learndroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "mydb", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table programs("
				+ "id integer primary key autoincrement," + "name text,"
				+ "date text," + "content text" + ")");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("DROP TABLE IF EXISTS programs");

		// onCreate(db);
	}

	public void addRecord(String name, String date, String content) {
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = this.getWritableDatabase();

		cv.put("name", name);
		cv.put("date", date);
		cv.put("content", content);

		db.insert("programs", null, cv);
		db.close();
	}

	public DBrecord[] getRecords() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor c = db.query("programs", null, null, null, null, null, "date");
		int count = c.getCount();
		DBrecord[] values = new DBrecord[count];
		boolean hasentries = true;
		int index = 0;

		if (c.moveToFirst()) {

			int nameColIndex = c.getColumnIndex("name");
			int dateColIndex = c.getColumnIndex("date");
			int contentColIndex = c.getColumnIndex("content");

			do {
				DBrecord tmp = new DBrecord();
				tmp.setName(c.getString(nameColIndex));
				tmp.setDate(c.getString(dateColIndex));
				tmp.setContent(c.getString(contentColIndex));
				values[index] = tmp;
				index++;

			} while (c.moveToNext());
		} else
			hasentries = false;
		c.close();
		if (hasentries)
			return values;
		else
			return null;

	}
	public void dropTable(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("programs", null, null);
	}
}