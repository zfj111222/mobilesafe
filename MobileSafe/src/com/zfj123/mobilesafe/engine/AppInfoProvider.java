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
 *ҵ�񷽷��࣬�ṩ�ֻ����氲װ�����е�Ӧ�ó�����Ϣ
 */
public class AppInfoProvider {

	/**
	 * ��ȡ���еİ�װӦ�ó�����Ϣ
	 * @param context ������
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		List<AppInfo> appInfos=new ArrayList<AppInfo>();
		PackageManager pm=context.getPackageManager();
		//���еİ�װ��ϵͳ�ϵ�Ӧ�ó������Ϣ
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		for (PackageInfo packInfo : packInfos) {
			AppInfo appInfo=new AppInfo();
			//packInfo �൱��TVһ��Ӧ�ó���apk�����嵥�ļ�
			String packName=packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			String  name = packInfo.applicationInfo.loadLabel(pm).toString();
			//Ӧ�ó�����Ϣ�ı��
			int flags=packInfo.applicationInfo.flags;
			if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
				//�û�����
				appInfo.setUserAPP(true);
			}else{
				//ϵͳ����
				appInfo.setUserAPP(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//�ֻ��ڴ�
				appInfo.setInRom(true);
			}else{
				//�ֻ���洢
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
