package com.android.weatherplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.alarm.Tab_alarm;
import com.android.calendar.CalendarActivity;
import com.android.dayweather.Tab_Weather;
import com.nhn.android.mapviewer.Tab_Map;

public class MenuFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String[] arr = { "  날씨", "  일정", "  교통", "  알람" };
		ArrayAdapter<String> ad = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), R.layout.listview_item, arr);

		setListAdapter(ad);
		getListView().setBackgroundResource(android.R.color.holo_blue_light);
		getListView().setCacheColorHint(0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		switch (position) {
		case 0:
			startActivity(new Intent(getActivity(), Tab_Weather.class));
			break;
		case 1:
			startActivity(new Intent(getActivity(), CalendarActivity.class));
			break;
		case 2:
			startActivity(new Intent(getActivity(), Tab_Map.class));
			break;
		case 3:
			startActivity(new Intent(getActivity(), Tab_alarm.class));
			break;
		}

		((MenuActivity) getActivity()).getSlideoutHelper().close();
	}

}
