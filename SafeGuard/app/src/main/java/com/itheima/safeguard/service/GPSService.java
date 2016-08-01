package com.itheima.safeguard.service;

import java.io.IOException;
import java.io.InputStream;

import com.itheima.safeguard.data.C;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class GPSService extends Service {

	private LocationManager manager;
	private MyLocationListener listener;
	private InputStream databaseStream;
	private ModifyOffset offset;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = this.manager.getBestProvider(criteria, true);
		this.listener = new MyLocationListener();
		this.manager.requestLocationUpdates(provider, 0, 0, this.listener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.manager.removeUpdates(this.listener);
		if(databaseStream != null) {
			try {
				databaseStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if(databaseStream == null) {
				try {
					databaseStream = GPSService.this.getAssets().open("axisoffset.dat");
				} catch (IOException e) {
					Toast.makeText(GPSService.this, "请将数据库文件axisoffset.dat放到项目的assets目录下", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
			if(databaseStream == null) {
				return;
			}
			if(offset == null) {
				 try {
					offset = ModifyOffset.getInstance(databaseStream);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(offset == null) {
				return ;
			}
			PointDouble point = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
			String accuracy = String.valueOf(location.getAccuracy());
			SharedPreferences prefs = GPSService.this.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
			prefs.edit().putString(C.prefs.LAST_LOCATION, "longitude:" + point.x + ",latitude:" + point.y + ",accuracy:" + accuracy).commit();
		}
		

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}

}
