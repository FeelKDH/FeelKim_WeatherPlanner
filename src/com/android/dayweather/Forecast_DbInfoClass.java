package com.android.dayweather;

public class Forecast_DbInfoClass {
	private int id;
	private String wea;		//�ϱ�
	private String tem;		//�µ�
	private String rain;	//����Ȯ��
	private String tim;		//�ð�
	private String hum;		//����
	
	public Forecast_DbInfoClass() {	}

	/* id ����Ұ�� */	public Forecast_DbInfoClass(int id, String wea,String tem,String rain,String tim,String hum){
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