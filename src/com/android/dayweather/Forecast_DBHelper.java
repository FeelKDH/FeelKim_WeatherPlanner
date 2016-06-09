package com.android.dayweather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Forecast_DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "db_Forecast";
	private static final String TABLE_FORECAST = "table_Forecast";

	//ID : ForecastDB의 id값 저장	//WEA : 날씨	//TEM : 온도	//RAIN : 강수량	//TIM : 시간	//HUM : 습도	
	private static final String F_KEY_ID = "f_id";
	private static final String F_KEY_WEA = "f_wea";
	private static final String F_KEY_TEM = "f_tem";
	private static final String F_KEY_RAIN = "f_rain";
	private static final String F_KEY_TIM = "f_tim";
	private static final String F_KEY_HUM = "f_hum";
	
	public Forecast_DBHelper(Context weather) {
		super(weather, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_FORECAST + "(" + F_KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + F_KEY_WEA+ " TEXT, "
				+ F_KEY_TEM + " TEXT, " + F_KEY_RAIN + " TEXT, "
				+ F_KEY_TIM + " TEXT, " + F_KEY_HUM + " TEXT);";
		db.execSQL(CREATE_LOCATION_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST);
		onCreate(db);
	}

	public void addDbInfoClass(Forecast_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		// table rows
		values.put(F_KEY_WEA, DbInfoClass.getWea());
		values.put(F_KEY_TEM, DbInfoClass.getTem());
		values.put(F_KEY_RAIN, DbInfoClass.getRain());
		values.put(F_KEY_TIM, DbInfoClass.getTim());
		values.put(F_KEY_HUM, DbInfoClass.getHum());
		
		// inserting row
		db.insert(TABLE_FORECAST, null, values);
		db.close(); // closing database connection
	}

	// id 에 해당하는 DbInfoClass 객체 가져오기
	public Forecast_DbInfoClass getDbInfoClass(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_FORECAST, new String[] { F_KEY_ID,
				F_KEY_WEA, F_KEY_TEM, F_KEY_RAIN, F_KEY_TIM,
				F_KEY_HUM }, F_KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		Forecast_DbInfoClass DbInfoClass = new Forecast_DbInfoClass(
				Integer.parseInt(cursor.getString(0)), cursor.getString(1),
				cursor.getString(2), cursor.getString(3), cursor.getString(4),
				cursor.getString(5));

		return DbInfoClass;
	}

	// 모든 DbInfoClass 정보 가져오기
	public List<Forecast_DbInfoClass> getAllDbInfoClasss() {
		List<Forecast_DbInfoClass> DbInfoClassList = new ArrayList<Forecast_DbInfoClass>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_FORECAST;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Forecast_DbInfoClass DbInfoClass = new Forecast_DbInfoClass();
				DbInfoClass.setId(Integer.parseInt(cursor.getString(0)));
				DbInfoClass.setWea(cursor.getString(1));
				DbInfoClass.setTem(cursor.getString(2));
				DbInfoClass.setRain(cursor.getString(3));
				DbInfoClass.setTim(cursor.getString(4));
				DbInfoClass.setHum(cursor.getString(5));

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
		db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"	+ TABLE_FORECAST + "'");
	}

	// DbInfoClass 정보 업데이트
	public int updateDbInfoClass(Forecast_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		// table rows
		values.put(F_KEY_WEA, DbInfoClass.getWea());
		values.put(F_KEY_TEM, DbInfoClass.getTem());
		values.put(F_KEY_RAIN, DbInfoClass.getRain());
		values.put(F_KEY_TIM, DbInfoClass.getTim());
		values.put(F_KEY_HUM, DbInfoClass.getHum());
		
		return db.update(TABLE_FORECAST, values, F_KEY_ID + " = ?",
				new String[] { String.valueOf(DbInfoClass.getId()) });
	}

	// DbInfoClass 정보 삭제하기
	public void deleteDbInfoClass(Forecast_DbInfoClass DbInfoClass) {
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
		String deleteSQL = "DELETE FROM " + TABLE_FORECAST;
		db.execSQL(deleteSQL);
	}

	// DbInfoClass 정보 숫자
	public int getDbInfoClasssCount() {
		Cursor cursor = null;

		try {
			String countQuery = "SELECT * FROM " + TABLE_FORECAST;
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
