package com.android.alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.weatherplanner.R;

@SuppressLint("ValidFragment")
public class Tab_alarm extends Activity implements View.OnClickListener {
	ListView AlarmView;
	MyListAdapter MyAdapter;
	Button btnAdd;
	Button btnDel;

	DBHelper Helper;
	List<A_DbInfoClass> A_DbInfoClassList = new ArrayList<A_DbInfoClass>();

	boolean flag = true;
	boolean[] checked;
	int checkeditem = 0;

	public Tab_alarm() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_alarm);

		AlarmView = (ListView) findViewById(R.id.listView1);
		btnAdd = (Button) findViewById(R.id.addbtn);
		btnDel = (Button) findViewById(R.id.delbtn);
		btnAdd.setOnClickListener(this);
		btnDel.setOnClickListener(this);

		MyAdapter = new MyListAdapter(this, R.layout.itemswitch);
		AlarmView.setAdapter(MyAdapter);

	}

	@Override
	public void onResume() {
		super.onResume();
		Helper = new DBHelper(getApplicationContext());
		if (Helper.getDbInfoClasssCount() == 0)
			btnDel.setEnabled(false);
		else
			btnDel.setEnabled(true);
		A_sortClass mainSort = new A_sortClass();
		A_DbInfoClassList = mainSort.Sort(Helper.getAllDbInfoClasss());
		MyAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		Helper = new DBHelper(getApplicationContext());
		if (Helper.getDbInfoClasssCount() == 0)
			btnDel.setEnabled(false);
		else
			btnDel.setEnabled(true);
		A_sortClass mainSort = new A_sortClass();
		A_DbInfoClassList = mainSort.Sort(Helper.getAllDbInfoClasss());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addbtn:
			if (flag) {
				DialogTimePicker();
			} else {
				MyAdapter = new MyListAdapter(this, R.layout.itemswitch);
				AlarmView.setAdapter(MyAdapter);
				btnAdd.setText("+");
				flag = true;
				btnDel.setEnabled(true);
				MyAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.delbtn:
			if (flag) {
				MyAdapter = new MyListAdapter(this, R.layout.itemcheck);
				AlarmView.setAdapter(MyAdapter);
				btnAdd.setText("x");
				flag = false;
				checkeditem = 0;
				checked = new boolean[A_DbInfoClassList.size()];
				btnDel.setEnabled(false);
				MyAdapter.notifyDataSetChanged();
			} else {
				DialogDelete();
				MyAdapter = new MyListAdapter(this, R.layout.itemswitch);
				AlarmView.setAdapter(MyAdapter);
				btnAdd.setText("+");
				flag = true;
			}
			break;
		}
	}

	int nCheck = 0;

	public void DialogTimePicker() {
		nCheck = 0;
		GregorianCalendar calendar = new GregorianCalendar();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		TimePickerDialog dialog = new TimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						if (nCheck == 0) {
							nCheck = 1;
							String Hour = String.valueOf(hourOfDay);
							String Minute = String.valueOf(minute);
							A_DbInfoClass alarmInfo = new A_DbInfoClass(Hour,
									Minute, "true");
							Helper.addDbInfoClass(alarmInfo);
							A_setClass set_alarm = new A_setClass();
							set_alarm.AlarmSet(getApplicationContext(), Helper
									.getDbInfoClass(Helper
											.getDbInfoClasssCount()), Helper);

							Helper = new DBHelper(getApplicationContext());
							if (Helper.getDbInfoClasssCount() == 0)
								btnDel.setEnabled(false);
							else
								btnDel.setEnabled(true);
							A_sortClass mainSort = new A_sortClass();
							A_DbInfoClassList = mainSort.Sort(Helper
									.getAllDbInfoClasss());
							MyAdapter.notifyDataSetChanged();
						}
					}
				}, hour, minute, true);

		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				nCheck = 1;
			}
		});

		dialog.setButton(TimePickerDialog.BUTTON_NEGATIVE, "취소",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						nCheck = 1;
					}
				});

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK)
					nCheck = 1;
				return false;
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void DialogDelete() {
		new AlertDialog.Builder(this).setTitle("삭제")
				.setMessage("알람을 삭제하시겠습니까?").setIcon(R.drawable.icon_launcher)
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < checked.length; i++) {
							if (checked[i])
								Helper.deleteDbInfoClass(A_DbInfoClassList
										.get(i));
						}
						AlarmView.clearChoices();

						A_DbInfoClassList = Helper.getAllDbInfoClasss();
						Helper.deleteAllDbInfoClass();
						Helper.init_AutoIncrementValue();
						for (int i = 0; i < A_DbInfoClassList.size(); i++) {
							if (A_DbInfoClassList.get(i).getID() != i + 1)
								A_DbInfoClassList.get(i).setID(i + 1);
							Helper.addDbInfoClass(A_DbInfoClassList.get(i));
						}
						A_sortClass mainSort = new A_sortClass();
						A_DbInfoClassList = mainSort.Sort(Helper
								.getAllDbInfoClasss());
						MyAdapter.notifyDataSetChanged();
					}
				}).setNegativeButton("아니오", null).show();
	}

	class MyListAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater Inflater;
		int mlayout;

		public MyListAdapter(Context context, int layout) {
			mContext = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mlayout = layout;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return A_DbInfoClassList.size();
		}

		@Override
		public Object getItem(int position) {
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
			if (convertView == null) {
				convertView = Inflater.inflate(mlayout, parent, false);
			}
			if (flag) {
				TextView txt = (TextView) convertView.findViewById(R.id.text);
				txt.setText(A_DbInfoClassList.get(position).getHour() + "시 "
						+ A_DbInfoClassList.get(position).getMin() + "분");
				final Switch sw = (Switch) convertView.findViewById(R.id.swch);
				setSwitchCheckedValue(sw, pos);
				sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							A_DbInfoClassList.get(pos).setCheck("true");
							A_setClass on_alarm = new A_setClass();
							on_alarm.AlarmSet(mContext,
									A_DbInfoClassList.get(pos), Helper);
						} else {
							A_DbInfoClassList.get(pos).setCheck("false");
							AlarmManager alarm = (AlarmManager) mContext
									.getSystemService(Context.ALARM_SERVICE);
							Intent intent = new Intent(mContext,
									A_Receive.class);
							PendingIntent pender = PendingIntent.getBroadcast(
									mContext, A_DbInfoClassList.get(pos)
											.getID(), intent,
									PendingIntent.FLAG_ONE_SHOT);
							alarm.cancel(pender);
						}
						Helper.updateDbInfoClass(A_DbInfoClassList.get(pos));
					}
				});
			} else {
				TextView txt = (TextView) convertView
						.findViewById(R.id.deltext);
				txt.setText(A_DbInfoClassList.get(position).getHour() + "시 "
						+ A_DbInfoClassList.get(position).getMin() + "분");
				CheckBox chk = (CheckBox) convertView.findViewById(R.id.chkbox);
				chk.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							checked[pos] = true;
							checkeditem++;
						} else {
							checked[pos] = false;
							checkeditem--;
						}
						if (checkeditem == 0)
							btnDel.setEnabled(false);
						else
							btnDel.setEnabled(true);
					}
				});
				chk.setChecked(checked[position]);
			}
			return convertView;
		}
	}

	public void setSwitchCheckedValue(Switch swc, int pos) {
		if (A_DbInfoClassList.get(pos).getCheck().equals("true")) {
			swc.setChecked(true);
		} else {
			swc.setChecked(false);
		}
	}
}