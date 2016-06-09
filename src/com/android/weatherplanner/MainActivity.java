package com.android.weatherplanner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.calendar.CalendarActivity;
import com.android.calendar.CalendarInfoClass;
import com.android.dayweather.W_DBHelper;
import com.android.dayweather.W_DbInfoClass;
import com.android.slideout.SlideoutActivity;

public class MainActivity extends Activity {
	private GestureDetector mDetector;

	ListView listview;
	CalendarActivity.CalendarListAdapter adapter;

	String acctName = "";
	String acctType = "";
	int calId;

	ImageView weather;
	TextView month, day, temper, hum, wind, yo;

	List<CalendarInfoClass> InfoClassList = new ArrayList<CalendarInfoClass>();
	List<W_DbInfoClass> weatherclass = new ArrayList<W_DbInfoClass>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//메인에 사용되는 TextView, ImageView 연결///////////////////////
		weather = (ImageView) findViewById(R.id.main_weather);
		month = (TextView) findViewById(R.id.main_txt1);
		day = (TextView) findViewById(R.id.main_txt3);
		temper = (TextView) findViewById(R.id.main_temper);
		hum = (TextView) findViewById(R.id.main_hum);
		wind = (TextView) findViewById(R.id.main_wind);
		yo = (TextView) findViewById(R.id.main_txt4);
		
		////앱 메인화면에 요일 표시////////////////////////////////////////
		Calendar cal = Calendar.getInstance();	GregorianCalendar today = new GregorianCalendar();
		
		int tyear = today.get(Calendar.YEAR);	int tmonth = today.get(Calendar.MONTH);	int tday = today.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.YEAR, tyear);	cal.set(Calendar.MONTH, tmonth);	cal.set(Calendar.DATE, tday);
		
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 1:	yo.setText("일");	break;		case 2:	yo.setText("월");	break;	case 3:	yo.setText("화");	break;
		case 4:	yo.setText("수");	break;		case 5:	yo.setText("목");	break;	case 6:	yo.setText("금");	break;
		case 7:	yo.setText("토");	break;
		}
		
		////앱 메인에 날씨 아이콘 및 온도 습도 기온 표시////////////////////////////
		W_DBHelper temp = new W_DBHelper(this);
		if (temp.getDbInfoClasssCount() != 0) {
			
			weatherclass = temp.getAllDbInfoClasss();
			month.setText(weatherclass.get(0).getMonth());
			day.setText(weatherclass.get(0).getDay());

			if (weatherclass.get(0).getWeather().equals("구름 많음")	|| weatherclass.get(0).getWeather().equals("구름 조금")
					|| weatherclass.get(0).getWeather().equals("흐림")) {
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				weather.setImageDrawable(img);
			}
			else {
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				weather.setImageDrawable(img);
			}
			temper.setText(weatherclass.get(0).getTemper());
			hum.setText(weatherclass.get(0).getHumidity() + "%");
			wind.setText(weatherclass.get(0).getWind() + "m/s");
		}
		mDetector = new GestureDetector(this, mGestureListener);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {	// TODO Auto-generated method stub
		return mDetector.onTouchEvent(event);
	}

	OnGestureListener mGestureListener = new OnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {		// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			if ((int) velocityX >= 0) {
				int width = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
								.getDisplayMetrics());
				SlideoutActivity.prepare(MainActivity.this, R.id.inner_content,
						width);
				startActivity(new Intent(MainActivity.this, MenuActivity.class));
				overridePendingTransition(0, 0);

			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

	};
}
