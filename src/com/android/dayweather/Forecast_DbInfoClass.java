package com.android.dayweather;

public class Forecast_DbInfoClass {
	private int id;
	private String wea;		//일기
	private String tem;		//온도
	private String rain;	//강수확률
	private String tim;		//시간
	private String hum;		//습도
	
	public Forecast_DbInfoClass() {	}

	/* id 사용할경우 */	public Forecast_DbInfoClass(int id, String wea,String tem,String rain,String tim,String hum){
		this.id = id;	this.wea=wea;	this.tem=tem;	this.rain=rain;	this.tim=tim;	this.hum=hum;
	}
	/* id x */	public Forecast_DbInfoClass(String wea,String tem,String rain,String tim,String hum){
		this.wea=wea;	this.tem=tem;	this.rain=rain;	this.tim=tim;	this.hum=hum;
	}
	
	public int getId()		{return id;}		public void setId(int id)		{this.id=id;}
	public String getWea()	{return wea;}		public void setWea(String wea)	{this.wea=wea;}
	public String getTem()	{return tem;}		public void setTem(String tem)	{this.tem=tem;}
	public String getRain()	{return rain;}		public void setRain(String rain){this.rain=rain;}
	public String getTim()	{return tim;}		public void setTim(String tim)	{this.tim=tim;}
	public String getHum()	{return hum;}		public void setHum(String hum)	{this.hum=hum;}
}