package com.android.dayweather;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Geo_DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "db_Geo";
	private static final String TABLE_GEO = "table_Geo";

	//ID : GeoDB에서 사용 되는 ID	//DO : 경상남도,전라남도,특별시,광역시 저장
	//CITY : 경상남도 김해시, 부산광역시 강서구 등 도에서는 시를 광역시 특별시에서는 구를 저장	//DONG : oo동 을 저장
	private static final String G_KEY_ID = "g_id";
	private static final String G_KEY_DO = "g_do";
	private static final String G_KEY_CITY = "g_city";
	private static final String G_KEY_DONG = "g_dong";

	public Geo_DBHelper(Context geo_go) {
		super(geo_go, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		String CREATE_GEO_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GEO
				+ "(" + G_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ G_KEY_DO + " TEXT, " + G_KEY_CITY + " TEXT, " + G_KEY_DONG
				+ " TEXT);";
		db.execSQL(CREATE_GEO_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEO);
		onCreate(db);
	}

	public void addDbInfoClass(Geo_DbInfoClass geoInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		// table rows
		values.put(G_KEY_DO, geoInfo.getDo());
		values.put(G_KEY_CITY, geoInfo.getCity());
		values.put(G_KEY_DONG, geoInfo.getDong());

		// inserting row
		db.insert(TABLE_GEO, null, values);
		db.close(); // closing database connection
	}

	// id 에 해당하는 DbInfoClass 객체 가져오기
	public Geo_DbInfoClass getDbInfoClass(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_GEO, new String[] { G_KEY_ID, G_KEY_DO,
				G_KEY_CITY, G_KEY_DONG }, G_KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		Geo_DbInfoClass DbInfoClass = new Geo_DbInfoClass(
				Integer.parseInt(cursor.getString(0)), cursor.getString(1),
				cursor.getString(2), cursor.getString(3));

		return DbInfoClass;
	}

	// 모든 DbInfoClass 정보 가져오기
	public List<Geo_DbInfoClass> getAllDbInfoClasss() {
		List<Geo_DbInfoClass> DbInfoClassList = new ArrayList<Geo_DbInfoClass>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_GEO;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Geo_DbInfoClass DbInfoClass = new Geo_DbInfoClass();
				DbInfoClass.setId(Integer.parseInt(cursor.getString(0)));
				DbInfoClass.setDo(cursor.getString(1));
				DbInfoClass.setCity(cursor.getString(2));
				DbInfoClass.setDong(cursor.getString(3));

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

		db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_GEO
				+ "'");
	}

	// DbInfoClass 정보 업데이트
	public int updateDbInfoClass(Geo_DbInfoClass DbInfoClass) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		// table rows
		values.put(G_KEY_DO, DbInfoClass.getDo());
		values.put(G_KEY_CITY, DbInfoClass.getCity());
		values.put(G_KEY_DONG, DbInfoClass.getDong());

		return db.update(TABLE_GEO, values, G_KEY_ID + " = ?",
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
		String deleteSQL = "DELETE FROM " + TABLE_GEO;
		db.execSQL(deleteSQL);
	}

	// DbInfoClass 정보 숫자
	public int getDbInfoClasssCount() {
		Cursor cursor = null;

		try {
			String countQuery = "SELECT * FROM " + TABLE_GEO;
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
