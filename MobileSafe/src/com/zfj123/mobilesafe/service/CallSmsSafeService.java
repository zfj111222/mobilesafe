package com.zfj123.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.zfj123.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallSmsSafeService extends Service {
	
	public static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager  tm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener=new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		dao=new BlackNumberDao(CallSmsSafeService.this);
		receiver=new InnerSmsReceiver();
		IntentFilter filter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver=null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener=null;
	}
	
	private class MyListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://响铃状态
				String result = dao.findMode(incomingNumber);
				if("1".equals(result)||"3".equals(result)){
					Log.i(TAG, "挂断电话");
					//删除呼叫记录
					//另外一个应用程序联系人的应用的私有数据库
//					deleteCallLog(incomingNumber);
					//观察呼叫记录数据库内容的变化
					Uri uri=Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(
							uri, true, new CallLogObserver(incomingNumber,new Handler()));
					//1.5版本之后没有tm.endCall();
					endCall();
				}
				break;

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	
	private class CallLogObserver extends ContentObserver{

		private String incomingNumber;
		
		public CallLogObserver(String incomingNumber, Handler handler) {
			super(handler);
			this.incomingNumber=incomingNumber;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			Log.i(TAG, "数据库的内容变化了，产生了呼叫记录");
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}
	}
	
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "内部广播接收者，短信到来了.");
			//检查发件人是否是黑名单号码，设置了短信拦截或者全部拦截
			Object[] objs=(Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage=SmsMessage.createFromPdu((byte[])obj);
				//得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result)||"3".equals(result)){
					Log.i(TAG, "拦截短信");
					abortBroadcast();
				}
				String body = smsMessage.getMessageBody();
				if(body.contains("zfj")){
					Log.i(TAG, "疑似广告");
					abortBroadcast();
				}
			}
		}
		
	}

	public void endCall() {
//		Builder builder=ServiceManager.getService(TELEPHONY_SERVICE)；
		try {
			//加载servicemanager字节码
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			Toast.makeText(CallSmsSafeService.this, "挂断电话", 1).show();
			ITelephony.Stub.asInterface(iBinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 利用内容提供者删除呼叫记录
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		//呼叫记录uri的路径
		Uri uri=Uri.parse("content://call_log/calls");
//		CallLog.CONTENT_URI
		Log.i(TAG, "呼叫记录删除");
		resolver.delete(uri, "number=?", new String[]{incomingNumber});
	}

}
