package com.android.alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dayweather.W_DBHelper;
import com.android.dayweather.W_DbInfoClass;
import com.android.weatherplanner.R;

@SuppressLint("SimpleDateFormat")
public class A_ShowActivity extends Activity implements OnInitListener {
	TextView AlarmedTime;
	Button btnStop;
	TextView TitleText;
	TextView WeatherText;
	MediaPlayer mPlayer = null;
	AudioManager am = null;

	boolean flag = true;

	int curYear;
	int curMonth;
	int curDay;
	int currVol;
	int pos;
	
	//
	TextToSpeech TTS;
	String Intent_string = "";
	//
	
	List<A_DbInfoClass> A_DbInfoClassList = new ArrayList<A_DbInfoClass>();
	DBHelper Helper = new DBHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_a__show);
		
		//
		TTS=new TextToSpeech(this, this);
		// 초기화 설정
		TTS.setLanguage(Locale.KOREA);
		TTS.setPitch(1.0f); // 음의 높낮이 normal(1.0f)
		TTS.setSpeechRate(0.9f); // 음의 속도 normal(1.0f)	
		//
		
		AlarmedTime = (TextView) findViewById(R.id.showtext);
		btnStop = (Button) findViewById(R.id.stopbtn);
		TitleText = (TextView) findViewById(R.id.showtitletext);
		WeatherText = (TextView) findViewById(R.id.showweathertext);

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		currVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol,
				AudioManager.FLAG_PLAY_SOUND);
		mPlayer = MediaPlayer.create(this, R.raw.alarm1);
		mPlayer.start();

		A_DbInfoClassList = Helper.getAllDbInfoClasss();
		long now = System.currentTimeMillis();
		Date date = new Date(now);

		SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy");
		curYear = Integer.parseInt(curYearFormat.format(date));
		SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM");
		curMonth = Integer.parseInt(curMonthFormat.format(date));
		SimpleDateFormat curDayFormat = new SimpleDateFormat("dd");
		curDay = Integer.parseInt(curDayFormat.format(date));
		SimpleDateFormat curHourFormat = new SimpleDateFormat("HH");
		int curHour = Integer.parseInt(curHourFormat.format(date));
		SimpleDateFormat curMinuteFormat = new SimpleDateFormat("mm");
		int curMinute = Integer.parseInt(curMinuteFormat.format(date));
		String time = String.valueOf(((curHour * 60) + curMinute));

		for (int i = 0; i < A_DbInfoClassList.size(); i++) {
			if (A_DbInfoClassList.get(i).getTime() == time)
				pos = i - 1;
		}
		AlarmedTime.setText(curHour + "시 " + curMinute + "분");

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	public void mOnClick(View v) {
		if (v.getId() == R.id.stopbtn) {
			if (flag) {
				flag = false;
				am.setStreamVolume(AudioManager.STREAM_SYSTEM, currVol,
						AudioManager.FLAG_PLAY_SOUND);
				mPlayer.stop();
				
				
				

				W_DBHelper temp=new W_DBHelper(this);
				List<W_DbInfoClass> tempt=new ArrayList<W_DbInfoClass>();
				tempt=temp.getAllDbInfoClasss();
				String math=tempt.get(0).getTemper();
				int kkk=math.indexOf("℃");
				math = math.substring(0, kkk);
				Log.i("test",""+math);
				double result;				
				ImageView IV=(ImageView)findViewById(R.id.imageView1);
				if (tempt.get(0).getWeather().equals("구름 많음")
						|| tempt.get(0).getWeather().equals("구름 조금")) {
					BitmapDrawable img = (BitmapDrawable) getResources()
							.getDrawable(R.drawable.cloud);
					IV.setImageDrawable(img);
				} else {
					BitmapDrawable img = (BitmapDrawable) getResources()
							.getDrawable(R.drawable.sunny);
					IV.setImageDrawable(img);
				}
				
				Intent_string += "\n온도는 " + tempt.get(0).getTemper() + "  로   ";
				if (Float.parseFloat(math) > Float.parseFloat(tempt.get(0).getYtemp()))
				{
					result = Float.parseFloat(math)- Float.parseFloat(tempt.get(0).getYtemp());
					result=(float) Double.parseDouble( String.format( "%.1f" , result ) );
					Intent_string += "어제보다  " + result + " 도 높습니다.";
				}
				else if (Float.parseFloat(math) < Float.parseFloat(tempt.get(0).getYtemp()))
				{
					result = Float.parseFloat(math)- Float.parseFloat(tempt.get(0).getTemper());
					result=(float) Double.parseDouble( String.format( "%.1f" , result ) );
					Intent_string += "어제보다  " + result + " 도 낮습니다.";				
				}
				else
				{
					Intent_string += " 어제와   온도차는   없습니다.";
				}
				//Intent_string="씨발놈아";
				TTS.speak(Intent_string, TextToSpeech.QUEUE_FLUSH, null);
				// C_DBHelper calendar = new
				// C_DBHelper(getApplicationContext());
				// List<C_DbInfoClass> c_temp = new ArrayList<C_DbInfoClass>();
				// c_temp = calendar.getAllDbInfoClasss();
				// // String iljung = "";
				// int count = 0;
				// for (int i = 0; i < c_temp.size(); i++) {
				// if (c_temp.get(i).getYear()
				// .equals(Integer.toString(curYear))
				// && c_temp.get(i).getMonth()
				// .equals(Integer.toString(curMonth))
				// && c_temp.get(i).getDay()
				// .equals(" " + Integer.toString(curDay))) {
				// count++;
				// // iljung = temp.get(i).getTitle() + "\n" + iljung +
				// // "\n";
				// }
				// }
				// TitleText.setText("오늘의 일정 " + count + "개");
				//
				// W_DBHelper weather = new W_DBHelper(getApplicationContext());
				// List<W_DbInfoClass> w_temp = new ArrayList<W_DbInfoClass>();
				// w_temp = weather.getAllDbInfoClasss();
				//
				// // for(int i=0; i<w_temp.size(); i++){
				// // Log.i("test", "temper" + i + " : " +
				// // w_temp.get(i).getTemper());
				// // }
				//
				// // Log.i("test", "1Year :" + w_temp.get(0).getYear());
				// // Log.i("test", "1Month :" + w_temp.get(0).getMonth());
				// // Log.i("test", "1Day :" + w_temp.get(0).getDay());
				// // Log.i("test", "2Year :" + w_temp.get(1).getYear());
				// // Log.i("test", "2Month :" + w_temp.get(1).getMonth());
				// // Log.i("test", "2Day :" + w_temp.get(1).getDay());
				//
				// String gion = "";
				// for (int i = 0; i < w_temp.size(); i++) {
				// if (w_temp.get(i).getYear()
				// .equals(Integer.toString(curYear))
				// && w_temp.get(i).getMonth()
				// .equals(Integer.toString(curMonth))
				// && w_temp.get(i).getDay()
				// .equals(Integer.toString(curDay))) {
				// Log.i("test", "w_if in");
				// gion = w_temp.get(i).getTemper();
				// }
				// WeatherText.setText("현재기온 " + gion);
				// }

			} else {
				flag = true;
				
				//Intent_string=;
				A_DbInfoClassList.get(pos).setCheck("false");
				Helper.updateDbInfoClass(A_DbInfoClassList.get(pos));
				finish();
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			return false;
		if (keyCode == KeyEvent.KEYCODE_HOME)
			return false;
		return false;
	}

	@Override
	public void onInit(int status) {
//		// TODO Auto-generated method stub
//
//		if (status == TextToSpeech.SUCCESS) {
//			Log.i("TTS_SUCCESS", "초기화 성공");
//			// button이나 listener로 특정 시점에 TTS 출력 가능
//			//TTS.speak("안녕하세요. TTS를 초기화시 출력합니다.", TextToSpeech.QUEUE_FLUSH, null);
//		} else {
//			Log.i("TTS_FAILED", "초기화 실패");
//		}
	}
}
