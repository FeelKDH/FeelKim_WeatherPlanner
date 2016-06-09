package com.android.dayweather;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import com.android.weatherplanner.R;

@SuppressLint("NewApi")
public class W_Service extends Service {
	static String html = null;

	static String[] wide_name;	static String[] wide_code;
	static String[] city_name;	static String[] city_code;
	static String[] dong_name;	static String[] dong_code;
	static String url;
	static String[] c_name;	static String[] c_num;
	String temp, re;
	SQLiteDatabase db;
	Geo_DBHelper G_Helper;
	Cursor cursor;

	/**/

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		try {
			int j = 0;
			String url = null, url1=null;
			Log.e("test", "start Weather");
			
			G_Helper = new Geo_DBHelper(this);
			List<Geo_DbInfoClass> G_DbInfo = G_Helper.getAllDbInfoClasss();

			GregorianCalendar today = new GregorianCalendar();
			int year = today.get(Calendar.YEAR);
			int month = today.get(Calendar.MONTH) + 1;
			int day = today.get(Calendar.DAY_OF_MONTH);
			int hour = today.get(Calendar.HOUR_OF_DAY);
			
			 if (hour > 9) { re = day-1 + "." + hour + "H";}
			 else { re = day-1 + ".0" + hour + "H";}
			
			 c_name = getResources().getStringArray(R.array.c_Name);	c_num = getResources().getStringArray(R.array.c_Num);
				
			 if (G_Helper.getDbInfoClasssCount() != 0) {

					String city_n = G_DbInfo.get(1).getCity(); String do_n = G_DbInfo.get(1).getDo();
					for (j = 0; j < c_name.length; j++) {
						if (city_n.contains(c_name[j])) {
							url1 = "http://www.kma.go.kr/weather/observation/currentweather.jsp?type=t99&mode=0&stn="+ c_num[j];
							break;
						}
						if (do_n.contains(c_name[j])) {
							url1 = "http://www.kma.go.kr/weather/observation/currentweather.jsp?type=t99&mode=0&stn="+ c_num[j];
							break;
						}
						else{url1 = "http://www.kma.go.kr/weather/observation/currentweather.jsp?type=t99&mode=0&stn=159";}
					}
			}
			Source Ysource = new Source(new URL(url1));
			Ysource.fullSequentialParse(); // 시작부터 끝까지 태그들만 parse

			Element Ytable = Ysource.getAllElements(HTMLElementName.TABLE).get(0);
			int Ytr_count = Ytable.getAllElements(HTMLElementName.TR).size();
			for (int i = 2; i < Ytr_count; i++) 
			{
				Element Ytr = (Element) Ytable.getAllElements(HTMLElementName.TR).get(i);
				String s2 = ((Element) Ytr.getAllElements(HTMLElementName.TD).get(0)).getContent().toString();
				String s3 = s2.replaceAll("<a[^>]+>", "");
				s3 = s3.replaceAll("</a>", "");
				if (s3.contains(re)) {
				//Log.e("Test if 1", Helper.getDbInfoClasssCount() + "");
				////////온도
					if (!((Element) Ytr.getAllElements(HTMLElementName.TD).get(5)).getContent().toString().equals("&nbsp;")) {
						temp=((Element) Ytr.getAllElements(HTMLElementName.TD).get(5)).getContent().toString();
					}
				}
			}
			wide_name = getResources().getStringArray(com.android.weatherplanner.R.array.wide_name);
			wide_code = getResources().getStringArray(com.android.weatherplanner.R.array.wide_code);

			if (G_DbInfo.size() > 0) {
				for (int i = 0; i < wide_name.length; i++) {
					if (G_DbInfo.get(1).getDo().equals(wide_name[i])) {
						if (wide_name[i].equals("부산광역시")){}
						else if (wide_name[i].equals("경상남도")) {
							url = "http://www.kma.go.kr/weather/forecast/timeseries.jsp?searchType=INTEREST&wideCode="+ wide_code[i];
							city_name = getResources().getStringArray(com.android.weatherplanner.R.array.gyeongnam_cityname);
							city_code = getResources().getStringArray(com.android.weatherplanner.R.array.gyeongnam_citycode);
						}
					}
				}
				for (int i = 0; i < city_name.length; i++) {
					if (G_DbInfo.get(1).getCity().equals(city_name[i])) {
						if (city_name[i].equals("김해시")) {
							url += "&cityCode=" + city_code[i];
							dong_name = getResources().getStringArray(com.android.weatherplanner.R.array.kimhea_dongname);
							dong_code = getResources().getStringArray(com.android.weatherplanner.R.array.kimhea_dongcode);
						} 
						else if (city_name[i].equals("거제시")) {}
					}
				}

				W_DBHelper Helper = new W_DBHelper(this);
				Helper.deleteAllDbInfoClass();

				W_DbInfoClass WeatherDbInfoClass = new W_DbInfoClass("0", "0","0", "0", "0", "0", "0", "0","0");

				for (int i = 0; i < dong_name.length; i++) {
					if (G_DbInfo.get(1).getDong().equals(dong_name[i])) {
						url += "&dongCode=" + dong_code[i];
						
						try {
							Source source = new Source(new URL(url));
							source.fullSequentialParse();
							Element table = source.getAllElements(HTMLElementName.TABLE).get(1);
							int tr_count = table.getAllElements(HTMLElementName.TR).size();
							
							for (int k = 0; k < tr_count; k++) {
								Element th = (Element) table.getAllElements(HTMLElementName.TH).get(0);
								Element tr = (Element) table.getAllElements(HTMLElementName.TR).get(1);
								String d1 = ((Element) tr.getAllElements(HTMLElementName.DD).get(0)).getContent().toString();

								Element DD3 = (Element) tr.getAllElements(HTMLElementName.DD).get(2);
								Element DD4 = (Element) tr.getAllElements(HTMLElementName.DD).get(3);

								String d3 = ((Element) DD3.getAllElements(HTMLElementName.STRONG).get(0)).getContent().toString();
								String d4 = ((Element) DD4.getAllElements(HTMLElementName.STRONG).get(0)).getContent().toString();

								String weather = ((Element) tr.getAllElements(HTMLElementName.DT).get(0)).getContent().toString();

								Pattern p = Pattern.compile("<(?:.|\\s)*?>");
								Matcher m = p.matcher(weather);
								weather = m.replaceAll("").trim();

								Element span1 = (Element) th.getAllElements(HTMLElementName.SPAN).get(0);

								String time = span1.getContent().toString();
								
								WeatherDbInfoClass.setYear(Integer.toString(year));
								WeatherDbInfoClass.setMonth(Integer.toString(month));
								WeatherDbInfoClass.setDay(Integer.toString(day));
								WeatherDbInfoClass.setHour(time);
								WeatherDbInfoClass.setWeather(weather);
								WeatherDbInfoClass.setTemper(d1);
								WeatherDbInfoClass.setHumidity(d3);
								WeatherDbInfoClass.setWind(d4);
								WeatherDbInfoClass.setYtemp(temp);
							}
							Helper.addDbInfoClass(WeatherDbInfoClass);
							
							Forecast_DBHelper F_Helper = new Forecast_DBHelper(	this);
							if (F_Helper.getDbInfoClasssCount() < 10) {F_Helper.deleteAllDbInfoClass();}
							List<Forecast_DbInfoClass> F_DbInfo = new ArrayList<Forecast_DbInfoClass>();
							for (int w = 0; w < 4; w++) {F_DbInfo.add(new Forecast_DbInfoClass("0", "0","0", "0", "0"));}

							Element table2 = source.getAllElements(HTMLElementName.TABLE).get(2);
							Element tr1 = table2.getAllElements(HTMLElementName.TR).get(1);
							Element tr2 = table2.getAllElements(HTMLElementName.TR).get(2);
							Element tr3 = table2.getAllElements(HTMLElementName.TR).get(3);
							Element tr6 = table2.getAllElements(HTMLElementName.TR).get(6);
							Element tr8 = table2.getAllElements(HTMLElementName.TR).get(8);

							String[] wea = new String[20];
							String[] tem = new String[20];
							String[] rain = new String[20];
							String[] tim = new String[20];
							String[] hum = new String[20];
							
							for (int w = 0; w < 4; w++) {
								Element e = tr2.getAllElements(HTMLElementName.TD).get(w);
								wea[w] = e.getAttributeValue("title");
								F_DbInfo.get(w).setWea(wea[w]);
							}
							for (int w = 0; w < 4; w++) {
								Element e = tr6.getAllElements(HTMLElementName.TD).get(w + 1).getAllElements(HTMLElementName.P).get(0);
								tem[w] = e.getContent().toString();
								F_DbInfo.get(w).setTem(tem[w]);
							}
							for (int w = 0; w < 4; w++) {
								Element e = tr1.getAllElements(HTMLElementName.TD).get(w).getAllElements(HTMLElementName.IMG).get(0);
								tim[w] = e.getAttributeValue("title");
								F_DbInfo.get(w).setTim(tim[w]);
							}
							for (int w = 0; w < 4; w++) {
								Element e = tr3.getAllElements(HTMLElementName.TD).get(w);
								rain[w] = e.getContent().toString();
								F_DbInfo.get(w).setRain(rain[w]);
							}
							for (int w = 0; w < 4; w++) {
								Element e = tr8.getAllElements(HTMLElementName.TD).get(w + 1).getAllElements(HTMLElementName.P).get(0);
								hum[w] = e.getContent().toString();
								F_DbInfo.get(w).setHum(hum[w]);
							}
							for (int w = 0; w < 4; w++) {
								F_Helper.addDbInfoClass(F_DbInfo.get(w));
							}
						}
						catch (Exception e) {e.printStackTrace();}
					}
				}
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}

	@Override
	public void onDestroy() {}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {return super.onStartCommand(intent, flags, startId);}
}