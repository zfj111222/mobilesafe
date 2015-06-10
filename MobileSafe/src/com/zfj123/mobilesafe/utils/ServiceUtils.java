package com.zfj123.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {

	/**
	 * У��ĳ�������Ƿ񻹻���
	 * serviceName : ���������������
	 */
	public static boolean isServiceRunnung(Context context,String serviceName){
		//У������Ƿ񻹻���
		ActivityManager am=(ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//��ʾ������������
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for (RunningServiceInfo info : infos) {
			String name= info.service.getClassName();
			System.out.println(name);
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	}
}
