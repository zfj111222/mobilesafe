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
	
	//λ�÷���
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
//			System.out.println("λ�÷���"+location);
//		}
		//��λ���ṩ����������
		Criteria criteria=new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//����Ϊ��󾫶� 
//		criteria.setAltitudeRequired(false);//��Ҫ�󺣰���Ϣ 
//		criteria.setBearingRequired(false);//��Ҫ��λ��Ϣ 
//		criteria.setCostAllowed(true);//�Ƿ������� 
//		criteria.setPowerRequirement(Criteria.POWER_LOW);//�Ե�����Ҫ�� 

		String proveder=lm.getBestProvider(criteria, true);
		listener=new MyLocationListener();
		//ע���������
		lm.requestLocationUpdates(proveder, 1, 1,listener );
	}
	
	class MyLocationListener implements LocationListener{

		/**
		 * ��λ�øı��ʱ��ص�
		 */
		@Override
		public void onLocationChanged(Location location) {
			String longitude="j"+location.getLongitude()+"\n";
			String latitude="w"+location.getLatitude()+"\n";
			String accuracy="a"+location.getAccuracy()+"\n";
			System.out.println(longitude+"--"+latitude+"--"+accuracy);
			//�ѱ�׼��gps����ת���ɻ�������
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
			//�����Ÿ���ȫ����
			SharedPreferences sp=getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastloction", longitude+latitude+accuracy);
			editor.commit();
		}

		/**
		 * ��״̬�����ı��ʱ��ص�
		 * �� gps������ɹر�
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		/**
		 * ĳһ��λ���ṩ����ʹ��ʱ�ص�
		 */
		@Override
		public void onProviderEnabled(String provider) {
			
		}

		/**
		 * ĳһ��λ���ṩ�߲�����ʹ��ʱ�ص�
		 */
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ȡ������λ�÷���
		lm.removeUpdates(listener);
		listener=null;
	}

}
