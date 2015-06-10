package com.zfj123.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode.Mode;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		sp=context.getSharedPreferences("config", context.MODE_PRIVATE);
		boolean safeState = sp.getBoolean("safeState", false);
		System.out.println("safeState的值"+safeState);
		tm=(TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		//读取之前保存的sim信息
		String saveSim = sp.getString("sim", "");
		//读取当前的sim卡信息
		String realSim = tm.getSimSerialNumber();
		//比较是否一样
		if(realSim.equals(saveSim)){
			//sim没有变更，还是同一个sim
		}else {
			//sim已经变更，发一个短信给安全号码
			if (safeState) {
				System.out.println("sim已经变更");
				// Toast.makeText(context, "sim", 1).show();
				String safeNumber = sp.getString("safenumber", "");
				SmsManager smsManager = SmsManager.getDefault();
				System.out.println("safeNumber的值"+safeNumber);
				smsManager.sendTextMessage(safeNumber	, null, "sim has changed", null, null);
			}
		}
	}

}
