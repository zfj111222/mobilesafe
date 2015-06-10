package com.zfj123.mobilesafe.service;

import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class GPSServive extends Service {
	
	//位置服务
	private LocationManager lm;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lm=(LocationManager) getSystemService(LOCATION_SERVICE);
//		List<String> providers = lm.getAllProviders();
//		for (String location : providers) {
//			System.out.println("位置服务："+location);
//		}
		//给位置提供者设置条件
		Criteria criteria=new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度 
//		criteria.setAltitudeRequired(false);//不要求海拔信息 
//		criteria.setBearingRequired(false);//不要求方位信息 
//		criteria.setCostAllowed(true);//是否允许付费 
//		criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求 

		String proveder=lm.getBestProvider(criteria, true);
		listener=new MyLocationListener();
		//注册监听服务
		lm.requestLocationUpdates(proveder, 1, 1,listener );
	}
	
	class MyLocationListener implements LocationListener{

		/**
		 * 当位置改变的时候回调
		 */
		@Override
		public void onLocationChanged(Location location) {
			String longitude="j"+location.getLongitude()+"\n";
			String latitude="w"+location.getLatitude()+"\n";
			String accuracy="a"+location.getAccuracy()+"\n";
			System.out.println(longitude+"--"+latitude+"--"+accuracy);
			//把标准得gps坐标转换成火星坐标
//			InputStream is;
//			try {
//				is = getAssets().open("axisoffset.dat");
//				ModifyOffset offset = ModifyOffset.getInstance(is);
//				PointDouble point = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
//				longitude="j"+point.x+"\n";
//				latitude="j"+point.y+"\n";
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			//发短信给安全号码
			SharedPreferences sp=getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastloction", longitude+latitude+accuracy);
			editor.commit();
		}

		/**
		 * 当状态发生改变的时候回调
		 * 如 gps开启变成关闭
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		/**
		 * 某一个位置提供者能使用时回调
		 */
		@Override
		public void onProviderEnabled(String provider) {
			
		}

		/**
		 * 某一个位置提供者不可以使用时回调
		 */
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消监听位置服务
		lm.removeUpdates(listener);
		listener=null;
	}

}
