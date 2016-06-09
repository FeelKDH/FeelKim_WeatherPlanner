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

		////�����޴� ����� TextView, ImageView ����/////////////////////////
		iv_Weather = (ImageView) findViewById(R.id.iv_weather);
		tv_Temper = (TextView) findViewById(R.id.tv_temper);
		tv_weaState = (TextView) findViewById(R.id.tv_weastate);
		tv_humid = (TextView) findViewById(R.id.tv_humid);
		////��ġ,����,�ð������� DB���� �� ����//////////////////////////////////
		W_Helper = new W_DBHelper(getApplicationContext());
		DbInfoClassList = W_Helper.getAllDbInfoClasss();
		G_Helper = new Geo_DBHelper(getApplicationContext());
		G_DbInfoClassList = G_Helper.getAllDbInfoClasss();
		temp = new Forecast_DBHelper(getApplicationContext());
		tempt = temp.getAllDbInfoClasss();

		////�����޴� �ϴ��� �ð��� �޴��� TextView,ImageView ���� /////////////////////
		////t1,2,3,4 : �ð�	//i1,2,3,4 : ����������	//te1,2,3,4 : ���	//s1,2,3,4 : ����
		if (temp.getDbInfoClasssCount() != 0) {
			// time
			TextView t1 = (TextView) findViewById(R.id.weather_txt1);		TextView t2 = (TextView) findViewById(R.id.weather_txt3);
			TextView t3 = (TextView) findViewById(R.id.weather_txt5);		TextView t4 = (TextView) findViewById(R.id.weather_txt7);
			t1.setText(tempt.get(0).getTim());		t2.setText(tempt.get(1).getTim());
			t3.setText(tempt.get(2).getTim());		t4.setText(tempt.get(3).getTim());

			ImageView i1 = (ImageView) findViewById(R.id.weather_iv1);		ImageView i2 = (ImageView) findViewById(R.id.weather_iv2);
			ImageView i3 = (ImageView) findViewById(R.id.weather_iv3);		ImageView i4 = (ImageView) findViewById(R.id.weather_iv4);
			if (tempt.get(0).getWea().equals("����"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i1.setImageDrawable(img);
			}
			else if (tempt.get(0).getWea().equals("���� ����") || tempt.get(0).getWea().equals("���� ����")|| tempt.get(0).getWea().equals("�帲"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i1.setImageDrawable(img);
			}
			
			if (tempt.get(1).getWea().equals("����"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i2.setImageDrawable(img);
			}
			else if (tempt.get(1).getWea().equals("���� ����") || tempt.get(0).getWea().equals("���� ����")|| tempt.get(0).getWea().equals("�帲"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i2.setImageDrawable(img);
			}
			
			if (tempt.get(2).getWea().equals("����"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i3.setImageDrawable(img);
			}
			else if (tempt.get(2).getWea().equals("���� ����") || tempt.get(0).getWea().equals("���� ����")|| tempt.get(0).getWea().equals("�帲"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i3.setImageDrawable(img);
			}
			
			if (tempt.get(3).getWea().equals("����"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
				i4.setImageDrawable(img);
			}
			else if (tempt.get(3).getWea().equals("���� ����") || tempt.get(0).getWea().equals("���� ����")|| tempt.get(0).getWea().equals("�帲"))
			{
				BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);
				i4.setImageDrawable(img);
			}
			TextView te1 = (TextView) findViewById(R.id.weather_txt2);	TextView te2 = (TextView) findViewById(R.id.weather_txt4);
			TextView te3 = (TextView) findViewById(R.id.weather_txt6);	TextView te4 = (TextView) findViewById(R.id.weather_txt8);
			te1.setText(tempt.get(0).getTem()+"��");		te2.setText(tempt.get(1).getTem()+"��");
			te3.setText(tempt.get(2).getTem()+"��");		te4.setText(tempt.get(3).getTem()+"��");

			TextView s1 = (TextView) findViewById(R.id.s1);		TextView s2 = (TextView) findViewById(R.id.s2);
			TextView s3 = (TextView) findViewById(R.id.s3);		TextView s4 = (TextView) findViewById(R.id.s4);
			s1.setText(tempt.get(0).getHum()+"%");		s2.setText(tempt.get(1).getHum()+"%");
			s3.setText(tempt.get(2).getHum()+"%");		s4.setText(tempt.get(3).getHum()+"%");
		}

		//Thread�� �̿��Ͽ� weather,geo service ����
		mThread = new ReceiverThread(getApplicationContext());
		mThread.setDaemon(true);
		mThread.start();

		W_Helper = new W_DBHelper(getApplicationContext());
		DbInfoClassList = W_Helper.getAllDbInfoClasss();

		//�����޴� ����� textView,ImageView�� ������ �ֱ����� �Լ�ȣ��
		if (DbInfoClassList.size() != 0)	{modify_WeatherInfo(W_Helper, DbInfoClassList);}
	}

	@Override
	protected void onResume() {		// TODO Auto-generated method stub
		super.onResume();
	}

	void modify_WeatherInfo(W_DBHelper db_helper, List<W_DbInfoClass> db_list) {
		//tv_Temper : ���	//tv_weaState : �ϱ�	//tv_humid : ����	//iv_Weather : ����������
		//DB���� ��ġ�� �´� ������ �����ͼ� set����
		
		tv_Temper.setText(db_list.get(0).getTemper());
		tv_weaState.setText(db_list.get(0).getWeather());
		tv_humid.setText("Humidity: " + db_list.get(0).getHumidity() + "%");

		if (DbInfoClassList.get(0).getWeather().equals("1")	|| DbInfoClassList.get(0).getWeather().equals("����")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.sunny);
			iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("�帲")	|| DbInfoClassList.get(0).getWeather().equals("���� ����")
				|| DbInfoClassList.get(0).getWeather().equals("���� ����")	|| DbInfoClassList.get(0).getWeather().equals("�� ����")
				|| DbInfoClassList.get(0).getWeather().equals("���� ��")	|| DbInfoClassList.get(0).getWeather().equals("�ҳ��� ��")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.cloud);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("����")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.thunderstorm);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("�ڹ�")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.mist);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("����,��/��")	|| DbInfoClassList.get(0).getWeather().equals("��")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.snow);	iv_Weather.setImageDrawable(img);
		}
		else if (DbInfoClassList.get(0).getWeather().equals("���Ѽҳ���")	|| DbInfoClassList.get(0).getWeather().equals("���Ѻ���")
				|| DbInfoClassList.get(0).getWeather().equals("���Ѻ�ܼ�")	|| DbInfoClassList.get(0).getWeather().equals("���쳡,��")) {
			BitmapDrawable img = (BitmapDrawable) getResources().getDrawable(R.drawable.rain);	iv_Weather.setImageDrawable(img);
		}
	}
}

class ReceiverThread extends Thread {
	Context mContext;
	ReceiverThread(Context context) {mContext = context;}

	public void run() {
		Looper.prepare();
		//������ ��ġ������ service�� �̿��Ͽ� ����
		mContext.startService(new Intent(mContext, GeoService.class));
		mContext.startService(new Intent(mContext, W_Service.class));
		Looper.loop();
	};
}
