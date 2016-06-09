package com.android.dayweather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.weatherplanner.R;

public class Tab_Weather extends Activity {
	SQLiteDatabase db;
	W_DBHelper W_Helper;
	Geo_DBHelper G_Helper;
	Forecast_DBHelper temp;

	List<W_DbInfoClass> DbInfoClassList = new ArrayList<W_DbInfoClass>();
	List<Geo_DbInfoClass> G_DbInfoClassList = new ArrayList<Geo_DbInfoClass>();
	List<Forecast_DbInfoClass> tempt= new ArrayList<Forecast_DbInfoClass>();
	ArrayAdapter<W_DbInfoClass> Adapter;
	List<Lo_DbInfoClass> list;

	ImageView iv_Weather;
	TextView tv_Temper, tv_weaState, tv_humid, tv_wind;
	ReceiverThread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);

		////날씨메뉴 상단의 TextView, ImageView 선언/////////////////////////
		iv_Weather = (ImageView) findViewById(R.id.iv_weather);
		tv_Temper = (TextView) findViewById(R.id.tv_temper);
		tv_weaState = (TextView) findViewById(R.id.tv_weastate);
		tv_humid = (TextView) findViewById(R.id.tv_humid);
		////위치,날씨,시간별날씨 DB선언 및 연동//////////////////////////////////
		W_Helper = new W_DBHelper(getApplicationContext());
		DbInfoClassList = W_Helper.getAllDbInfoClasss();
		G_Helper = new Geo_DBHelper(getApplicationContext());
		G_DbInfoClassList = G_Helper.getAllDbInfoClasss();
		temp = new Forecast_DBHelper(getApplicationContext());
		tempt = temp.getAllDbInfoClasss();

		////날씨메뉴 하단의 시간별 메뉴의 TextView,ImageView 선언 /////////////////////
		////t1,2,3,4 : 시간	//i1,2,3,4 : 날씨아이콘	//te1,2,3,4 : 기온	//s1,2,3,4 : 습도
		if (temp.getDbInfoClasssCount() != 0) {
			// time
			TextView t1 = (TextView) findViewById(R.id.weather_txt1);		TextView t2 = (TextView) findViewById(R.id.weather_txt3);
			TextView t3 = (TextView) findViewById(R.id.weather_txt5);		TextView t4 = (TextView) findViewById(R.id.weather_txt7);
			t1.setText(tempt.get(0).getTim());		t2.setText(tempt.get(1).getTim());
			t3.setText(tempt.get(2).getTim());		t4.setText(tempt.get(3).getTim());

			ImageView i1 = (ImageView) findViewById(R.id.weather_iv1);		ImageView i2 = (ImageView) findViewById(R.id.weather_iv2);
			ImageView i3 = (ImageView) findViewById(R.id.weather_iv3);		ImageView i4 = (ImageView) findViewById(R.id.weather_iv4);
			if (tempt.get(0).getWea().equals("맑음"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i1.setImageDrawable(img);
			}
			else if (tempt.get(0).getWea().equals("구름 많음") || tempt.get(0).getWea().equals("구름 조금")|| tempt.get(0).getWea().equals("흐림"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i1.setImageDrawable(img);
			}
			
			if (tempt.get(1).getWea().equals("맑음"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i2.setImageDrawable(img);
			}
			else if (tempt.get(1).getWea().equals("구름 많음") || tempt.get(0).getWea().equals("구름 조금")|| tempt.get(0).getWea().equals("흐림"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i2.setImageDrawable(img);
			}
			
			if (tempt.get(2).getWea().equals("맑음"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i3.setImageDrawable(img);
			}
			else if (tempt.get(2).getWea().equals("구름 많음") || tempt.get(0).getWea().equals("구름 조금")|| tempt.get(0).getWea().equals("흐림"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i3.setImageDrawable(img);
			}
			
			if (tempt.get(3).getWea().equals("맑음"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i4.setImageDrawable(img);
			}
			else if (tempt.get(3).getWea().equals("구름 많음") || tempt.get(0).getWea().equals("구름 조금")|| tempt.get(0).getWea().equals("흐림"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i4.setImageDrawable(img);
			}
			TextView te1 = (TextView) findViewById(R.id.weather_txt2);	TextView te2 = (TextView) findViewById(R.id.weather_txt4);
			TextView te3 = (TextView) findViewById(R.id.weather_txt6);	TextView te4 = (TextView) findViewById(R.id.weather_txt8);
			te1.setText(tempt.get(0).getTem()+"℃");		te2.setText(tempt.get(1).getTem()+"℃");
			te3.setText(tempt.get(2).getTem()+"℃");		te4.setText(tempt.get(3).getTem()+"℃");

			TextView s1 = (TextView) findViewById(R.id.s1);		TextView s2 = (TextView) findViewById(R.id.s2);
			TextView s3 = (TextView) findViewById(R.id.s3);		TextView s4 = (TextView) findViewById(R.id.s4);
			s1.setText(tempt.get(0).getHum()+"%");		s2.setText(tempt.get(1).getHum()+"%");
			s3.setText(tempt.get(2).getHum()+"%");		s4.setText(tempt.get(3).getHum()+"%");
		}

		//Thread를 이용하여 weather,geo service 실행
		mThread = new ReceiverThread(getApplicationContext());
		mThread.setDaemon(true);
		mThread.start();

		W_Helper = new W_DBHelper(getApplicationContext());
		DbInfoClassList = W_Helper.getAllDbInfoClasss();

		//날씨메뉴 상단의 textView,ImageView에 정보를 넣기위해 함수호출
		if (DbInfoClassList.size() != 0)	{modify_WeatherInfo(W_Helper, DbInfoClassList);}
	}

	@Override
	protected void onResume() {		// TODO Auto-generated method stub
		super.onResume();
	}

	void modify_WeatherInfo(W_DBHelper db_helper, List<W_DbInfoClass> db_list) {
		//tv_Temper : 기온	//tv_weaState : 일기	//tv_humid : 습도	//iv_Weather : 날씨아이콘
		//DB에서 위치에 맞는 정보를 가져와서 set해줌
		
		tv_Temper.setText(db_list.get(0).getTemper());
		tv_weaState.setText(db_list.get(0).getWeather());
		tv_humid.setText("Humidity: " + db_list.get(0).getHumidity() + "%");

		if (DbInfoClassList.get(0).getWeather().equals("1")	|| DbInfoClassList.get(0).getWeather().equals("맑음")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
			iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("흐림")	|| DbInfoClassList.get(0).getWeather().equals("구름 많음")
				|| DbInfoClassList.get(0).getWeather().equals("구름 조금")	|| DbInfoClassList.get(0).getWeather().equals("비 끝남")
				|| DbInfoClassList.get(0).getWeather().equals("뇌전 끝")	|| DbInfoClassList.get(0).getWeather().equals("소나기 끝")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("뇌우")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.thunderstorm);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("박무")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.mist);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("뇌우,비/눈")	|| DbInfoClassList.get(0).getWeather().equals("눈")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.snow);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("약한소나기")	|| DbInfoClassList.get(0).getWeather().equals("약한비계속")
				|| DbInfoClassList.get(0).getWeather().equals("약한비단속")	|| DbInfoClassList.get(0).getWeather().equals("뇌우끝,비")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.rain);	iv_Weather.setImageDrawable(img);
		}
	}
}

class ReceiverThread extends Thread {
	Context mContext;
	ReceiverThread(Context context) {mContext = context;}

	public void run() {
		Looper.prepare();
		//날씨와 위치정보를 service를 이용하여 구함
		mContext.startService(new Intent(mContext, GeoService.class));
		mContext.startService(new Intent(mContext, W_Service.class));
		Looper.loop();
	};
}
