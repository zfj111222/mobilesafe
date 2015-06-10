package com.zfj123.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.zfj123.mobilesafe.bean.AppInfo;

/**
 *业务方法类，提供手机里面安装的所有的应用程序信息
 */
public class AppInfoProvider {

	/**
	 * 获取所有的安装应用程序信息
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		List<AppInfo> appInfos=new ArrayList<AppInfo>();
		PackageManager pm=context.getPackageManager();
		//所有的安装在系统上的应用程序包信息
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		for (PackageInfo packInfo : packInfos) {
			AppInfo appInfo=new AppInfo();
			//packInfo 相当于TV一个应用程序apk包的清单文件
			String packName=packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			String  name = packInfo.applicationInfo.loadLabel(pm).toString();
			//应用程序信息的标记
			int flags=packInfo.applicationInfo.flags;
			if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
				//用户程序
				appInfo.setUserAPP(true);
			}else{
				//系统程序
				appInfo.setUserAPP(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//手机内存
				appInfo.setInRom(true);
			}else{
				//手机外存储
				appInfo.setInRom(false);
			}
			appInfo.setPackName(packName);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
