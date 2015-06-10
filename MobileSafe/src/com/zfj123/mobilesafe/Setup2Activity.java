package com.zfj123.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.zfj123.mobilesafe.ui.SettiingItemView;

public class Setup2Activity extends BaseSetupActivity{
	
	private SettiingItemView siv_setup2_sim;
	
	/**
	 * 读取手机sim的信息
	 */
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		siv_setup2_sim=(SettiingItemView) findViewById(R.id.siv_setup2_sim);
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			//没有绑定
			siv_setup2_sim.setCheck(false);
		}else{
			//绑定好了
			siv_setup2_sim.setCheck(true);
		}
		siv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_setup2_sim.isChecked()){
					siv_setup2_sim.setCheck(false);
					//保存sim卡的序列号
					editor.putString("sim", null);
				}else{
					siv_setup2_sim.setCheck(true);
					//保存sim卡的序列号
					String sim = tm.getSimSerialNumber();
					editor.putString("sim", sim);
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
		//取出是否绑定sim
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			//没有绑定sim
			Toast.makeText(Setup2Activity.this, "sim卡没有绑定", 1).show();
			return;
		}
		Intent intent=new Intent(Setup2Activity.this, Setup3Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {

		Intent intent=new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	
	}
	
}
