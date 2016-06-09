package com.android.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.weatherplanner.R;

public class AddDialog extends Dialog implements OnClickListener {
	private Context context;

	private EditText Name;
	private EditText Location;
	private Button sDate;
	private Button sTime;
	private Button eDate;
	private Button eTime;
	private EditText Description;
	private Button Cancel;
	private Button Save;

	String acctName = "";
	String acctType = "";

	private int calID;
	private int sYear, sMonth, sDay, sHour, sMinute;
	private int eYear, eMonth, eDay, eHour, eMinute;
	private boolean flag = true;

	public AddDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		getWindow().setAttributes(lpWindow);
		setContentView(R.layout.calendar_dialog);

		Name = (EditText) findViewById(R.id.edit_name);
		Location = (EditText) findViewById(R.id.edit_location);
		sDate = (Button) findViewById(R.id.button_sdate);
		sTime = (Button) findViewById(R.id.button_stime);
		eDate = (Button) findViewById(R.id.button_edate);
		eTime = (Button) findViewById(R.id.button_etime);
		Description = (EditText) findViewById(R.id.edit_description);
		Cancel = (Button) findViewById(R.id.button_cancel);
		Save = (Button) findViewById(R.id.button_save);

		sDate.setOnClickListener(this);
		sTime.setOnClickListener(this);
		eDate.setOnClickListener(this);
		eTime.setOnClickListener(this);
		Cancel.setOnClickListener(this);
		Save.setOnClickListener(this);

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat curDayFormat = new SimpleDateFormat("dd");
		SimpleDateFormat curHourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat curMinuteFormat = new SimpleDateFormat("mm");
		sYear = Integer.parseInt(curYearFormat.format(date));
		eYear = sYear;
		sMonth = Integer.parseInt(curMonthFormat.format(date));
		eMonth = sMonth;
		sDay = Integer.parseInt(curDayFormat.format(date));
		eDay = sDay;
		sHour = Integer.parseInt(curHourFormat.format(date));
		eHour = sHour;
		sMinute = Integer.parseInt(curMinuteFormat.format(date));
		eMinute = sMinute;
		sDate.setText(sYear + "년 " + sMonth + "월 " + sDay + "일");
		sTime.setText(sHour + "시 " + sMinute + "분");
		eDate.setText(eYear + "년 " + eMonth + "월 " + eDay + "일");
		eTime.setText(eHour + "시 " + eMinute + "분");

		AccountManager acctMgr = AccountManager.get(context); // 사용자계정 전부를 불러온다.
		Account[] accts = acctMgr.getAccounts(); // 사용자 계정들을 배열에 입력
		int acctCnt = accts.length;
		Account acct;
		int i = 0;
		while (i < acctCnt) {
			acct = accts[i];
			if (acct.type.equals("com.google")) {
				acctName = acct.name;
				acctType = acct.type;
				break;
			}
			i++;
		}
		final String[] EVENT_PROJECTION = new String[] { Calendars._ID,
				Calendars.ACCOUNT_NAME, Calendars.CALENDAR_DISPLAY_NAME,
				Calendars.OWNER_ACCOUNT };
		Cursor cur = null;
		ContentResolver cr = context.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;
		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
				+ Calendars.ACCOUNT_TYPE + " = ?) AND ("
				+ Calendars.OWNER_ACCOUNT + " = ?))";
		String[] selectionArgs = new String[] { acctName, acctType, acctName };
		cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
		while (!cur.isLast()) {
			cur.moveToNext();
			calID = cur.getInt(0);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button_sdate) {
			flag = true;
			DialogDatePicker(sDate);
		} else if (v.getId() == R.id.button_stime) {
			flag = true;
			DialogTimePicker(sTime);
		} else if (v.getId() == R.id.button_edate) {
			flag = false;
			DialogDatePicker(eDate);
		} else if (v.getId() == R.id.button_etime) {
			flag = false;
			DialogTimePicker(eTime);
		} else if (v.getId() == R.id.button_cancel) {
			dismiss();
		} else if (v.getId() == R.id.button_save) {
			// 추가 할 때 반드시 들어가야 할 항목 : CALENDAR_ID, DTSTART, DTEND,
			// EVENT_TIMEZONE
			Calendar s_cal = Calendar.getInstance();
			s_cal.set(sYear, sMonth - 1, sDay, sHour, sMinute, 0);
			Calendar e_cal = Calendar.getInstance();
			e_cal.set(eYear, eMonth - 1, eDay + 1, eHour, eMinute, 0);

			ContentResolver cr = context.getContentResolver();
			ContentValues value = new ContentValues();

			value.put(Events.CALENDAR_ID, calID);
			value.put(Events.TITLE, Name.getText().toString());
			value.put(Events.EVENT_LOCATION, Location.getText().toString());
			value.put(Events.DTSTART, s_cal.getTimeInMillis());
			value.put(Events.DTEND, e_cal.getTimeInMillis());
			value.put(Events.DESCRIPTION, Description.getText().toString());
			value.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

			Uri uri = cr.insert(Events.CONTENT_URI, value);
			if (!uri.equals(null))
				Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
			
			dismiss();
		}
	}

	private void DialogDatePicker(final Button Date) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				setDate(year, monthOfYear + 1, dayOfMonth, Date);
			}
		};
		DatePickerDialog alert = new DatePickerDialog(context,
				mDateSetListener, cyear, cmonth, cday);
		alert.show();
	}

	public void setDate(int year, int month, int day, Button Date) {
		if (flag) {
			sYear = year;
			sMonth = month;
			sDay = day;
		} else {
			eYear = year;
			eMonth = month;
			eDay = day;
		}
		Date.setText(year + "년 " + month + "월 " + day + "일");
	}

	private void DialogTimePicker(final Button Time) {
		TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				setTime(hourOfDay, minute, Time);
			}
		};
		TimePickerDialog alert = new TimePickerDialog(context,
				mTimeSetListener, 0, 0, false);
		alert.show();
	}

	public void setTime(int hour, int minute, Button Time) {
		if (flag) {
			sHour = hour;
			sMinute = minute;
		} else {
			eHour = hour;
			eMinute = minute;
		}
		Time.setText(hour + "시 " + minute + "분");
	}
}
