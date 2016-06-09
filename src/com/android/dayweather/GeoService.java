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
		// GPS ��ġ������ �����´� �Ʒ��� �Ķ��� ��Ȳ�� �����ؼ� ����� ���� �Ѵ� �ȴ�.
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// ���ι��̴��� PASSIVE, NATWORK, GPS �� �ִ�.
		provider = LocationManager.NETWORK_PROVIDER;
		location = lm.getLastKnownLocation(provider);

		// ���ι��̴� �߿� ������ ���ǿ� ������ ���� �����´�.
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); // ��Ȯ��
		criteria.setPowerRequirement(Criteria.POWER_LOW); // ���� �Ҹ���
		criteria.setAltitudeRequired(false); // ��
		criteria.setBearingRequired(false); // ..
		criteria.setSpeedRequired(false); // �ӵ�
		criteria.setCostAllowed(true); // ������ ���

		// �ι�° �Ķ���͸� true �� �ؾ� Ȱ��ȭ�� ���ι��̴��� �����´�.
		bestProvider = lm.getBestProvider(criteria, true);
		location = lm.getLastKnownLocation(bestProvider);

		if (location != null) {getGoogleAddress(location.getLatitude(), location.getLongitude());}
		// �̵��� ���� ��ġ������Ʈ�� ���� ������ ����
		LocationListener = new DispLocListener();
		// 30�� ����, 10.0f �Ÿ� �̵� �� ��ġ�� ������Ʈ �Ѵ�.
		lm.requestLocationUpdates(bestProvider, 3600000L, 1000.0f,LocationListener);
	}

	@Override
	public void onDestroy() {Toast.makeText(this, "GeoService Destroy", Toast.LENGTH_LONG).show();}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {return super.onStartCommand(intent, flags, startId);}

	// ���� ��ġ ������Ʈ�� ���� ������
	public class DispLocListener implements LocationListener {
		public void onLocationChanged(Location location) {getGoogleAddress(location.getLatitude(), location.getLongitude());}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}

	//����,�浵�� �ּ����
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
				// ����° �μ��� �ִ������ε� �ϳ��� ���Ϲ޵��� �����ߴ�
				ltAddress = geocoder.getFromLocation(latitude, longitude, 1);
				if (ltAddress != null && ltAddress.size() > 0) {
					// �ּ�
					currentLocationAddress = ltAddress.get(0).getAddressLine(0).toString();
					// ������ �ּ� ������ (����/�浵 ���� ����)
					sbInfo.append(currentLocationAddress).append(">");	sbInfo.append(latitude).append(">");sbInfo.append(longitude);
					GeoInfoTemp = new Geo_DbInfoClass(ltAddress.get(0).getAdminArea().toString(),
							ltAddress.get(0).getLocality().toString(), ltAddress.get(0).getThoroughfare().toString());
					g_helper.addDbInfoClass(GeoInfoTemp);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}
}
