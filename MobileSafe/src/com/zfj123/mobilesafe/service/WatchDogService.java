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
 *看门狗代码 监视系统程序的运行状态
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
		//服务没有任务栈信息，在服务开启activity，要指定activity的任务栈
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(getApplicationContext(), EnterPwdActivity.class);
		flag=true;
		new Thread(){
			public void run() {
				while(flag){
					List<RunningTaskInfo> infos=am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
//					System.out.println("栈顶包名"+packname);
					//if(dao.find(packname)){//查询数据库太慢了，消耗资源，改成查询内存
						//判断这个应用程序是否需要临时停止保护
					if(protectPackname.contains(packname)){//查询内存效率高很多
						if(packname.equals(tempStopProtectPackname)){
							
						}else{
//							System.out.println("开启密码保护");
							//设置要保护程序的包名
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
			System.out.println("接收到临时广播事件");
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
	}
	
	private class screenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "屏幕锁屏了");
			tempStopProtectPackname=null;
		}
	}
	
	private class DataChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收到了数据库已经改变的广播");
			protectPackname=dao.findAll();
		}
		
	}
}
