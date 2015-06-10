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
	 * ϵͳ�ṩ���𶯷���
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
			 * ���ı����ͱ仯ʱ�Ļص�
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length()>3&&s!=null){
					String address = NumberAddressQueryUtils.queryNumber(s.toString());
					tv_result.setText(address);
				}
			}
			
			/**
			 * �ı������仯֮ǰ�ص�
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			/**
			 * �ı������仯��ص�
			 */
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	/**
	 * ��ѯ���������
	 * @param view
	 */
	public void numberAddressQuery(View view){
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "����Ϊ��", 0).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//			shake.setInterpolator(new Interpolator() {
//				
//				@Override
//				public float getInterpolation(float x) {
//					//����������
//					return y;
//				}
//			});
			et_phone.startAnimation(shake);
			//���绰����Ϊ�յ�ʱ�򣬾�ȥ�������û�
			//long[] {ͣ�٣���ʱ��}
			long[] pattern={200,200,300,300,1000,2000};
			//-1���ظ���0ѭ���𶯣�1
			vibrator.vibrate(pattern, -1);
			return;
		}else{
			//ȥ���ݿ��ѯ���������
			//1.�����ѯ 2.���ص����ݿ�--���ݿ�
			String address = NumberAddressQueryUtils.queryNumber(phone);
			tv_result.setText(address);
			Log.i(TAG, "��ѯ�ĺ���:"+address);
		}
	}
}
