package com.android.dayweather;

public class Geo_DbInfoClass {

	private int id;
	private String _do; // 도
	private String city;// 도시
	private String dong;// 동

	public Geo_DbInfoClass() {}

	/* id 사용할경우 */
	public Geo_DbInfoClass(int id, String _do, String city, String dong) {
		this.id = id;	this._do = _do;	this.city = city;	this.dong = dong;
	}

	/* id x */
	public Geo_DbInfoClass(String _do, String city, String dong) {
		this._do = _do;	this.city = city;	this.dong = dong;
	}

	public int getId() 		{return id;}		public void setId(int id) 		{this.id = id;}
	public String getDo() 	{return _do;}		public void setDo(String _do) 	{this._do = _do;}
	public String getCity() {return city;}		public void setCity(String city){this.city = city;}
	public String getDong() {return dong;}		public void setDong(String dong){this.dong = dong;}
}