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
	 * 设置是否开启自动更新
	 */
	private SettiingItemView siv_update;
	private SharedPreferences sp;
	
	/**
	 * 设置是否开启显示号码归属地
	 */
	private SettiingItemView siv_show_address;
	
	/**
	 * 设置归属地显示框背景
	 */
	private SettiingClickView scv_changebg;
	private Intent intent;
	
	/**
	 * 黑名单拦截设置
	 */
	private SettiingItemView siv_callsms_safe;
	private  Intent callSmsSafeIntent;
	
	/**
	 * 程序锁拦截设置
	 */
	private SettiingItemView siv_watch_dog;
	private  Intent WatchDogIntent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//设置是否开启自动更新
		siv_update=(SettiingItemView) findViewById(R.id.siv_update);
		boolean update=sp.getBoolean("update", false);
		siv_update.setCheck(update);
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor=sp.edit();
				//判断是否选中
				if(siv_update.isChecked()){
					//已经打开自动升级了
					siv_update.setCheck(false);
					editor.putBoolean("update", false);
				}else{
					//没有打开自动升级
					siv_update.setCheck(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		
		//设置是否开启显示号码归属地
		siv_show_address=(SettiingItemView) findViewById(R.id.siv_show_address);
		intent=new Intent(this,AddressService.class);
		siv_show_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_show_address.isChecked()){
					//变为非选中状态
					siv_show_address.setCheck(false);
					stopService(intent);
				}else{
					//变为选中状态
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
		
		//设置归属地显示框背景
		final String[] items= new String[] {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		scv_changebg=(SettiingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		int which=sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int dd=sp.getInt("which", 0);
				AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//保存选择的参数
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						scv_changebg.setDesc(items[which]);
						//取消对话框
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
			
		});
		
		//黑名单拦截设置
		siv_callsms_safe=(SettiingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent=new Intent(this,CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(siv_callsms_safe.isChecked()){
					//变为非选中状态
					siv_callsms_safe.setCheck(false);
					stopService(callSmsSafeIntent);
				}else{
					//变为选中状态
					siv_callsms_safe.setCheck(true);
					startService(callSmsSafeIntent);
				}
			}
		});
		
		// 程序锁拦截设置
		siv_watch_dog = (SettiingItemView) findViewById(R.id.siv_watch_dog);
		WatchDogIntent = new Intent(this, WatchDogService.class);
		siv_watch_dog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watch_dog.isChecked()) {
					// 变为非选中状态
					siv_watch_dog.setCheck(false);
					stopService(WatchDogIntent);
				} else {
					// 变为选中状态
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
