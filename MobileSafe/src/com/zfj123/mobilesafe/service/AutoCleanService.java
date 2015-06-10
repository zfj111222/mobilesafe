package com.zfj123.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoCleanService extends Service {

	public static final String TAG = "AutoCleanService";
	private screenOffReceiver receiver;
	private ActivityManager am;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		am=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver=new screenOffReceiver();
		IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver=null;
	}
	
	private class screenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "∆¡ƒªÀ¯∆¡¡À");
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : infos) {
				am.killBackgroundProcesses(info.processName);
			}
		}
	}
}
