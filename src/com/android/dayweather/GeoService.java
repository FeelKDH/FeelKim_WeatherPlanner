package com.android.dayweather;

import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class GeoService extends Service {
	LocationListener LocationListener;
	LocationManager lm;
	Location location;
	String bestProvider;
	String provider;
	String result;
	Context context;
	String tag = "ReverseGeocode";
	private String currentLocationAddress;
	Geo_DBHelper g_helper;

	@Override
	public IBinder onBind(Intent intent) {	// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// GPS 위치정보를 가져온다 아래의 파랑과 주황중 택일해서 사용해 보자 둘다 된다.
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// 프로바이더는 PASSIVE, NATWORK, GPS 가 있다.
		provider = LocationManager.NETWORK_PROVIDER;
		location = lm.getLastKnownLocation(provider);

		// 프로바이더 중에 설정한 조건에 적합한 것을 가져온다.
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 정확도
		criteria.setPowerRequirement(Criteria.POWER_LOW); // 전원 소리량
		criteria.setAltitudeRequired(false); // 고도
		criteria.setBearingRequired(false); // ..
		criteria.setSpeedRequired(false); // 속도
		criteria.setCostAllowed(true); // 금전적 비용

		// 두번째 파라미터를 true 로 해야 활성화된 프로바이더를 가져온다.
		bestProvider = lm.getBestProvider(criteria, true);
		location = lm.getLastKnownLocation(bestProvider);

		if (location != null) {getGoogleAddress(location.getLatitude(), location.getLongitude());}
		// 이동시 현재 위치업데이트를 위한 리스너 구현
		LocationListener = new DispLocListener();
		// 30초 단위, 10.0f 거리 이동 시 위치를 업데이트 한다.
		lm.requestLocationUpdates(bestProvider, 3600000L, 1000.0f,LocationListener);
	}

	@Override
	public void onDestroy() {Toast.makeText(this, "GeoService Destroy", Toast.LENGTH_LONG).show();}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {return super.onStartCommand(intent, flags, startId);}

	// 현재 위치 업데이트를 위한 리스너
	public class DispLocListener implements LocationListener {
		public void onLocationChanged(Location location) {getGoogleAddress(location.getLatitude(), location.getLongitude());}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}

	//위도,경도로 주소취득
	private void getGoogleAddress(double latitude, double longitude) {
		g_helper = new Geo_DBHelper(this);
		g_helper.deleteAllDbInfoClass();

		StringBuffer sbInfo = new StringBuffer();
		Geocoder geocoder = new Geocoder(this, Locale.KOREA);
		List<Address> ltAddress;

		g_helper.deleteAllDbInfoClass();
		Geo_DbInfoClass GeoInfoTemp = new Geo_DbInfoClass("0", "0", "0");
		if (g_helper.getDbInfoClasssCount() < 1) {g_helper.addDbInfoClass(GeoInfoTemp);}

		try {
			if (geocoder != null) {
				// 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
				ltAddress = geocoder.getFromLocation(latitude, longitude, 1);
				if (ltAddress != null && ltAddress.size() > 0) {
					// 주소
					currentLocationAddress = ltAddress.get(0).getAddressLine(0).toString();
					// 전송할 주소 데이터 (위도/경도 포함 편집)
					sbInfo.append(currentLocationAddress).append(">");	sbInfo.append(latitude).append(">");sbInfo.append(longitude);
					GeoInfoTemp = new Geo_DbInfoClass(ltAddress.get(0).getAdminArea().toString(),
							ltAddress.get(0).getLocality().toString(), ltAddress.get(0).getThoroughfare().toString());
					g_helper.addDbInfoClass(GeoInfoTemp);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}
}
