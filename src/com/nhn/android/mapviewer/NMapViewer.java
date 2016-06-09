/* 
 * NMapViewer.java $version 2010. 1. 1
 * 
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

package com.nhn.android.mapviewer;

/* 
 * NMapViewer.java $version 2010. 1. 1
 * 
 * Copyright 2010 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

/**
 * Sample class for map viewer library.
 * 
 * @author kyjkim
 */
public class NMapViewer extends NMapActivity implements
		OnCalloutOverlayListener {
	// 여기부터 네이버 맵을 사용하는 변수 선언
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	// set your API key which is registered for NMapViewer library.
	private static final String API_KEY = "8bdcdd716cb09fb0357d3ffa08b8f06d";

	private MapContainerView mMapContainerView;

	private NMapView mMapView;
	private NMapController mMapController;

	private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
	private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
	private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
	private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
	private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
	private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";

	private SharedPreferences mPreferences;

	private NMapMyLocationOverlay mMyLocationOverlay;
	private NMapLocationManager mMapLocationManager;
	private NMapCompassManager mMapCompassManager;

	// 오버레이의 리소스를 제공하기 위한 객체
	NMapViewerResourceProvider mMapViewerResourceProvider = null;
	// 오버레이 관리자
	NMapOverlayManager mOverlayManager;

	MHandler mHandler = new MHandler();

	private NMapPOIdataOverlay mFloatingPOIdataOverlay;
	private NMapPOIitem mFloatingPOIitem;

	final Context context = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 상단액션바 없애기

		// =====================================================//
		// 여기부터 맵 생성 및 보여주기 //
		// =====================================================//

		// create map view
		mMapView = new NMapView(this);

		// set a registered API key for Open MapViewer Library
		mMapView.setApiKey(API_KEY);

		// create parent view to rotate map view
		mMapContainerView = new MapContainerView(this);
		mMapContainerView.addView(mMapView);

		// set the activity content to the parent view
		setContentView(mMapContainerView);

		// initialize map view
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setFocusable(true);
		mMapView.setFocusableInTouchMode(true);
		mMapView.requestFocus();

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
		mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
		mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

		// use map controller to zoom in/out, pan and set map center, zoom level
		// etc.
		mMapController = mMapView.getMapController();

		// use built in zoom controls
		NMapView.LayoutParams lp = new NMapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				NMapView.LayoutParams.BOTTOM_RIGHT);
		mMapView.setBuiltInZoomControls(true, lp);

		// create resource provider
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

		// create overlay manager 오버레이 관리자 추가
		mOverlayManager = new NMapOverlayManager(this, mMapView,
				mMapViewerResourceProvider);

		// 지도 중심 좌표 및 축척 레벨을 설정한다. 축척 레벨을 지정하지 않으면 중심 좌표만 변경된다. 유효 축척 레벨 범위는
		// 1~14이다.
		// set data provider listener
		super.setMapDataProviderListener(onDataProviderListener);

		// 오버레이 이벤트 등록
		mOverlayManager.setOnCalloutOverlayListener(this);

		// create overlay manager
		mOverlayManager = new NMapOverlayManager(this, mMapView,
				mMapViewerResourceProvider);
		// register callout overlay listener to customize it.

		// location manager
		mMapLocationManager = new NMapLocationManager(this);
		mMapLocationManager
				.setOnLocationChangeListener(onMyLocationChangeListener);

		// compass manager
		mMapCompassManager = new NMapCompassManager(this);

		// create my location overlay
		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(
				mMapLocationManager, mMapCompassManager);

		Intent intent2 = getIntent();
		if (intent2.getExtras() == null) {
			startMyLocation();
		} else {
			Draw();
		}
	}

	/** 오버레이가 클릭되었을 때의 이벤트 **/
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay arg0,
			NMapOverlayItem arg1, Rect arg2) {
		return null;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {

		stopMyLocation();

		super.onStop();
	}

	@Override
	protected void onDestroy() {

		// save map view state such as map center position and zoom level.
		saveInstanceState();

		super.onDestroy();
	}

	// 액티비티가 활성화되면 mylocation 이 시작됨
	private void startMyLocation() {

		if (mMyLocationOverlay != null) { // 최초 내위치용 오버레이가 생성된 경우만 사용
			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) { // 현재 오버레이
																	// 매니져가 my
																	// location
																	// 오버레이를 가지고
																	// 있지 않으면
				mOverlayManager.addOverlay(mMyLocationOverlay); // my location
																// 오버레이를 추가
			}

			if (mMapLocationManager.isMyLocationEnabled()) { // 이미 내위치를 확인하는
																// 오버레이가 있는 경우
																// 나침반을 이용한 컴퍼스를
																// 보여준다.

				if (!mMapView.isAutoRotateEnabled()) { // 나침반 기능이 활성화 되어있지 않으면
					mMyLocationOverlay.setCompassHeadingVisible(true);

					mMapCompassManager.enableCompass();

					mMapView.setAutoRotateEnabled(true, false);

					mMapContainerView.requestLayout();
				} else { // 나침반 컴퍼스가 활성화 되어 있으면 my location 정지!
					stopMyLocation();
				}

				mMapView.postInvalidate(); // 별도의 스레드에서 화면갱신을 할때 사용
			} else {
				boolean isMyLocationEnabled = mMapLocationManager
						.enableMyLocation(false);
				if (!isMyLocationEnabled) {
					Toast.makeText(
							NMapViewer.this,
							"Please enable a My Location source in system settings",
							Toast.LENGTH_LONG).show();

					Intent goToSettings = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(goToSettings);

					return;
				}
			}
		}
	}

	// 액티비티가 활성화되면 mylocation 이 중지됨
	private void stopMyLocation() {
		if (mMyLocationOverlay != null) { // 최초 내위치용 오버레이가 생성된 경우만 사용
			mMapLocationManager.disableMyLocation();

			if (mMapView.isAutoRotateEnabled()) {
				mMyLocationOverlay.setCompassHeadingVisible(false);

				mMapCompassManager.disableCompass();

				mMapView.setAutoRotateEnabled(false, false);

				mMapContainerView.requestLayout();
			}
		}
	}

	/* NMapDataProvider Listener */
	private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

		@Override
		public void onReverseGeocoderResponse(NMapPlacemark placeMark,
				NMapError errInfo) {

			if (DEBUG) {
				Log.i(LOG_TAG, "onReverseGeocoderResponse: placeMark="
						+ ((placeMark != null) ? placeMark.toString() : null));
			}

			if (errInfo != null) {
				Log.e(LOG_TAG, "Failed to findPlacemarkAtLocation: error="
						+ errInfo.toString());

				Toast.makeText(NMapViewer.this, errInfo.toString(),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
				mFloatingPOIdataOverlay.deselectFocusedPOIitem();

				if (placeMark != null) {
					mFloatingPOIitem.setTitle(placeMark.toString());
				}
				mFloatingPOIdataOverlay.selectPOIitemBy(
						mFloatingPOIitem.getId(), false);
			}
		}
	};

	/* MyLocation Listener */
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

		@Override
		public boolean onLocationChanged(NMapLocationManager locationManager,
				NGeoPoint myLocation) {
			if (mMapController != null) {
				mMapController.animateTo(myLocation);

			}
			return true;
		}

		@Override
		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

			Toast.makeText(NMapViewer.this,
					"Your current location is temporarily unavailable.",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationUnavailableArea(
				NMapLocationManager locationManager, NGeoPoint myLocation) {

			Toast.makeText(NMapViewer.this,
					"Your current location is unavailable area.",
					Toast.LENGTH_LONG).show();

			stopMyLocation();
		}
	};

	/* MapView State Change Listener */
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

			if (errorInfo == null) { // success
				// restore map view state such as map center position and zoom
				// level.
				// restoreInstanceState();

			} else { // fail
				Log.e(LOG_TAG,
						"onFailedToInitializeWithError: "
								+ errorInfo.toString());

				Toast.makeText(NMapViewer.this, errorInfo.toString(),
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onAnimationStateChange(NMapView mapView, int animType,
				int animState) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType
						+ ", animState=" + animState);
			}
		}

		@Override
		public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
			}
		}

		@Override
		public void onZoomLevelChange(NMapView mapView, int level) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
			}
		}

		@Override
		public void onMapCenterChangeFine(NMapView mapView) {

		}
	};

	private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

		@Override
		public void onLongPress(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onLongPressCanceled(NMapView mapView) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTouchDown(NMapView mapView, MotionEvent ev) {
			// 클릭했을경우의 이벤트처리.
		}

		@Override
		public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
		}

		@Override
		public void onTouchUp(NMapView arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
		}
	};

	private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

		@Override
		public boolean isLocationTracking() {
			if (mMapLocationManager != null) {
				if (mMapLocationManager.isMyLocationEnabled()) {
					return mMapLocationManager.isMyLocationFixed();
				}
			}
			return false;
		}
	};

	private void saveInstanceState() {
		if (mPreferences == null) {
			return;
		}
		NGeoPoint center = mMapController.getMapCenter();
		int level = mMapController.getZoomLevel();
		int viewMode = mMapController.getMapViewMode();
		boolean trafficMode = mMapController.getMapViewTrafficMode();
		boolean bicycleMode = mMapController.getMapViewBicycleMode();

		SharedPreferences.Editor edit = mPreferences.edit();

		edit.putInt(KEY_CENTER_LONGITUDE, center.getLongitudeE6());
		edit.putInt(KEY_CENTER_LATITUDE, center.getLatitudeE6());
		edit.putInt(KEY_ZOOM_LEVEL, level);
		edit.putInt(KEY_VIEW_MODE, viewMode);
		edit.putBoolean(KEY_TRAFFIC_MODE, trafficMode);
		edit.putBoolean(KEY_BICYCLE_MODE, bicycleMode);

		edit.commit();
	}

	/* Menus */
	private static final int MENU_ITEM_MAP_MODE = 10;
	private static final int MENU_ITEM_MAP_MODE_SUB_VECTOR = MENU_ITEM_MAP_MODE + 1;
	private static final int MENU_ITEM_MAP_MODE_SUB_SATELLITE = MENU_ITEM_MAP_MODE + 2;
	private static final int MENU_ITEM_MAP_MODE_SUB_HYBRID = MENU_ITEM_MAP_MODE + 3;
	private static final int MENU_ITEM_MAP_MODE_SUB_TRAFFIC = MENU_ITEM_MAP_MODE + 4;
	private static final int MENU_ITEM_MY_LOCATION = 20;

	// 추가한 메뉴 버튼에 사용되는 상수
	private static final int MENU_ITEM_TRAFFICLINE_TEST = 30;
	private static final int MENU_ITEM_ATTRACTIONLINE_TEST = 40;

	/**
	 * Invoked during init to give the Activity a chance to set up its Menu.
	 * 
	 * @param menu
	 *            the Menu to which entries may be added
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem menuItem = null;
		SubMenu subMenu = null;

		subMenu = menu.addSubMenu(Menu.NONE, MENU_ITEM_MAP_MODE,
				Menu.CATEGORY_SECONDARY, "Map mode");
		subMenu.setIcon(android.R.drawable.ic_menu_mapmode);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_VECTOR, Menu.NONE,
				"Standard");
		menuItem.setAlphabeticShortcut('a');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_SATELLITE, Menu.NONE,
				"Satellite");
		menuItem.setAlphabeticShortcut('b');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_HYBRID, Menu.NONE,
				"Hybrid");
		menuItem.setAlphabeticShortcut('c');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_TRAFFIC, Menu.NONE,
				"Traffic");
		menuItem.setAlphabeticShortcut('d');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = menu.add(0, MENU_ITEM_MY_LOCATION, Menu.CATEGORY_SECONDARY,
				"Compass mode");
		menuItem.setAlphabeticShortcut('f');
		menuItem.setIcon(android.R.drawable.ic_menu_mylocation);

		menuItem = menu.add(0, MENU_ITEM_TRAFFICLINE_TEST,
				Menu.CATEGORY_SECONDARY, "Bus Information");
		menuItem.setAlphabeticShortcut('g'); // 단축키 설정
		menuItem.setIcon(android.R.drawable.ic_menu_mylocation); // 아이콘 이미지 설정

		menuItem = menu.add(0, MENU_ITEM_ATTRACTIONLINE_TEST,
				Menu.CATEGORY_SECONDARY, "Attraction Searching");
		menuItem.setAlphabeticShortcut('h'); // 단축키 설정

		menuItem.setIcon(android.R.drawable.ic_menu_mylocation);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu pMenu) {
		super.onPrepareOptionsMenu(pMenu);

		int viewMode = mMapController.getMapViewMode();
		boolean isTraffic = mMapController.getMapViewTrafficMode();

		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_VECTOR).setChecked(
				viewMode == NMapView.VIEW_MODE_VECTOR);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_SATELLITE).setChecked(
				viewMode == NMapView.VIEW_MODE_SATELLITE);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_HYBRID).setChecked(
				viewMode == NMapView.VIEW_MODE_HYBRID);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_TRAFFIC).setChecked(isTraffic);

		if (mMyLocationOverlay == null) {
			pMenu.findItem(MENU_ITEM_MY_LOCATION).setEnabled(false);
		}

		return true;
	}

	/**
	 * Invoked when the user selects an item from the Menu.
	 * 
	 * @param item
	 *            the Menu entry which was selected
	 * @return true if the Menu item was legit (and we consumed it), false
	 *         otherwise
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case MENU_ITEM_MAP_MODE_SUB_VECTOR:
			mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
			return true;

		case MENU_ITEM_MAP_MODE_SUB_SATELLITE:
			mMapController.setMapViewMode(NMapView.VIEW_MODE_SATELLITE);
			return true;

		case MENU_ITEM_MAP_MODE_SUB_HYBRID:
			mMapController.setMapViewMode(NMapView.VIEW_MODE_HYBRID);
			return true;

		case MENU_ITEM_MAP_MODE_SUB_TRAFFIC:
			mMapController.setMapViewTrafficMode(!mMapController
					.getMapViewTrafficMode());
			return true;

		case MENU_ITEM_MY_LOCATION:
			startMyLocation();
			return true;

		case MENU_ITEM_TRAFFICLINE_TEST:
			DialogProgress();
			/*
			 * Intent intent3 = new Intent(NMapViewer.this, Map_Parser.class);
			 * startActivity(intent3);
			 */
			return true;

		case MENU_ITEM_ATTRACTIONLINE_TEST:
			SingleChoice();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Container view class to rotate map view.
	 */
	private class MapContainerView extends ViewGroup {

		public MapContainerView(Context context) {
			super(context);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			final int width = getWidth();
			final int height = getHeight();
			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);
				final int childWidth = view.getMeasuredWidth();
				final int childHeight = view.getMeasuredHeight();
				final int childLeft = (width - childWidth) / 2;
				final int childTop = (height - childHeight) / 2;
				view.layout(childLeft, childTop, childLeft + childWidth,
						childTop + childHeight);
			}
			if (changed) {
				mOverlayManager.onSizeChanged(width, height);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			int h = getDefaultSize(getSuggestedMinimumHeight(),
					heightMeasureSpec);
			int sizeSpecWidth = widthMeasureSpec;
			int sizeSpecHeight = heightMeasureSpec;

			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);

				if (view instanceof NMapView) {
					if (mMapView.isAutoRotateEnabled()) {
						int diag = (((int) (Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
						sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag,
								MeasureSpec.EXACTLY);
						sizeSpecHeight = sizeSpecWidth;
					}
				}
				view.measure(sizeSpecWidth, sizeSpecHeight);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void DialogProgress() {
		BusanBus_data();

		final ProgressDialog dialog = ProgressDialog.show(NMapViewer.this, "",
				"Searching for a station.", true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (searching)
					;
				dialog.dismiss();
				searching = true;
			}
		}).start();
	}

	public void Busline(NGeoPoint start, NGeoPoint end) {
		Intent intent2 = getIntent();
		File mFile;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			mFile = new File(getApplicationContext().getApplicationContext()
					.getExternalCacheDir(), "/databases/busan_bus.sqlite");
		} else {
			mFile = new File(getApplicationContext().getCacheDir(),
					"/databases/busan_bus.sqlite");
		}
		SQLiteDatabase mDb;
		mDb = SQLiteDatabase.openDatabase(mFile.getPath(), null,
				SQLiteDatabase.CREATE_IF_NECESSARY);

		Cursor c = mDb.rawQuery(
				"SELECT DISTINCT BUSLINENUM, ORD FROM BUSLINE WHERE X=\""
						+ Double.toString(start.getLongitude()) + "\" AND Y=\""
						+ Double.toString(start.getLatitude()) + "\";", null);
		Cursor c2 = mDb.rawQuery(
				"SELECT DISTINCT BUSLINENUM, ORD FROM BUSLINE WHERE X=\""
						+ Double.toString(end.getLongitude()) + "\" AND Y=\""
						+ Double.toString(end.getLatitude()) + "\";", null);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			for (int j = 0; j < c2.getCount(); j++) {
				c2.moveToFirst();
				if (c.getString(0).equals(c2.getString(0))) {
					Cursor c3 = mDb.rawQuery(
							"SELECT X, Y, BUSSTOPNAME FROM BUSLINE WHERE BUSLINENUM=\""
									+ c.getString(0) + "\";", null);

					NMapPathData pathData = new NMapPathData(c3.getCount());
					pathData.initPathData();
					c3.moveToPosition(c.getInt(1) - 1);
					pathData.addPathPoint(Double.parseDouble(c3.getString(0)),
							Double.parseDouble(c3.getString(1)),
							NMapPathLineStyle.TYPE_SOLID);
					for (int k = 0; k < c2.getInt(1) - c.getInt(1); k++) {
						c3.moveToNext();
						pathData.addPathPoint(
								Double.parseDouble(c3.getString(0)),
								Double.parseDouble(c3.getString(1)), 0);
					}
					pathData.endPathData();
					NMapPathDataOverlay pathDataOverlay = mOverlayManager
							.createPathDataOverlay(pathData);

					NMapPOIdata poiData = new NMapPOIdata(2,
							mMapViewerResourceProvider);
					poiData.beginPOIdata(c2.getInt(1) - c.getInt(1) + 1);

					poiData.addPOIitem(
							Double.parseDouble(intent2.getExtras().getString(
									"FROM_Y")),
							Double.parseDouble(intent2.getExtras().getString(
									"FROM_X")),
							intent2.getExtras().getString("FROM_NAME"),
							NMapPOIflagType.FROM, 0);
					poiData.addPOIitem(
							Double.parseDouble(intent2.getExtras().getString(
									"TO_Y")),
							Double.parseDouble(intent2.getExtras().getString(
									"TO_X")),
							intent2.getExtras().getString("TO_NAME"),
							NMapPOIflagType.TO, 0);
					poiData.endPOIdata();
					mFloatingPOIdataOverlay = mOverlayManager
							.createPOIdataOverlay(poiData, null);
					mFloatingPOIdataOverlay.showAllPOIdata(0);
					mFloatingPOIdataOverlay
							.setOnStateChangeListener(onPOIdataStateChangeListener);
					

				}
				c2.moveToNext();
			}
			c.moveToNext();
		}
	}

	NMapPOIitem from_item, temp_item;
	NMapPOIdataOverlay temp_poiDataOverlay;
	boolean flag1 = false;
	boolean flag2 = false;
	private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

		@Override
		public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay,
				NMapPOIitem item) {
			// TODO Auto-generated method stub
			Toast.makeText(NMapViewer.this,
					"onCalloutClick: " + item.getTitle(), Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
			// TODO Auto-generated method stub
			if (item != null) {
				Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
			} else {
				Log.i(LOG_TAG, "onFocusChanged: ");
			}
		}

	};

	//
	// @Override
	// public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay,
	// NMapPOIitem item) {
	// Log.e("====info====" , "head Text : " + item.getHeadText() + " - " +
	// item.getTitle() + " - " + item.getTailText());
	// Log.e("tester", "_____");
	//
	//
	//
	// }
	//
	// // @Override
	// public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay,
	// NMapPOIitem item) {
	// if (flag2) {
	// if (!flag1) {
	// item.setMarkerId(NMapPOIflagType.FROM);
	// from_item = item;
	// flag1 = true;
	// Toast.makeText(NMapViewer.this, "도착지를 선택하세요",
	// Toast.LENGTH_LONG).show();
	// } else if (flag1 && item.getMarkerId() != NMapPOIflagType.FROM) {
	// item.setMarkerId(NMapPOIflagType.TO);
	// temp_item = item;
	// temp_poiDataOverlay = poiDataOverlay;
	// AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
	// NMapViewer.this);
	// dialogBuilder.setTitle("추가");
	// dialogBuilder.setMessage("출발지 : " + from_item.getTitle()
	// + ", 도착지 : " + temp_item.getTitle());
	// dialogBuilder.setPositiveButton("추가",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface arg0,
	// int arg1) {
	// File mFile;
	// if (Environment.getExternalStorageState()
	// .equals(Environment.MEDIA_MOUNTED)) {
	// mFile = new File(
	// getApplicationContext()
	// .getApplicationContext()
	// .getExternalCacheDir(),
	// "/databases/busan_bus.sqlite");
	// } else {
	// mFile = new File(
	// getApplicationContext()
	// .getCacheDir(),
	// "/databases/busan_bus.sqlite");
	// }
	// SQLiteDatabase mDb;
	// mDb = SQLiteDatabase.openDatabase(
	// mFile.getPath(), null,
	// SQLiteDatabase.CREATE_IF_NECESSARY);
	// String temp =
	// "INSERT INTO FAVORITE (FROMW, TOW, FROM_X, FROM_Y, TO_X, TO_Y, FROM_NAME, TO_NAME) VALUES (\""
	// + from_item.getTitle()
	// + "\", \""
	// + temp_item.getTitle()
	// + "\", \""
	// + Double.toString(from_item
	// .getPoint().getLatitude())
	// + "\", \""
	// + Double.toString(from_item
	// .getPoint().getLongitude())
	// + "\", \""
	// + Double.toString(temp_item
	// .getPoint().getLatitude())
	// + "\", \""
	// + Double.toString(temp_item
	// .getPoint().getLongitude())
	// + "\", \""
	// + from_item.getTitle()
	// + "\", \""
	// + temp_item.getTitle()
	// + "\");";
	// mDb.execSQL(temp);
	// finish();
	// arg0.dismiss();
	// }
	// });
	// dialogBuilder.setNegativeButton("취소",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface arg0,
	// int arg1) {
	// temp_item.setMarkerId(NMapPOIflagType.PIN);
	// from_item.setMarkerId(NMapPOIflagType.PIN);
	// flag2 = false;
	// flag1 = false;
	// arg0.dismiss();
	// }
	// });
	// AlertDialog alert = dialogBuilder.create();
	// alert.show();
	// }
	// }
	// }

	public void Draw() {
		Intent intent2 = getIntent();
		Busline(new NGeoPoint(Double.parseDouble(intent2.getExtras().getString(
				"FROM_Y")), Double.parseDouble(intent2.getExtras().getString(
				"FROM_X"))),
				new NGeoPoint(Double.parseDouble(intent2.getExtras().getString(
						"TO_Y")), Double.parseDouble(intent2.getExtras()
						.getString("TO_X"))));
	}

	private NotesDbAdapter dbAdapter;
	protected ArrayList<Object> bus_allMarker = new ArrayList<Object>();
	protected ArrayList<Object> bus_allMarker2 = new ArrayList<Object>();
	Bundle bus_bundle = new Bundle();

	public void BusanBus_data() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File mFile;
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					mFile = new File(getApplicationContext()
							.getApplicationContext().getExternalCacheDir(),
							"/databases/busan_bus.sqlite");
				} else {
					mFile = new File(getApplicationContext().getCacheDir(),
							"/databases/busan_bus.sqlite");
				}
				SQLiteDatabase mDb;
				mDb = SQLiteDatabase.openDatabase(mFile.getPath(), null,
						SQLiteDatabase.CREATE_IF_NECESSARY);

				Cursor c = mDb.rawQuery(
						"select _id, BUSSTOPNAME, X, Y, DISTANCE from BUSLINE",
						null);
				c.moveToFirst();

				double distance;
				Location locationA = new Location("p A");
				Location locationB = new Location("p D");
				locationA.setLatitude(mMapLocationManager.getMyLocation()
						.getLatitudeE6() / 1E6);
				locationA.setLongitude(mMapLocationManager.getMyLocation()
						.getLongitudeE6() / 1E6);
				for (int i = 0; i < c.getCount(); i++) {
					locationB.setLatitude(Double.parseDouble(c.getString(3)));
					locationB.setLongitude(Double.parseDouble(c.getString(2)));
					distance = locationA.distanceTo(locationB);
					if (distance < 300) {
						bus_allMarker.add(new NGeoPoint(Double.parseDouble(c
								.getString(2)), Double.parseDouble(c
								.getString(3))));
						bus_allMarker2.add(new String(c.getString(1)));
						bus_bundle.putInt("_id",
								Integer.parseInt(c.getString(0)));
						bus_bundle.putDouble("_dis",
								Double.parseDouble(c.getString(4)));
					}
					c.moveToNext();
				}
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	boolean searching = true;
	int markerID = NMapPOIflagType.PIN;

	NGeoPoint nGeopoint;

	@SuppressLint("HandlerLeak")
	class MHandler extends Handler {
		@Override
		public void handleMessage(Message Msg) {

			NMapPOIdata poiData = new NMapPOIdata(bus_allMarker.size(),
					mMapViewerResourceProvider);

			Log.e("tester", "comon1");

			poiData.beginPOIdata(bus_allMarker.size());
			for (int i = 0; i < bus_allMarker.size(); i++) {
				nGeopoint = (NGeoPoint) bus_allMarker.get(i);
				poiData.addPOIitem(nGeopoint, (String) bus_allMarker2.get(i),
						markerID, i);

			}
			poiData.endPOIdata();

			mFloatingPOIdataOverlay = mOverlayManager.createPOIdataOverlay(
					poiData, null);

			mFloatingPOIdataOverlay
					.setOnStateChangeListener(onPOIdataStateChangeListener);

			searching = false;
		}
	}

	// 명소 test
	public void SingleChoice() {

		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("명소 선택");
		final String items[] = { "수로왕릉", "남산공원", "연지공원", "김해시민체육공원", "국립김해박물관",
				"김해봉황동유적", "수로왕비릉" };

		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// TODO Auto-generated method stub
						if (items[which] == "수로왕릉") {
							Sooro_data();
							final ProgressDialog dialog2 = ProgressDialog.show(
									NMapViewer.this, "",
									"예상소요시간:37분  이용가능버스:1번", true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(2000);
									} catch (Throwable ex) {
										ex.printStackTrace();
									}
									dialog2.dismiss();
								}
							}).start();
						}
						if (items[which] == "남산공원") {
							NamsanPark_data();
							final ProgressDialog dialog2 = ProgressDialog.show(
									NMapViewer.this, "",
									"예상소요시간:24분 이용가능버스:128-1번", true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(2000);
									} catch (Throwable ex) {
										ex.printStackTrace();
									}
									dialog2.dismiss();
								}
							}).start();
						}
						if (items[which] == "연지공원") {
							YeonjiPark_data();
							final ProgressDialog dialog2 = ProgressDialog.show(
									NMapViewer.this, "",
									"예상소요시간:36분 이용가능버스:128-1번", true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(2000);
									} catch (Throwable ex) {
										ex.printStackTrace();
									}
									dialog2.dismiss();
								}
							}).start();
						}
						if (items[which] == "김해시민체육공원") {
							PhysicalTrainingPark_data();
							final ProgressDialog dialog2 = ProgressDialog.show(
									NMapViewer.this, "",
									"예상소요시간:55분 이용가능버스:1번", true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(2000);
									} catch (Throwable ex) {
										ex.printStackTrace();
									}
									dialog2.dismiss();
								}
							}).start();
						}
						if (items[which] == "국립김해박물관") {
							KimhaeMuseum_data();
							final ProgressDialog dialog2 = ProgressDialog.show(
									NMapViewer.this, "",
									"예상소요시간:33분 이용가능버스:128-1번", true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(2000);
									} catch (Throwable ex) {
										ex.printStackTrace();
									}
									dialog2.dismiss();
								}
							}).start();
						}
						if (items[which] == "김해봉황동유적") {
							KimhaeBH_data();
							final ProgressDialog dialog2 = ProgressDialog.show(
									NMapViewer.this, "",
									"예상소요시간:39분 이용가능버스:1번,1-1번", true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(2000);
									} catch (Throwable ex) {
										ex.printStackTrace();
									}
									dialog2.dismiss();
								}
							}).start();
						}
						if (items[which] == "수로왕비릉") {
							SooroQueen_data();
							final ProgressDialog dialog2 = ProgressDialog.show(
									NMapViewer.this, "",
									"예상소요시간:33분 이용가능버스:128-1번,1번", true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										Thread.sleep(2000);
									} catch (Throwable ex) {
										ex.printStackTrace();
									}
									dialog2.dismiss();
								}
							}).start();
						}
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void Sooro_data() {
		// 소요시간 37분 1번
		mOverlayManager.clearOverlays();
		int markerID = NMapPOIflagType.PIN;

		NMapPOIdata poidata = new NMapPOIdata(2, mMapViewerResourceProvider);
		poidata.beginPOIdata(3);
		poidata.addPOIitem(128.878361, 35.235245, "수로왕릉", markerID, 0);
		poidata.endPOIdata();
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poidata, null);

		NMapPathData pathData = new NMapPathData(9);

		pathData.initPathData();
		pathData.addPathPoint(128.902401, 35.249485,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.903883, 35.246997,
				NMapPathLineStyle.TYPE_SOLID);
		pathData.addPathPoint(128.904080, 35.239308, 0);
		pathData.addPathPoint(128.900132, 35.234488, 0);
		pathData.addPathPoint(128.893603, 35.230632, 0);
		pathData.addPathPoint(128.894063, 35.227527, 0);
		pathData.addPathPoint(128.882854, 35.226453, 0);
		pathData.addPathPoint(128.881279, 35.235350,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.878361, 35.235245, 0);
		pathData.endPathData();

		NMapPathDataOverlay pathDataOverlay = mOverlayManager
				.createPathDataOverlay(pathData);
		
		SharedPreferences pref = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit(); 
		         
		editor.putString("recent_map" , "인제대 ~ 수로왕릉"); 
		editor.putString("consume_time" , "예상소요시간:37분  이용가능버스:1번");
		editor.commit(); 
	}

	public void NamsanPark_data() {
		// 소요시간 24분 128-1번
		mOverlayManager.clearOverlays();
		int markerID = NMapPOIflagType.PIN;

		NMapPOIdata poidata = new NMapPOIdata(2, mMapViewerResourceProvider);
		poidata.beginPOIdata(3);
		poidata.addPOIitem(128.890825, 35.231444, "남산공원", markerID, 0);
		poidata.endPOIdata();
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poidata, null);

		NMapPathData pathData = new NMapPathData(5);
		pathData.initPathData();
		pathData.addPathPoint(128.902401, 35.249485,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.903883, 35.246997,
				NMapPathLineStyle.TYPE_SOLID);
		pathData.addPathPoint(128.904624, 35.235639, 0);
		pathData.addPathPoint(128.891198, 35.234854,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.890825, 35.231444, 0);
		pathData.endPathData();
		NMapPathDataOverlay pathDataOverlay = mOverlayManager
				.createPathDataOverlay(pathData);
		
		SharedPreferences pref = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit(); 
		         
		editor.putString("recent_map" , "인제대 ~ 남산공원"); 
		editor.putString("consume_time" , "예상소요시간:24분 이용가능버스:128-1번");
		editor.commit();
	}

	public void YeonjiPark_data() {
		// 소요시간 36분 128-1번
		mOverlayManager.clearOverlays();
		int markerID = NMapPOIflagType.PIN;

		NMapPOIdata poidata = new NMapPOIdata(2, mMapViewerResourceProvider);
		poidata.beginPOIdata(3);
		poidata.addPOIitem(128.868938, 35.246206, "연지공원", markerID, 0);
		poidata.endPOIdata();
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poidata, null);

		NMapPathData pathData = new NMapPathData(9);
		pathData.initPathData();
		pathData.addPathPoint(128.902401, 35.249485,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.903883, 35.246997,
				NMapPathLineStyle.TYPE_SOLID);
		pathData.addPathPoint(128.904583, 35.235705, 0);
		pathData.addPathPoint(128.889829, 35.234771, 0);
		pathData.addPathPoint(128.884761, 35.240763, 0);
		pathData.addPathPoint(128.883923, 35.240012, 0);
		pathData.addPathPoint(128.884025, 35.238593, 0);
		pathData.addPathPoint(128.873909, 35.247856,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.868938, 35.246206, 0);
		pathData.endPathData();
		NMapPathDataOverlay pathDataOverlay = mOverlayManager
				.createPathDataOverlay(pathData);
		
		SharedPreferences pref = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit(); 
		         
		editor.putString("recent_map" , "인제대 ~ 연지공원"); 
		editor.putString("consume_time" , "예상소요시간:36분 이용가능버스:128-1번");
		editor.commit(); 
	}

	public void PhysicalTrainingPark_data() {
		// 소요시간 55분 1번
		mOverlayManager.clearOverlays();
		int markerID = NMapPOIflagType.PIN;

		NMapPOIdata poidata = new NMapPOIdata(2, mMapViewerResourceProvider);
		poidata.beginPOIdata(3);
		poidata.addPOIitem(128.878079, 35.260302, "김해시민체육공원", markerID, 0);
		poidata.endPOIdata();
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poidata, null);

		NMapPathData pathData = new NMapPathData(12);
		pathData.initPathData();
		pathData.addPathPoint(128.902401, 35.249485,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.903883, 35.246997,
				NMapPathLineStyle.TYPE_SOLID);
		pathData.addPathPoint(128.904078, 35.239324, 0);
		pathData.addPathPoint(128.893595, 35.230578, 0);
		pathData.addPathPoint(128.893901, 35.227573, 0);
		pathData.addPathPoint(128.882764, 35.226455, 0);
		pathData.addPathPoint(128.880863, 35.237705, 0);
		pathData.addPathPoint(128.873424, 35.248571, 0);
		pathData.addPathPoint(128.870236, 35.257265, 0);
		pathData.addPathPoint(128.872954, 35.257999, 0);
		pathData.addPathPoint(128.872055, 35.261771,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.878079, 35.260302, 0);
		pathData.endPathData();
		NMapPathDataOverlay pathDataOverlay = mOverlayManager
				.createPathDataOverlay(pathData);
		
		SharedPreferences pref = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit(); 
		         
		editor.putString("recent_map" , "인제대 ~ 김해시민체육공원"); 
		editor.putString("consume_time" , "예상소요시간:55분 이용가능버스:1번");
		editor.commit(); 
	}

	public void KimhaeBH_data() {
		// 소요시간 33분 128-1번
		mOverlayManager.clearOverlays();
		int markerID = NMapPOIflagType.PIN;

		NMapPOIdata poidata = new NMapPOIdata(2, mMapViewerResourceProvider);
		poidata.beginPOIdata(3);
		poidata.addPOIitem(128.878368, 35.229926, "김해봉황동유적", markerID, 0);
		poidata.endPOIdata();
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poidata, null);

		NMapPathData pathData = new NMapPathData(9);
		pathData.initPathData();
		pathData.addPathPoint(128.902401, 35.249485,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.903883, 35.246997,
				NMapPathLineStyle.TYPE_SOLID);
		pathData.addPathPoint(128.904116, 35.239125, 0);
		pathData.addPathPoint(128.899947, 35.234418, 0);
		pathData.addPathPoint(128.893652, 35.230679, 0);
		pathData.addPathPoint(128.894020, 35.227807, 0);
		pathData.addPathPoint(128.882903, 35.226772, 0);
		pathData.addPathPoint(128.882167, 35.230178, 
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.878368, 35.229926, 0);
		pathData.endPathData();
		NMapPathDataOverlay pathDataOverlay = mOverlayManager
				.createPathDataOverlay(pathData);
		
		SharedPreferences pref = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit(); 
		         
		editor.putString("recent_map" , "인제대 ~ 김해봉황동유적"); 
		editor.putString("consume_time" , "예상소요시간:39분 이용가능버스:1번,1-1번");
		editor.commit(); 
	}

	public void KimhaeMuseum_data() {
		// 소요시간 33분 128-1번
		mOverlayManager.clearOverlays();
		int markerID = NMapPOIflagType.PIN;

		NMapPOIdata poidata = new NMapPOIdata(2, mMapViewerResourceProvider);
		poidata.beginPOIdata(3);
		poidata.addPOIitem(128.873483, 35.242891, "국립김해박물관", markerID, 0);
		poidata.endPOIdata();
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poidata, null);

		NMapPathData pathData = new NMapPathData(11);
		pathData.initPathData();
		pathData.addPathPoint(128.902401, 35.249485,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.903883, 35.246997,
				NMapPathLineStyle.TYPE_SOLID);
		pathData.addPathPoint(128.904566, 35.235714, 0);
		pathData.addPathPoint(128.889791, 35.234746, 0);
		pathData.addPathPoint(128.884845, 35.240755, 0);
		pathData.addPathPoint(128.883803, 35.240037, 0);
		pathData.addPathPoint(128.884028, 35.238669, 0);
		pathData.addPathPoint(128.880104, 35.238802, 0);
		pathData.addPathPoint(128.880125, 35.238869, 0);
		pathData.addPathPoint(128.875179, 35.245962,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.873483, 35.242891, 0);
		pathData.endPathData();
		NMapPathDataOverlay pathDataOverlay = mOverlayManager
				.createPathDataOverlay(pathData);
		
		SharedPreferences pref = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit(); 
		         
		editor.putString("recent_map" , "인제대 ~ 국립김해박물관"); 
		editor.putString("consume_time" , "예상소요시간:33분 이용가능버스:128-1번");
		editor.commit(); 
	}

	public void SooroQueen_data() {
		// 소요시간 33분 128-1번
		mOverlayManager.clearOverlays();
		int markerID = NMapPOIflagType.PIN;

		NMapPOIdata poidata = new NMapPOIdata(2, mMapViewerResourceProvider);
		poidata.beginPOIdata(3);
		poidata.addPOIitem(128.877661, 35.243779, "수로왕비릉", markerID, 0);
		poidata.endPOIdata();
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poidata, null);

		NMapPathData pathData = new NMapPathData(12);
		pathData.initPathData();
		pathData.addPathPoint(128.902401, 35.249485,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.903883, 35.246997,
				NMapPathLineStyle.TYPE_SOLID);
		pathData.addPathPoint(128.904629, 35.235629, 0);
		pathData.addPathPoint(128.889854, 35.234786, 0);
		pathData.addPathPoint(128.888893, 35.237090, 0);
		pathData.addPathPoint(128.884837, 35.240753, 0);
		pathData.addPathPoint(128.883886, 35.240061, 0);
		pathData.addPathPoint(128.884009, 35.238617, 0);
		pathData.addPathPoint(128.880154, 35.238839, 0);
		pathData.addPathPoint(128.878417, 35.241334,
				NMapPathLineStyle.TYPE_DASH);
		pathData.addPathPoint(128.877661, 35.243779, 0);
		pathData.endPathData();
		NMapPathDataOverlay pathDataOverlay = mOverlayManager
				.createPathDataOverlay(pathData);
		
		SharedPreferences pref = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit(); 
		         
		editor.putString("recent_map" , "인제대 ~ 수로왕비릉"); 
		editor.putString("consume_time" , "예상소요시간:33분 이용가능버스:128-1번,1번");
		editor.commit();
	}
}