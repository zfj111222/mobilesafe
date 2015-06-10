package com.zfj123.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LostFindActivity extends Activity{
	
	private SharedPreferences sp;
	private ImageView iv_safe_state;
	private TextView tv_safe_number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//判断一下，是否做过设置向导，如果没有做过，
		//就跳转到设置向导页面去设置，否则就留着当前的页面
		boolean configed = sp.getBoolean("configed", false);
		boolean sateState=sp.getBoolean("safeState", false);
		String safeNumber = sp.getString("safenumber", "");
		System.out.println("safeNumber-->"+safeNumber);
		if(configed){
			//就在手机防盗页面
			setContentView(R.layout.activity_lost_find);
			tv_safe_number=(TextView) findViewById(R.id.tv_safe_number);
			if(TextUtils.isEmpty(safeNumber)){
				tv_safe_number.setText("没有设置安全号码");
				Toast.makeText(LostFindActivity.this, "安全号码没有设置", 0).show();
			}else{
				tv_safe_number.setText(safeNumber);
			}
			iv_safe_state=(ImageView) findViewById(R.id.iv_safe_state);
			if(sateState){
				iv_safe_state.setImageResource(R.drawable.lock);
			}else{
				iv_safe_state.setImageResource(R.drawable.unlock);
			}
		}else{
			Intent intent=new Intent(LostFindActivity.this,Setup1Activity.class);
			startActivity(intent);
			//关闭当前页面
			finish();
		}
	}
	
	/**
	 * 重新进入手机防盗设置页面向导
	 */
	public void reEnterSetup(View view){
		Intent intent=new Intent(LostFindActivity.this,Setup1Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
	}
}
