package com.android.dayweather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class W_DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "db_Weather";
	private static final String TABLE_WEATHER = "table_Weather";

	private static final String W_KEY_ID = "w_id";
	private static final String W_KEY_YEAR = "w_year";
	private static final String W_KEY_MONTH = "w_month";
	private static final String W_KEY_DAY = "w_day";
	private static final String W_KEY_HOUR = "w_hour";
	private static final String W_KEY_WEATHER = "w_weather";
	private static final String W_KEY_TEMPER = "w_temper";
	private static final String W_KEY_HUMIDITY = "w_humidity";
	private static final String W_KEY_WIND = "w_wind";
	private static final String W_KEY_YTEMP = "w_ytemp";

	public W_DBHelper(Context weather) {
		super(weather, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String CREATE_WEATHER_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_WEATHER + "(" + W_KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + W_KEY_YEAR
				+ " TEXT, " + W_KEY_MONTH + " TEXT, " + W_KEY_DAY + " TEXT, "
				+ W_KEY_HOUR + " TEXT, " + W_KEY_WEATHER + " TEXT, "
				+ W_KEY_TEMPER + " TEXT, " + W_KEY_HUMIDITY + " TEXT, "
				+ W_KEY_WIND + " TEXT, "+ W_KEY_YTEMP + " TEXT);";
		db.execSQL(CREATE_WEATHER_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
		onCreate(db);
	}

	public void addDbInfoClass(W_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		// table rows
		values.put(W_KEY_YEAR, DbInfoClass.getYear());
		values.put(W_KEY_MONTH, DbInfoClass.getMonth());
		values.put(W_KEY_DAY, DbInfoClass.getDay());
		values.put(W_KEY_HOUR, DbInfoClass.getHour());
		values.put(W_KEY_WEATHER, DbInfoClass.getWeather());
		values.put(W_KEY_TEMPER, DbInfoClass.getTemper());
		values.put(W_KEY_HUMIDITY, DbInfoClass.getHumidity());
		values.put(W_KEY_WIND, DbInfoClass.getWind());
		values.put(W_KEY_YTEMP, DbInfoClass.getYtemp());

		// inserting row
		db.insert(TABLE_WEATHER, null, values);
		db.close(); // closing database connection
	}

	// id 에 해당하는 DbInfoClass 객체 가져오기
	public W_DbInfoClass getDbInfoClass(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_WEATHER, new String[] { W_KEY_ID,
				W_KEY_YEAR, W_KEY_MONTH, W_KEY_DAY, W_KEY_HOUR, W_KEY_WEATHER,
				W_KEY_TEMPER, W_KEY_HUMIDITY, W_KEY_WIND, W_KEY_YTEMP }, W_KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		W_DbInfoClass DbInfoClass = new W_DbInfoClass(Integer.parseInt(cursor
				.getString(0)), cursor.getString(1), cursor.getString(2),
				cursor.getString(3), cursor.getString(4), cursor.getString(5),
				cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));

		return DbInfoClass;
	}

	// 모든 DbInfoClass 정보 가져오기
	public List<W_DbInfoClass> getAllDbInfoClasss() {
		List<W_DbInfoClass> DbInfoClassList = new ArrayList<W_DbInfoClass>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_WEATHER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				W_DbInfoClass DbInfoClass = new W_DbInfoClass();
				DbInfoClass.setId(Integer.parseInt(cursor.getString(0)));
				DbInfoClass.setYear(cursor.getString(1));
				DbInfoClass.setMonth(cursor.getString(2));
				DbInfoClass.setDay(cursor.getString(3));
				DbInfoClass.setHour(cursor.getString(4));
				DbInfoClass.setWeather(cursor.getString(5));
				DbInfoClass.setTemper(cursor.getString(6));
				DbInfoClass.setHumidity(cursor.getString(7));
				DbInfoClass.setWind(cursor.getString(8));
				DbInfoClass.setYtemp(cursor.getString(9));

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

		db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_WEATHER
				+ "'");
	}

	// DbInfoClass 정보 업데이트
	public int updateDbInfoClass(W_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		// table rows
		values.put(W_KEY_YEAR, DbInfoClass.getYear());
		values.put(W_KEY_MONTH, DbInfoClass.getMonth());
		values.put(W_KEY_DAY, DbInfoClass.getDay());
		values.put(W_KEY_HOUR, DbInfoClass.getHour());
		values.put(W_KEY_WEATHER, DbInfoClass.getWeather());
		values.put(W_KEY_TEMPER, DbInfoClass.getTemper());
		values.put(W_KEY_HUMIDITY, DbInfoClass.getHumidity());
		values.put(W_KEY_WIND, DbInfoClass.getWind());
		values.put(W_KEY_YTEMP, DbInfoClass.getYtemp());

		return db.update(TABLE_WEATHER, values, W_KEY_ID + " = ?",
				new String[] { String.valueOf(DbInfoClass.getId()) });
	}

	// DbInfoClass 정보 삭제하기
	public void deleteDbInfoClass(W_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		// db.delete(TABLE_CALENDER, KEY_HOUR + " ='" + DbInfoClass.getHour()
		// + "' and " + KEY_MINUTE + " ='" + DbInfoClass.getMin() + "'", null);
		// db.delete(TABLE_ALARM, KEY_TIME + " ='" + DbInfoClass.getTime() +
		// "'", null);
		db.close();
	}

	// Db 모든정보 삭제
	public void deleteAllDbInfoClass() {
		SQLiteDatabase db = this.getWritableDatabase();
		String deleteSQL = "DELETE FROM " + TABLE_WEATHER;
		db.execSQL(deleteSQL);
	}

	// DbInfoClass 정보 숫자
	public int getDbInfoClasssCount() {
		Cursor cursor = null;

		try {
			String countQuery = "SELECT * FROM " + TABLE_WEATHER;
			SQLiteDatabase db = this.getReadableDatabase();
			cursor = db.rawQuery(countQuery, null);
			return cursor.getCount();
		} catch (Exception e) {
			// Log.i("test", e.toString());
		}

		cursor.close();
		return 0;
		// return count
	}
}
