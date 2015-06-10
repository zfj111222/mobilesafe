package com.zfj123.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity{
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	/**
	 * 下一步的点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
	}

	@Override
	public  void showNext() {
		Intent intent=new Intent(Setup1Activity.this, Setup2Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
		//要求在finish()或者startActivity()后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}


	@Override
	public void showPre() {
		
	}
	
	
	
}
