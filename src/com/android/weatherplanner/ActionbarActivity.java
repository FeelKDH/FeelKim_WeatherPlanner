package com.android.weatherplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;

import com.android.slideout.SlideoutActivity;

public class ActionbarActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			int width = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 40, getResources()
							.getDisplayMetrics());
			SlideoutActivity.prepare(ActionbarActivity.this,
					R.id.inner_content, width);
			startActivity(new Intent(ActionbarActivity.this, MenuActivity.class));
			overridePendingTransition(0, 0);
		}
		return true;
	}

}
