package com.zfj123.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zfj123.mobilesafe.db.dao.NumberAddressQueryUtils;

public class NumberAddressQueryActivity extends Activity {
	
	private static final String TAG = "NumberAddressQueryActivity";
	private TextView tv_result;
	private EditText et_phone;
	/**
	 * 系统提供的震动服务
	 */
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_phone=(EditText) findViewById(R.id.et_phone);
		tv_result=(TextView) findViewById(R.id.tv_result);
		et_phone.addTextChangedListener(new TextWatcher() {
			
			/**
			 * 当文本发送变化时的回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()>3&&s!=null){
					String address = NumberAddressQueryUtils.queryNumber(s.toString());
					tv_result.setText(address);
				}
			}
			
			/**
			 * 文本发生变化之前回调
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			/**
			 * 文本发生变化后回调
			 */
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	/**
	 * 查询号码归属地
	 * @param view
	 */
	public void numberAddressQuery(View view){
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "号码为空", 0).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//			shake.setInterpolator(new Interpolator() {
//				
//				@Override
//				public float getInterpolation(float x) {
//					//函数的运算
//					return y;
//				}
//			});
			et_phone.startAnimation(shake);
			//当电话号码为空的时候，就去震动提醒用户
			//long[] {停顿，震动时间}
			long[] pattern={200,200,300,300,1000,2000};
			//-1不重复，0循环震动，1
			vibrator.vibrate(pattern, -1);
			return;
		}else{
			//去数据库查询号码归属地
			//1.网络查询 2.本地的数据库--数据库
			String address = NumberAddressQueryUtils.queryNumber(phone);
			tv_result.setText(address);
			Log.i(TAG, "查询的号码:"+address);
		}
	}
}
