package com.android.dayweather;

public class Lo_DbInfoClass {
	private int id;
	private String location;	//지역
	private String weather; // 현재일기
	private String temper; // 현재기온
	private String wind;// 풍속

	public Lo_DbInfoClass() {}

	/* id 사용할경우 */	public Lo_DbInfoClass(int id, String location, String weather,String temper, String wind) {
		this.id = id;	this.location = location;	this.weather = weather;	this.temper = temper;	this.wind = wind;
	}
	/* id x */	public Lo_DbInfoClass(String location, String weather, String temper,String wind) {
		this.location = location;	this.weather = weather;	this.temper = temper;	this.wind = wind;
	}

	public int getId()			{return id;}		public void setId(int id)				{this.id = id;}
	public String getLocation()	{return location;}	public void setLocation(String location){this.location = location;}
	public String getWeather()	{return weather;}	public void setWeather(String weather)	{this.weather = weather;}
	public String getTemper()	{return temper;}	public void setTemper(String temper)	{this.temper = temper;}
	public String getWind()		{return wind;}		public void setWind(String wind)		{this.wind = wind;}
}