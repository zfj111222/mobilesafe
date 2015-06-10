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
		System.out.println("safeState��ֵ"+safeState);
		tm=(TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		//��ȡ֮ǰ�����sim��Ϣ
		String saveSim = sp.getString("sim", "");
		//��ȡ��ǰ��sim����Ϣ
		String realSim = tm.getSimSerialNumber();
		//�Ƚ��Ƿ�һ��
		if(realSim.equals(saveSim)){
			//simû�б��������ͬһ��sim
		}else {
			//sim�Ѿ��������һ�����Ÿ���ȫ����
			if (safeState) {
				System.out.println("sim�Ѿ����");
				// Toast.makeText(context, "sim", 1).show();
				String safeNumber = sp.getString("safenumber", "");
				SmsManager smsManager = SmsManager.getDefault();
				System.out.println("safeNumber��ֵ"+safeNumber);
				smsManager.sendTextMessage(safeNumber	, null, "sim has changed", null, null);
			}
		}
	}

}
