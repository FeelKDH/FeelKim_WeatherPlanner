package com.android.alarm;

public class A_DbInfoClass {
	private int id;
	private String hour;
	private String min;
	private String check;
	private String time;

	public A_DbInfoClass() {
	}

	public A_DbInfoClass(int id, String hour, String min, String check) {
		this.id = id;
		this.hour = hour;
		this.min = min;
		this.check = check;
		this.time = "" + (Integer.parseInt(hour) * 60 + Integer.parseInt(min));
	}

	public A_DbInfoClass(String hour, String min, String check) {
		this.hour = hour;
		this.min = min;
		this.check = check;
		this.time = "" + (Integer.parseInt(hour) * 60 + Integer.parseInt(min));
	}

	public int getID() {
		return this.id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getHour() {
		return this.hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMin() {
		return this.min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getCheck() {
		return this.check;
	}

	public void setCheck(String chk) {
		this.check = chk;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String toString() {
		return hour + ":" + min;

	}
}