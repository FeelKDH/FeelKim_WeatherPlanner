package com.android.alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

@SuppressLint("SimpleDateFormat")
public class A_Receive extends BroadcastReceiver {

	@SuppressWarnings("static-access")
	@Override
	public void onReceive(Context context, Intent intent) {

		long now = System.currentTimeMillis();
		Date date = new Date(now);

		SimpleDateFormat curHourFormat = new SimpleDateFormat("HH");
		String curHour = curHourFormat.format(date);
		if (curHour.substring(0, 1).equals("0"))
			curHour = curHourFormat.format(date).substring(1, 2);

		SimpleDateFormat curMinuteFormat = new SimpleDateFormat("mm");
		String curMinute = curMinuteFormat.format(date);
		if (curMinute.substring(0, 1).equals("0")) {
			curMinute = curMinuteFormat.format(date).substring(1, 2);
		}

		DBHelper Helper = new DBHelper(context);
		List<A_DbInfoClass> DbInfoClassList = new ArrayList<A_DbInfoClass>();
		DbInfoClassList = Helper.getDbInfoClass(curHour);

		for (int i = 0; i < DbInfoClassList.size(); i++) {
			if ((DbInfoClassList.get(i).getMin()).equals(curMinute)) {
				if ((DbInfoClassList.get(i).getCheck())
						.equalsIgnoreCase("true")) {
					Intent intent1 = new Intent(context, A_ShowActivity.class);
					intent1.setFlags(intent1.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent1);
				}
			}
		}
	}
}
