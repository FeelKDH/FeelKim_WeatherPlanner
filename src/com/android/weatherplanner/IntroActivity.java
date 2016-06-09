package com.android.weatherplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class IntroActivity extends Activity {

	Handler h;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_layout);
		h = new Handler();
		h.postDelayed(irun, 2500);
	}

	Runnable irun = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent i = new Intent(IntroActivity.this, MainActivity.class);
			startActivity(i);
			finish();

			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
		}

	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		h.removeCallbacks(irun);
	}

}
