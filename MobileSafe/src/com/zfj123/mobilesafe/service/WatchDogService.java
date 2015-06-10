package com.zfj123.mobilesafe.service;

import java.io.File;
import java.util.List;

import com.zfj123.mobilesafe.EnterPwdActivity;
import com.zfj123.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 *���Ź����� ����ϵͳ���������״̬
 */
public class WatchDogService extends Service {
	
	public static final String TAG = "WatchDogService";
	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	private InnerReceiver receiver;
	private String tempStopProtectPackname;
	private screenOffReceiver offReceiver;
	
	private List<String> protectPackname;
	private Intent intent;
	
	private DataChangeReceiver dataChangeReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		am=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver=new InnerReceiver();
		IntentFilter filter=new IntentFilter("com.zfj123.mobilesafe.stopdog");
		registerReceiver(receiver, filter);
		offReceiver=new screenOffReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		dataChangeReceiver=new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("com.zfj123.mobilesafe.applockchange"));
		dao=new AppLockDao(this);
		protectPackname=dao.findAll();
		intent = new Intent();
		//����û������ջ��Ϣ���ڷ�����activity��Ҫָ��activity������ջ
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(getApplicationContext(), EnterPwdActivity.class);
		flag=true;
		new Thread(){
			public void run() {
				while(flag){
					List<RunningTaskInfo> infos=am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
//					System.out.println("ջ������"+packname);
					//if(dao.find(packname)){//��ѯ���ݿ�̫���ˣ�������Դ���ĳɲ�ѯ�ڴ�
						//�ж����Ӧ�ó����Ƿ���Ҫ��ʱֹͣ����
					if(protectPackname.contains(packname)){//��ѯ�ڴ�Ч�ʸߺܶ�
						if(packname.equals(tempStopProtectPackname)){
							
						}else{
//							System.out.println("�������뱣��");
							//����Ҫ��������İ���
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
					}
					SystemClock.sleep(500);
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		flag=false;
		unregisterReceiver(receiver);
		receiver=null;
		unregisterReceiver(offReceiver);
		offReceiver=null;
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver=null;
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���յ���ʱ�㲥�¼�");
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
	}
	
	private class screenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "��Ļ������");
			tempStopProtectPackname=null;
		}
	}
	
	private class DataChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���յ������ݿ��Ѿ��ı�Ĺ㲥");
			protectPackname=dao.findAll();
		}
		
	}
}
