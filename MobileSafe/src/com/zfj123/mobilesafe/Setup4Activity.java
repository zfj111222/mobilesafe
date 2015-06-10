package com.zfj123.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity{
	
	private SharedPreferences sp;
	private CheckBox cb_safe_state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		boolean safeState = sp.getBoolean("safeState", false);
		setContentView(R.layout.activity_setup4);
		cb_safe_state=(CheckBox) findViewById(R.id.cb_safe_state);
		if (safeState) {
			cb_safe_state.setChecked(true);
			cb_safe_state.setText("你已经开启防盗保护");
		}else{
			cb_safe_state.setChecked(false);
			cb_safe_state.setText("你取消了开启防盗保护");
		}
		cb_safe_state.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				if (isChecked) {
					cb_safe_state.setText("你已经开启防盗保护");
					cb_safe_state.setChecked(true);
					editor.putBoolean("safeState", true);
				}else{
					cb_safe_state.setText("你取消了开启防盗保护");
					cb_safe_state.setChecked(false);
					editor.putBoolean("safeState", false);
				}
				editor.commit();
			}
		});
	}
	
	/**
	 * 下一步的点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
	}
	
	/**
	 * 上一步的点击事件
	 * @param view
	 */
	public void pre(View view){
		showPre();
	}

	@Override
	public void showNext() {
		Editor editor=sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		Intent intent=new Intent(Setup4Activity.this, LostFindActivity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent=new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
}
