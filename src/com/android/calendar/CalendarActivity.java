package com.android.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.android.weatherplanner.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CalendarActivity extends Activity implements OnClickListener {
	Button showDialog_Button;
	ListView listview;
	CalendarListAdapter adapter;
	
	AddDialog adg;
	String acctName = "";
	String acctType = "";
	int calId;
	
	List<CalendarInfoClass> InfoClassList = new ArrayList<CalendarInfoClass>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_main);
		
		showDialog_Button = (Button) findViewById(R.id.button_dialog);
		listview = (ListView) findViewById(R.id.customlistview);
		
		showDialog_Button.setOnClickListener(this);
		
		AccountManager acctMgr = AccountManager.get(this);
		Account[] accts = acctMgr.getAccounts();
		int acctCnt = accts.length;
		Account acct;
		int i = 0;
		while (i < acctCnt) {
			acct = accts[i];
			if(acct.type.equals("com.google")){
				acctName = acct.name;
				acctType = acct.type;
				break;
			}
			i++;
		}
		
		final String[] EVENT_PROJECTION = new String[] {
				Events.CALENDAR_ID, Events.TITLE, Events.EVENT_LOCATION,
				Events.DTSTART, 	Events.DTEND,  
				Events.DESCRIPTION, Events.EVENT_TIMEZONE };
		
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Events.CONTENT_URI;
		
		String selection = "((" + Events.ACCOUNT_NAME + " = ?))";
		String[] selectionArgs = new String[] { acctName };
		cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
		
		i = 0;
		if(cur != null){
			while(!cur.isLast()){
				cur.moveToNext();
				CalendarInfoClass InfoClass = new CalendarInfoClass();
				InfoClass.setName(cur.getString(1));
				InfoClass.setLoaction(cur.getString(2));
				InfoClass.setSTime(cur.getString(3));
				InfoClass.setETime(cur.getString(4));
				InfoClass.setDescription(cur.getString(5));
				InfoClass.setTimezone(cur.getString(6));

				InfoClassList.add(InfoClass);
				i++;
			}
		}
		adapter = new CalendarListAdapter(this, R.layout.customlistview1);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	public void onClick(View view){
		if(view.getId() == R.id.button_dialog){
			final View innerView = getLayoutInflater().inflate(R.layout.calendar_dialog, null);
			adg = new AddDialog(this);
			adg.setTitle("추가");
			adg.setContentView(innerView);
			adg.setCancelable(true);
			LayoutParams params = adg.getWindow().getAttributes();
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			adg.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
			adg.show();
		}
//		else if(view.getId() == R.id.event_load_button){
//		}
	}
	
	public class CalendarListAdapter extends BaseAdapter{
		Context context;
		LayoutInflater Inflater;
		int layout;
		
		public CalendarListAdapter(Context context, int layout){
			this.context = context;
			Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.layout = layout;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return InfoClassList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int pos = position;
			if(convertView == null)
				convertView = Inflater.inflate(layout, parent, false);
			
			int year;
			int month;
			int day;
			int hour;
			int minute;
			Date date = new Date(Long.parseLong(InfoClassList.get(pos).getSTime()));
			SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy");
			SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM");
			SimpleDateFormat curDayFormat = new SimpleDateFormat("dd");
			SimpleDateFormat curHourFormat = new SimpleDateFormat("HH");
			SimpleDateFormat curMinuteFormat = new SimpleDateFormat("mm");
			year = Integer.parseInt(curYearFormat.format(date));		
			month = Integer.parseInt(curMonthFormat.format(date));		
			day = Integer.parseInt(curDayFormat.format(date));			
			hour = Integer.parseInt(curHourFormat.format(date));		
			minute = Integer.parseInt(curMinuteFormat.format(date));
			
			TextView text_name = (TextView) convertView.findViewById(R.id.custom_name_text);
			TextView text_location = (TextView) convertView.findViewById(R.id.custom_location_text);
			TextView text_time = (TextView) convertView.findViewById(R.id.custom_time_text);
			
			if(InfoClassList.size() != 0){
				text_name.setText(InfoClassList.get(pos).getName());
				text_location.setText(InfoClassList.get(pos).getLocation());
				text_time.setText(hour + "시 " + minute + "분");
				
			}
			return convertView;
		}
		
	}
}
