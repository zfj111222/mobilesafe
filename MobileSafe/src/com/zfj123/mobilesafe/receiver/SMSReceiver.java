package com.zfj123.mobilesafe.receiver;

import java.io.IOException;

import com.zfj123.mobilesafe.R;
import com.zfj123.mobilesafe.service.GPSServive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp=context.getSharedPreferences("config", context.MODE_PRIVATE);
		//���ն��ŵĴ���
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objects) {
			//�����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			//������
			String sender = sms.getOriginatingAddress();
			String safeNumber = sp.getString("safenumber", "");
			Log.i(TAG, "sneder="+sender+"--safenumber="+safeNumber);
			if(sender.contains(safeNumber)){
				String body = sms.getMessageBody();
				if("#*location*#".equals(body)){
					//�õ��ֻ���gps
					Log.i(TAG, "�õ��ֻ���gps");
					//����gps����
					Intent i=new Intent(context,GPSServive.class);
					context.startService(i);
					SharedPreferences sp=context.getSharedPreferences("config", context.MODE_PRIVATE);
					String lastLocation = sp.getString("lastlocation", null);
					if (TextUtils.isEmpty(lastLocation)) {
						//λ��û�еõ�
						SmsManager.getDefault().sendTextMessage(sender, null, "getting loaction", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastLocation, null, null);
					}
					//������㲥��ֹ��
					abortBroadcast();
				}
				else if("#*alarm*#".equals(body)){
					//���ű�������
					Log.i(TAG, "���ű�������");
					MediaPlayer mediaPlayer =MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setVolume(1.0f, 1.0f);
					mediaPlayer.setLooping(false);
					mediaPlayer.start();
					abortBroadcast();
				}
				else if("#*wipedata*#".equals(body)){
					//Զ���������
					Log.i(TAG, "Զ���������");
					abortBroadcast();
				}
				else if("#*lockscreen*#".equals(body)){
					//Զ������
					Log.i(TAG, "Զ������");
					abortBroadcast();
				}
			}
		}
	}

}
