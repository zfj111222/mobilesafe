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
		//接收短信的代码
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objects) {
			//具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			//发送者
			String sender = sms.getOriginatingAddress();
			String safeNumber = sp.getString("safenumber", "");
			Log.i(TAG, "sneder="+sender+"--safenumber="+safeNumber);
			if(sender.contains(safeNumber)){
				String body = sms.getMessageBody();
				if("#*location*#".equals(body)){
					//得到手机的gps
					Log.i(TAG, "得到手机的gps");
					//启动gps服务
					Intent i=new Intent(context,GPSServive.class);
					context.startService(i);
					SharedPreferences sp=context.getSharedPreferences("config", context.MODE_PRIVATE);
					String lastLocation = sp.getString("lastlocation", null);
					if (TextUtils.isEmpty(lastLocation)) {
						//位置没有得到
						SmsManager.getDefault().sendTextMessage(sender, null, "getting loaction", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastLocation, null, null);
					}
					//把这个广播终止掉
					abortBroadcast();
				}
				else if("#*alarm*#".equals(body)){
					//播放报警音乐
					Log.i(TAG, "播放报警音乐");
					MediaPlayer mediaPlayer =MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setVolume(1.0f, 1.0f);
					mediaPlayer.setLooping(false);
					mediaPlayer.start();
					abortBroadcast();
				}
				else if("#*wipedata*#".equals(body)){
					//远程清除数据
					Log.i(TAG, "远程清除数据");
					abortBroadcast();
				}
				else if("#*lockscreen*#".equals(body)){
					//远程锁屏
					Log.i(TAG, "远程锁屏");
					abortBroadcast();
				}
			}
		}
	}

}
