package com.android.alarm;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final int A_DATABASE_VERSION = 1;

	private static final String A_DATABASE_NAME = "db_Alarm";
	private static final String A_TABLE_ALARM = "table_Alarm";

	private static final String A_KEY_ID = "a_id";
	private static final String A_KEY_HOUR = "a_hour";
	private static final String A_KEY_MINUTE = "a_min";
	private static final String A_KEY_CHECK = "a_check";
	private static final String A_KEY_TIME = "a_time";

	public DBHelper(Context context) {
		super(context, A_DATABASE_NAME, null, A_DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String CREATE_ALARM_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ A_TABLE_ALARM + "(" + A_KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + A_KEY_HOUR
				+ " TEXT, " + A_KEY_MINUTE + " TEXT, " + A_KEY_CHECK
				+ " TEXT, " + A_KEY_TIME + " TEXT);";
		db.execSQL(CREATE_ALARM_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + A_TABLE_ALARM);
		onCreate(db);
	}

	public void addDbInfoClass(A_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		// table rows
		values.put(A_KEY_HOUR, DbInfoClass.getHour());
		values.put(A_KEY_MINUTE, DbInfoClass.getMin());
		values.put(A_KEY_CHECK, DbInfoClass.getCheck());
		values.put(A_KEY_TIME, DbInfoClass.getTime());

		// inserting row
		db.insert(A_TABLE_ALARM, null, values);
		db.close(); // closing database connection
	}

	// id 에 해당하는 DbInfoClass 객체 가져오기
	public A_DbInfoClass getDbInfoClass(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(A_TABLE_ALARM, new String[] { A_KEY_ID,
				A_KEY_HOUR, A_KEY_MINUTE, A_KEY_CHECK, A_KEY_TIME }, A_KEY_ID
				+ "=?", new String[] { String.valueOf(id) }, null, null, null,
				null);

		if (cursor != null)
			cursor.moveToFirst();

		A_DbInfoClass DbInfoClass = new A_DbInfoClass(Integer.parseInt(cursor
				.getString(0)), cursor.getString(1), cursor.getString(2),
				cursor.getString(3));

		return DbInfoClass;
	}

	// hour 에 해당하는 DbInfoClass 정보 가져오기
	public List<A_DbInfoClass> getDbInfoClass(String hour) {
		List<A_DbInfoClass> DbInfoClassList = new ArrayList<A_DbInfoClass>();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(A_TABLE_ALARM, new String[] { A_KEY_ID,
				A_KEY_HOUR, A_KEY_MINUTE, A_KEY_CHECK, A_KEY_TIME }, A_KEY_HOUR
				+ "=?", new String[] { hour }, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				A_DbInfoClass DbInfoClass = new A_DbInfoClass();
				DbInfoClass.setID(Integer.parseInt(cursor.getString(0)));
				DbInfoClass.setHour(cursor.getString(1));
				DbInfoClass.setMin(cursor.getString(2));
				DbInfoClass.setCheck(cursor.getString(3));
				DbInfoClass.setTime(cursor.getString(4));

				// Adding DbInfoClass to list
				DbInfoClassList.add(DbInfoClass);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return DbInfoClassList;
	}

	// 모든 DbInfoClass 정보 가져오기
	public List<A_DbInfoClass> getAllDbInfoClasss() {
		List<A_DbInfoClass> DbInfoClassList = new ArrayList<A_DbInfoClass>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + A_TABLE_ALARM;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				A_DbInfoClass DbInfoClass = new A_DbInfoClass();
				DbInfoClass.setID(Integer.parseInt(cursor.getString(0)));
				DbInfoClass.setHour(cursor.getString(1));
				DbInfoClass.setMin(cursor.getString(2));
				DbInfoClass.setCheck(cursor.getString(3));
				DbInfoClass.setTime(cursor.getString(4));

				// Adding DbInfoClass to list
				DbInfoClassList.add(DbInfoClass);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		// return DbInfoClass list
		return DbInfoClassList;
	}

	// Auto Incresing Value
	public void init_AutoIncrementValue() {
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + A_TABLE_ALARM
				+ "'");
	}

	// DbInfoClass 정보 업데이트
	public int updateDbInfoClass(A_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		// table rows
		values.put(A_KEY_HOUR, DbInfoClass.getHour());
		values.put(A_KEY_MINUTE, DbInfoClass.getMin());
		values.put(A_KEY_CHECK, DbInfoClass.getCheck());
		values.put(A_KEY_TIME, DbInfoClass.getTime());

		return db.update(A_TABLE_ALARM, values, A_KEY_ID + " = ?",
				new String[] { String.valueOf(DbInfoClass.getID()) });

	}

	// DbInfoClass 정보 삭제하기
	public void deleteDbInfoClass(A_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(A_TABLE_ALARM, A_KEY_ID + " ='" + DbInfoClass.getID() + "'",
				null);
		db.close();
	}

	// Db 모든정보 삭제
	public void deleteAllDbInfoClass() {
		SQLiteDatabase db = this.getWritableDatabase();
		String deleteSQL = "DELETE FROM " + A_TABLE_ALARM;
		db.execSQL(deleteSQL);
	}

	// DbInfoClass 정보 숫자
	public int getDbInfoClasssCount() {
		Cursor cursor = null;

		try {
			String countQuery = "SELECT * FROM " + A_TABLE_ALARM;
			SQLiteDatabase db = this.getReadableDatabase();
			cursor = db.rawQuery(countQuery, null);
			return cursor.getCount();
		} catch (Exception e) {
		}

		cursor.close();
		return 0;

		// return count
	}
}
