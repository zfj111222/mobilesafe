package com.zfj123.mobilesafe.service;

import com.zfj123.mobilesafe.R;
import com.zfj123.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {
	
	protected static final String TAG = "AddressService";

	/**
	 * ��������
	 */
	private TelephonyManager tm;
	
	private OutCallReceiver receiver;
	
	/**
	 * ���������
	 */
	private WindowManager wm;
	
	SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	private MyPHoneStateListener listener;

	/**
	 * �Զ���toastview
	 */
	private View view;

	private WindowManager.LayoutParams params;

//	/**
//	 * �Զ���toastview
//	 */
//	private TextView view;
	
	@Override
	public void onCreate() {
		super.onCreate();
//		view = new TextView(getApplicationContext());
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//��������
		listener=new MyPHoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//�ô���ע��㲥������
		receiver=new OutCallReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		//ʵ�������������
		wm=(WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//ȡ����������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener=null;
		
		//�ô���ȡ��ע��㲥������
		unregisterReceiver(receiver);
		receiver=null;
	}
	
	private class MyPHoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			//��һ��������״̬���ڶ��������Ǵ�����ĵ绰����
			switch (state) {
			//���������ʱ��Ҳ�������ʱ��
			case TelephonyManager.CALL_STATE_RINGING:
				//���ݵõ��ĵ绰�����ѯ���Ĺ����أ�������˾����ʾ
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
				Toast.makeText(getApplicationContext(), address, 1).show();
				mToast(address);
				break;
				
			case TelephonyManager.CALL_STATE_IDLE://�绰�Ŀ���״̬;�ҵ绰������ܾ�
				//���Զ���Toastview�Ƴ�
				if(view!=null){
					wm.removeView(view);
				}
				break;

			default:
				break;
			}
		}
	}
	
	//���������ڲ���
	//�㲥�����ߵ��������ںͷ���һ��
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//����������õ��Ĳ���ȥ�绰����
			String phone = getResultData();
			//��ѯ���ݿ�
			String address = NumberAddressQueryUtils.queryNumber(phone);
//			Toast.makeText(context, address, 1).show();
			mToast(address);
		}

	}
	
	//������ָ��ʼ��λ��
	int startX = 0 ;
	int startY = 0 ;
	
	long[] mHits = new long[2];

	/**
	 * �Զ�����˾
	 * @param address
	 */
	public void mToast(String address) {
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//{"��͸��","������","��ʿ��","������","ƻ����"};
		int[] ids={R.drawable.call_locate_white,R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,R.drawable.call_locate_gray,
				R.drawable.call_locate_green};
		int count = sp.getInt("which", 0);
		view = View.inflate(this, R.layout.address_show, null);
		TextView tv_address=(TextView) view.findViewById(R.id.tv_address);
		tv_address.setText(address);
		view.setBackgroundResource(ids[count]);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
			      mHits[mHits.length-1] = SystemClock.uptimeMillis();
			      if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
			        //˫������
			    	  params.x=wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
			    	  params.y=wm.getDefaultDisplay().getHeight()/2-view.getHeight()/2;
			    	  Editor editor = sp.edit();
			    	  editor.putInt("lastX",params.x);
			    	  editor.putInt("lastY", params.y);
			    	  editor.commit();
			    	  wm.updateViewLayout(view, params);
			      }
			}
		});
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE://��ָ����Ļ���ƶ�
					int newX=(int) event.getRawX();
					int newY=(int) event.getRawY();
					Log.i(TAG, "�µ�λ�ã�X="+newX+"--Y="+newY);
					int dx=newX-startX;
					int dy=newY-startY;
					Log.i(TAG, "��ָ��ƫ������X="+dx+"--Y="+dy);
					//����view�ڴ����λ��
//					Log.i(TAG, "����view�ڴ����λ�ã�X="+dx+"--Y="+dy);
					params.x=params.x+dx;
					params.y=params.y+dy;
					if(params.x<0){
						params.x=0;
					}
					if(params.y<0){
						params.y=0;
					}
					if(params.x>wm.getDefaultDisplay().getWidth()-view.getWidth()){
						params.x=wm.getDefaultDisplay().getWidth()-view.getWidth();
					}
					if(params.y>wm.getDefaultDisplay().getHeight()-view.getHeight()){
						params.y=wm.getDefaultDisplay().getHeight()-view.getHeight();
					}
					wm.updateViewLayout(view, params);
					//���³�ʼ����ʼ��λ��
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_DOWN://��ָ������Ļ
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					Log.i(TAG, "��ʼλ�ã�X="+startX+"--Y="+startY);
					break;
					
				case MotionEvent.ACTION_UP://��ָ�뿪��Ļ˲��
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;

				default:
					break;
				}
				return false;//�¼��������,��Ҫ�ø��ؼ����߸�������Ӧ�����¼�
			}
		});
		
		params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //�봰�����ϽǶ���
        params.gravity=Gravity.TOP+Gravity.LEFT;
        //ָ������������100���أ��ϱ�100����
//        params.x=100;
//        params.y=100;
        int lastX = sp.getInt("lastX", 100);
        int lastY = sp.getInt("lastY", 100);
        params.x=lastX;
        params.y=lastY;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //androidϵͳ����е绰���ȼ���һ�ִ�������
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
	}

}
