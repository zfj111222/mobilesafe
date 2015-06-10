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
			case TelephonyManager.CALL_STATE_RINGING://����״̬
				String result = dao.findMode(incomingNumber);
				if("1".equals(result)||"3".equals(result)){
					Log.i(TAG, "�Ҷϵ绰");
					//ɾ�����м�¼
					//����һ��Ӧ�ó�����ϵ�˵�Ӧ�õ�˽�����ݿ�
//					deleteCallLog(incomingNumber);
					//�۲���м�¼���ݿ����ݵı仯
					Uri uri=Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(
							uri, true, new CallLogObserver(incomingNumber,new Handler()));
					//1.5�汾֮��û��tm.endCall();
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
			Log.i(TAG, "���ݿ�����ݱ仯�ˣ������˺��м�¼");
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}
	}
	
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "�ڲ��㲥�����ߣ����ŵ�����.");
			//��鷢�����Ƿ��Ǻ��������룬�����˶������ػ���ȫ������
			Object[] objs=(Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage=SmsMessage.createFromPdu((byte[])obj);
				//�õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result)||"3".equals(result)){
					Log.i(TAG, "���ض���");
					abortBroadcast();
				}
				String body = smsMessage.getMessageBody();
				if(body.contains("zfj")){
					Log.i(TAG, "���ƹ��");
					abortBroadcast();
				}
			}
		}
		
	}

	public void endCall() {
//		Builder builder=ServiceManager.getService(TELEPHONY_SERVICE)��
		try {
			//����servicemanager�ֽ���
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			Toast.makeText(CallSmsSafeService.this, "�Ҷϵ绰", 1).show();
			ITelephony.Stub.asInterface(iBinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���������ṩ��ɾ�����м�¼
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		//���м�¼uri��·��
		Uri uri=Uri.parse("content://call_log/calls");
//		CallLog.CONTENT_URI
		Log.i(TAG, "���м�¼ɾ��");
		resolver.delete(uri, "number=?", new String[]{incomingNumber});
	}

}
