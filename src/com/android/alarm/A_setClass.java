package com.android.alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

@SuppressLint("SimpleDateFormat")
public class A_setClass {
	public A_setClass() {
	}

	public void AlarmSet(Context context, A_DbInfoClass alarmInfo,
			DBHelper Helper) {
		int reqcode = alarmInfo.getID();
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, A_Receive.class);
		PendingIntent pender = PendingIntent.getBroadcast(context, reqcode,
				intent, PendingIntent.FLAG_ONE_SHOT);

		Calendar cal = Calendar.getInstance();
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat curDayFormat = new SimpleDateFormat("dd");

		int curYear = Integer.parseInt(curYearFormat.format(date));
		int curMonth = Integer.parseInt(curMonthFormat.format(date));
		int curDay = Integer.parseInt(curDayFormat.format(date));
		int timeHOUR = Integer.parseInt(alarmInfo.getHour());
		int timeMINUTE = Integer.parseInt(alarmInfo.getMin());

		cal.set(curYear, curMonth - 1, curDay, timeHOUR, timeMINUTE, 0);

		if (System.currentTimeMillis() >= cal.getTimeInMillis()) {
			alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()
					+ (24 * 60 * 60 * 1000), pender);
		} else {
			alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pender);
		}
	}

}
