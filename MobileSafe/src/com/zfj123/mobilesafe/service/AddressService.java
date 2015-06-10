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
	 * 监听来电
	 */
	private TelephonyManager tm;
	
	private OutCallReceiver receiver;
	
	/**
	 * 窗体管理者
	 */
	private WindowManager wm;
	
	SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	private MyPHoneStateListener listener;

	/**
	 * 自定义toastview
	 */
	private View view;

	private WindowManager.LayoutParams params;

//	/**
//	 * 自定义toastview
//	 */
//	private TextView view;
	
	@Override
	public void onCreate() {
		super.onCreate();
//		view = new TextView(getApplicationContext());
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//监听来电
		listener=new MyPHoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//用代码注册广播接收者
		receiver=new OutCallReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		//实例化窗体管理者
		wm=(WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消监听来电
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener=null;
		
		//用代码取消注册广播接收者
		unregisterReceiver(receiver);
		receiver=null;
	}
	
	private class MyPHoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			//第一个参数是状态，第二个参数是打进来的电话号码
			switch (state) {
			//铃声响起的时候，也是来电的时候
			case TelephonyManager.CALL_STATE_RINGING:
				//根据得到的电话号码查询它的归属地，并在土司上显示
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
				Toast.makeText(getApplicationContext(), address, 1).show();
				mToast(address);
				break;
				
			case TelephonyManager.CALL_STATE_IDLE://电话的空闲状态;挂电话；来电拒绝
				//把自定义Toastview移除
				if(view!=null){
					wm.removeView(view);
				}
				break;

			default:
				break;
			}
		}
	}
	
	//服务里面内部类
	//广播接收者的生命周期和服务一样
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//这就是我们拿到的拨出去电话号码
			String phone = getResultData();
			//查询数据库
			String address = NumberAddressQueryUtils.queryNumber(phone);
//			Toast.makeText(context, address, 1).show();
			mToast(address);
		}

	}
	
	//定义手指初始化位置
	int startX = 0 ;
	int startY = 0 ;
	
	long[] mHits = new long[2];

	/**
	 * 自定义土司
	 * @param address
	 */
	public void mToast(String address) {
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
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
			        //双击居中
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
				case MotionEvent.ACTION_MOVE://手指在屏幕上移动
					int newX=(int) event.getRawX();
					int newY=(int) event.getRawY();
					Log.i(TAG, "新的位置：X="+newX+"--Y="+newY);
					int dx=newX-startX;
					int dy=newY-startY;
					Log.i(TAG, "手指的偏移量：X="+dx+"--Y="+dy);
					//更新view在窗体的位置
//					Log.i(TAG, "更新view在窗体的位置：X="+dx+"--Y="+dy);
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
					//重新初始化开始的位置
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_DOWN://手指按下屏幕
					startX=(int) event.getRawX();
					startY=(int) event.getRawY();
					Log.i(TAG, "开始位置：X="+startX+"--Y="+startY);
					break;
					
				case MotionEvent.ACTION_UP://手指离开屏幕瞬间
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;

				default:
					break;
				}
				return false;//事件处理完毕,不要让父控件或者父布局响应触摸事件
			}
		});
		
		params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //与窗体左上角对齐
        params.gravity=Gravity.TOP+Gravity.LEFT;
        //指定窗体距离左边100像素，上边100像素
//        params.x=100;
//        params.y=100;
        int lastX = sp.getInt("lastX", 100);
        int lastY = sp.getInt("lastY", 100);
        params.x=lastX;
        params.y=lastY;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //android系统里具有电话优先级的一种窗体类型
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		wm.addView(view, params);
	}

}
