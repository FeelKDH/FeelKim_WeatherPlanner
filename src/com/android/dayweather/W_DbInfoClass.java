package com.android.dayweather;

public class W_DbInfoClass {

	private int id;
	private String year; // ��
	private String month; // ��
	private String day; // ��
	private String hour;
	private String weather; // �����ϱ�
	private String temper; // ������
	private String humidity; // ����
	private String wind;// ǳ��
	private String ytemp;

	public W_DbInfoClass() {
	}

	/* id ����Ұ�� */	public W_DbInfoClass(int id, String year, String month, String day,	String hour,
			String weather,String temper, String humidity,	String wind,String ytemp) {
		this.id = id;	this.year = year;	this.month = month;	this.day = day;	this.hour = hour;	this.weather = weather;
		this.temper = temper;	this.humidity = humidity;	this.wind = wind;	this.ytemp=ytemp;
	}

	/* id x */	public W_DbInfoClass(String year, String month, String day, String hour,
			String weather, String temper, String humidity, String wind,String ytemp) {
		this.year = year;	this.month = month;	this.day = day;	this.hour = hour;	this.weather = weather;
		this.temper = temper;	this.humidity = humidity;	this.wind = wind;	this.ytemp=ytemp;
	}

	public int getId()			{return id;}		public void setId(int id)				{this.id = id;}
	public String getYear()		{return year;}		public void setYear(String year)		{this.year = year;}
	public String getMonth()	{return month;}		public void setMonth(String month)		{this.month = month;}
	public String getDay()		{return day;}		public void setDay(String day)			{this.day = day;}
	public String getHour()		{return hour;}		public void setHour(String hour)		{this.hour = hour;}
	public String getWeather()	{return weather;}	public void setWeather(String weather)	{this.weather = weather;}
	public String getTemper()	{return temper;}	public void setTemper(String temper)	{this.temper = temper;}
	public String getHumidity()	{return humidity;}	public void setHumidity(String humidity){this.humidity = humidity;}
	public String getWind()		{return wind;}		public void setWind(String wind)		{this.wind = wind;}
	public String getYtemp()	{return ytemp;}		public void setYtemp(String ytemp)		{this.ytemp = ytemp;}
}