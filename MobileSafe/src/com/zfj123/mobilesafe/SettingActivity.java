package com.zfj123.mobilesafe;

import com.zfj123.mobilesafe.service.AddressService;
import com.zfj123.mobilesafe.service.CallSmsSafeService;
import com.zfj123.mobilesafe.service.WatchDogService;
import com.zfj123.mobilesafe.ui.SettiingClickView;
import com.zfj123.mobilesafe.ui.SettiingItemView;
import com.zfj123.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity{
	
	/**
	 * �����Ƿ����Զ�����
	 */
	private SettiingItemView siv_update;
	private SharedPreferences sp;
	
	/**
	 * �����Ƿ�����ʾ���������
	 */
	private SettiingItemView siv_show_address;
	
	/**
	 * ���ù�������ʾ�򱳾�
	 */
	private SettiingClickView scv_changebg;
	private Intent intent;
	
	/**
	 * ��������������
	 */
	private SettiingItemView siv_callsms_safe;
	private  Intent callSmsSafeIntent;
	
	/**
	 * ��������������
	 */
	private SettiingItemView siv_watch_dog;
	private  Intent WatchDogIntent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//�����Ƿ����Զ�����
		siv_update=(SettiingItemView) findViewById(R.id.siv_update);
		boolean update=sp.getBoolean("update", false);
		siv_update.setCheck(update);
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor=sp.edit();
				//�ж��Ƿ�ѡ��
				if(siv_update.isChecked()){
					//�Ѿ����Զ�������
					siv_update.setCheck(false);
					editor.putBoolean("update", false);
				}else{
					//û�д��Զ�����
					siv_update.setCheck(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		
		//�����Ƿ�����ʾ���������
		siv_show_address=(SettiingItemView) findViewById(R.id.siv_show_address);
		intent=new Intent(this,AddressService.class);
		siv_show_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_show_address.isChecked()){
					//��Ϊ��ѡ��״̬
					siv_show_address.setCheck(false);
					stopService(intent);
				}else{
					//��Ϊѡ��״̬
					siv_show_address.setCheck(true);
					startService(intent);
				}
			}
		});
		boolean isServiceRunnin = ServiceUtils.isServiceRunnung(SettingActivity.this, "com.zfj123.mobilesafe.service.AddressService");
		if(isServiceRunnin){
			siv_show_address.setCheck(true);
		}else{
			siv_show_address.setCheck(false);
		}
		
		//���ù�������ʾ�򱳾�
		final String[] items= new String[] {"��͸��","������","��ʿ��","������","ƻ����"};
		scv_changebg=(SettiingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("��������ʾ����");
		int which=sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int dd=sp.getInt("which", 0);
				AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//����ѡ��Ĳ���
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						//ȡ���Ի���
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
			
		});
		
		//��������������
		siv_callsms_safe=(SettiingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent=new Intent(this,CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_callsms_safe.isChecked()){
					//��Ϊ��ѡ��״̬
					siv_callsms_safe.setCheck(false);
					stopService(callSmsSafeIntent);
				}else{
					//��Ϊѡ��״̬
					siv_callsms_safe.setCheck(true);
					startService(callSmsSafeIntent);
				}
			}
		});
		
		// ��������������
		siv_watch_dog = (SettiingItemView) findViewById(R.id.siv_watch_dog);
		WatchDogIntent = new Intent(this, WatchDogService.class);
		siv_watch_dog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watch_dog.isChecked()) {
					// ��Ϊ��ѡ��״̬
					siv_watch_dog.setCheck(false);
					stopService(WatchDogIntent);
				} else {
					// ��Ϊѡ��״̬
					siv_watch_dog.setCheck(true);
					startService(WatchDogIntent);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		intent=new Intent(this,AddressService.class);
		boolean isServiceRuning=ServiceUtils.isServiceRunnung(this, "com.zfj123.mobilesafe.service.AddressService");
		if(isServiceRuning){
			siv_show_address.setCheck(true);
		}else{
			siv_show_address.setCheck(false);
		}
		boolean isCallSmsSafeRuning=ServiceUtils.isServiceRunnung(this, 
				"com.zfj123.mobilesafe.service.CallSmsSafeService");
		if(isCallSmsSafeRuning){
			siv_callsms_safe.setCheck(true);
		}else{
			siv_callsms_safe.setCheck(false);
		}
		
		boolean isWatchDogRuning=ServiceUtils.isServiceRunnung(this, 
				"com.zfj123.mobilesafe.service.WatchDogService");
		if(isWatchDogRuning){
			siv_watch_dog.setCheck(true);
		}else{
			siv_watch_dog.setCheck(false);
		}
	}
}
