package com.nhn.android.mapviewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.android.weatherplanner.R;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class Tab_Map extends Activity implements View.OnClickListener {
	ListView MapView;
	Button btnAdd;
	ArrayList<String> item;
	ArrayAdapter<String> adapter;

	public Tab_Map() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tap_map);

		MapView = (ListView) findViewById(R.id.maplistView);
		btnAdd = (Button) findViewById(R.id.viewmap);
		btnAdd.setOnClickListener(this);
		item = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, item);
		MapView.setAdapter(adapter);
		MapView.setOnItemClickListener(new ListViewItemClickListener());
		MapView.setOnItemLongClickListener(new ListViewItemLongClickListener());

		SharedPreferences pref = getSharedPreferences("Pref",
				Activity.MODE_PRIVATE);
		item.add("=====최근 검색=====");
		if (pref.getString("recent_map", "") != ""
				&& pref.getString("consume_time", "") != "") {
			item.add(pref.getString("recent_map", "") + " "
					+ pref.getString("consume_time", ""));

			if (pref.getString("recent_map", "").equals("인제대 ~ 수로왕릉")) {
				ImageView imgview = (ImageView) findViewById(R.id.imageView1);
				imgview.setImageResource(R.drawable.img1);
			}
			if (pref.getString("recent_map", "").equals("인제대 ~ 남산공원")) {
				ImageView imgview = (ImageView) findViewById(R.id.imageView1);
				imgview.setImageResource(R.drawable.img2);
			}
			if (pref.getString("recent_map", "").equals("인제대 ~ 연지공원")) {
				ImageView imgview = (ImageView) findViewById(R.id.imageView1);
				imgview.setImageResource(R.drawable.img3);
			}
			if (pref.getString("recent_map", "").equals("인제대 ~ 김해시민체육공원")) {
				ImageView imgview = (ImageView) findViewById(R.id.imageView1);
				imgview.setImageResource(R.drawable.img4);
			}
			if (pref.getString("recent_map", "").equals("인제대 ~ 김해봉황동유적")) {
				ImageView imgview = (ImageView) findViewById(R.id.imageView1);
				imgview.setImageResource(R.drawable.img5);
			}
			if (pref.getString("recent_map", "").equals("인제대 ~ 국립김해박물관")) {
				ImageView imgview = (ImageView) findViewById(R.id.imageView1);
				imgview.setImageResource(R.drawable.img6);
			}
			if (pref.getString("recent_map", "").equals("인제대 ~ 수로왕비릉")) {
				ImageView imgview = (ImageView) findViewById(R.id.imageView1);
				imgview.setImageResource(R.drawable.img7);
			}
		}

		File mFile;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			mFile = new File(getApplicationContext().getExternalCacheDir(),
					"/databases/busan_bus.sqlite");
		} else {
			mFile = new File(getApplicationContext().getCacheDir(),
					"/databases/busan_bus.sqlite");
		}

		File mDir;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			mDir = new File(getApplicationContext().getExternalCacheDir(),
					"/databases");
		} else {
			mDir = new File(getApplicationContext().getCacheDir(), "/databases");
		}

		if (!mFile.exists()) {
			mDir.mkdirs();

			AssetManager am = null;
			InputStream[] arrIs = new InputStream[3];
			BufferedInputStream[] arrBis = new BufferedInputStream[3];
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;

			try {
				am = getResources().getAssets();

				for (int i = 0; i < arrIs.length; i++) {
					arrIs[i] = am.open("busan_bus" + (i + 1) + ".sqlite");
					arrBis[i] = new BufferedInputStream(arrIs[i]);
				}

				fos = new FileOutputStream(mFile);
				bos = new BufferedOutputStream(fos);
				int read = -1;
				byte[] buffer = new byte[1024];

				for (int i = 0; i < arrIs.length; i++) {
					while ((read = arrBis[i].read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, read);
					}
					bos.flush();
				}

			} catch (Exception e) {
			} finally {
				for (int i = 0; i < arrIs.length; i++) {
					try {
						if (arrIs[i] != null)
							arrIs[i].close();
					} catch (Exception e) {
					}
					try {
						if (arrBis[i] != null)
							arrBis[i].close();
					} catch (Exception e) {
					}
				}
				try {
					if (fos != null)
						fos.close();
				} catch (Exception e) {
				}
				try {
					if (bos != null)
						bos.close();
				} catch (Exception e) {
				}
				arrIs = null;
				arrBis = null;
			}
		}
	}

	AlertDialog.Builder dialogBuilder;
	int sel;

	private class ListViewItemLongClickListener implements
			AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			sel = arg2;
			dialogBuilder = new AlertDialog.Builder(getApplicationContext());
			dialogBuilder.setTitle("삭제");
			dialogBuilder.setMessage("삭제 하시겠습니까?");
			dialogBuilder.setPositiveButton("예",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							item.remove(sel);
							arg0.dismiss();
						}
					});
			dialogBuilder.setNegativeButton("아니요",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
						}
					});
			AlertDialog alert = dialogBuilder.create();
			alert.show();
			return true;
		}
	}

	private class ListViewItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		MapView.setBackgroundColor(Color.BLACK);
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.viewmap:
			Intent intent2 = new Intent(getApplicationContext(),
					NMapViewer.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);
			break;
		}
	}
}