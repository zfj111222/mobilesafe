package com.zfj123.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {

	/**
	 * 校验某个服务是否还活着
	 * serviceName : 传进来服务的名称
	 */
	public static boolean isServiceRunnung(Context context,String serviceName){
		//校验服务是否还活着
		ActivityManager am=(ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//显示最多多少条服务
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
