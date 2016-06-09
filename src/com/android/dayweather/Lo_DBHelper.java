package com.android.dayweather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Lo_DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "db_Location";
	private static final String TABLE_LOCATION = "table_Location";
	
	//ID : LocationDB에서 사용 되는 ID	//LOCATION : 위치 저장
	//WEATHER : 날씨정보 저장	//TEMPER : 기온저장		//WIND :풍속 저장 
	private static final String L_KEY_ID = "w_id";
	private static final String L_KEY_LOCATION = "l_location";
	private static final String L_KEY_WEATHER = "l_weather";
	private static final String L_KEY_TEMPER = "l_temper";
	private static final String L_KEY_WIND = "l_wind";

	public Lo_DBHelper(Context weather) {
		super(weather, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_LOCATION + "(" + L_KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + L_KEY_LOCATION
				+ " TEXT, " + L_KEY_WEATHER + " TEXT, " + L_KEY_TEMPER
				+ " TEXT, " + L_KEY_WIND + " TEXT);";
		db.execSQL(CREATE_LOCATION_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		onCreate(db);
	}

	public void addDbInfoClass(Lo_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		// table rows
		values.put(L_KEY_LOCATION, DbInfoClass.getLocation());
		values.put(L_KEY_WEATHER, DbInfoClass.getWeather());
		values.put(L_KEY_TEMPER, DbInfoClass.getTemper());
		values.put(L_KEY_WIND, DbInfoClass.getWind());

		// inserting row
		db.insert(TABLE_LOCATION, null, values);
		db.close(); // closing database connection
	}

	// id 에 해당하는 DbInfoClass 객체 가져오기
	public Lo_DbInfoClass getDbInfoClass(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_LOCATION, new String[] { L_KEY_ID,
				L_KEY_LOCATION, L_KEY_WEATHER, L_KEY_TEMPER, L_KEY_WIND },
				L_KEY_ID + "=?", new String[] { String.valueOf(id) }, null,
				null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		Lo_DbInfoClass DbInfoClass = new Lo_DbInfoClass(Integer.parseInt(cursor
				.getString(0)), cursor.getString(1), cursor.getString(2),
				cursor.getString(3), cursor.getString(4));

		return DbInfoClass;
	}

	// 모든 DbInfoClass 정보 가져오기
	public List<Lo_DbInfoClass> getAllDbInfoClasss() {
		List<Lo_DbInfoClass> DbInfoClassList = new ArrayList<Lo_DbInfoClass>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_LOCATION;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Lo_DbInfoClass DbInfoClass = new Lo_DbInfoClass();
				DbInfoClass.setId(Integer.parseInt(cursor.getString(0)));
				DbInfoClass.setLocation(cursor.getString(1));
				DbInfoClass.setWeather(cursor.getString(2));
				DbInfoClass.setTemper(cursor.getString(3));
				DbInfoClass.setWind(cursor.getString(4));

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

		db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"
				+ TABLE_LOCATION + "'");
	}

	// DbInfoClass 정보 업데이트
	public int updateDbInfoClass(Lo_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		// table rows
		values.put(L_KEY_LOCATION, DbInfoClass.getLocation());
		values.put(L_KEY_WEATHER, DbInfoClass.getWeather());
		values.put(L_KEY_TEMPER, DbInfoClass.getTemper());
		values.put(L_KEY_WIND, DbInfoClass.getWind());

		return db.update(TABLE_LOCATION, values, L_KEY_ID + " = ?",
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
		String deleteSQL = "DELETE FROM " + TABLE_LOCATION;
		db.execSQL(deleteSQL);
	}

	// DbInfoClass 정보 숫자
	public int getDbInfoClasssCount() {
		Cursor cursor = null;

		try {
			String countQuery = "SELECT * FROM " + TABLE_LOCATION;
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
