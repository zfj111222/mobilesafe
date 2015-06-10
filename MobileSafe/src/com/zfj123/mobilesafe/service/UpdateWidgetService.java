package com.zfj123.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.zfj123.mobilesafe.R;
import com.zfj123.mobilesafe.receiver.SafeWidget;
import com.zfj123.mobilesafe.utils.SystemInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	protected static final String TAG = "UpdateWidgetService";
	private screenOffReceiver offreceiver;
	private screenOnReceiver onreceiver;
	private Timer timer;
	private TimerTask task;
	/**
	 * widget������
	 */
	private AppWidgetManager awm;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		offreceiver=new screenOffReceiver();
		onreceiver=new screenOnReceiver();
		registerReceiver(offreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onreceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		awm=AppWidgetManager.getInstance(this);
		startTimer();
	}

	private void startTimer() {
		if(timer==null&&task==null){
		timer=new Timer();
		task=new TimerTask() {
			
			@Override
			public void run() {
				Log.i(TAG, "widget����");
				//���ø��µ����
				ComponentName provider=new ComponentName(UpdateWidgetService.this,
						SafeWidget.class);
				RemoteViews views=new RemoteViews(getPackageName(), R.layout.process_widget);
				views.setTextViewText(R.id.process_count, "�������еĽ��̣�"+
						SystemInfoUtils.getProcessCount(UpdateWidgetService.this));
				long size=SystemInfoUtils.getAvailMem(getApplicationContext());
				views.setTextViewText(R.id.process_memory, "�����ڴ�:"+
						Formatter.formatFileSize(getApplicationContext(), size));
				//����һ�����������������������һ��Ӧ�ó���ִ��
				//�Զ���һ���㲥�¼���ɱ����̨���̵��¼�
				Intent intent=new Intent();
				intent.setAction("com.zfj123.mobilesafe.killall");
				PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),
						0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.scheduleAtFixedRate(task, 0, 3000);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(offreceiver);
		unregisterReceiver(onreceiver);
		offreceiver=null;
		onreceiver=null;
		stopTimer();
	}

	private void stopTimer() {
		if(timer!=null&&task!=null){
			timer.cancel();
			task.cancel();
			timer=null;
			task=null;
		}
	}

	private class screenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
				Log.i("screenOffReceiver", "��Ļ������");
				stopTimer();
			}
	}
	
	private class screenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
				Log.i("screenOnReceiver", "��Ļ������");
				startTimer();
			}
	}
}
