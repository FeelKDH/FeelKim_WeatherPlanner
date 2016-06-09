package com.android.calendar;

public class CalendarInfoClass {
	private String Name;
	private String Location;
	private String STime;
	private String ETime;
	private String Description;
	private String Timezone;
	
	public CalendarInfoClass(){
	}
	
	public CalendarInfoClass(String Name, String Location, String STime, String ETime, String Description, String Timezone){
		this.Name = Name;
		this.Location = Location;
		this.STime = STime;
		this.ETime = ETime;
		this.Description = Description;
		this.Timezone = Timezone;
	}
	
	public String getName(){
		return this.Name;
	}
	public void setName(String Name){
		this.Name = Name;
	}
	public String getLocation(){
		return this.Location;
	}
	public void setLoaction(String Loaction){
		this.Location =Loaction;
	}
	public String getSTime(){
		return this.STime;
	}
	public void setSTime(String STime){
		this.STime = STime;
	}
	public String getETime(){
		return this.ETime;
	}
	public void setETime(String ETime){
		this.ETime = ETime;
	}
	public String getDescription(){
		return this.Description;
	}
	public void setDescription(String Description){
		this.Description = Description;
	}
	public String getTimezone(){
		return this.Timezone;
	}
	public void setTimezone(String Timezone){
		this.Timezone = Timezone;
	}
}
