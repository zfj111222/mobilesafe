package com.zfj123.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity{
	
	private  EditText et_setup3_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_phone=(EditText) findViewById(R.id.et_setup3_phone);
		String phone = sp.getString("safenumber", "");
		et_setup3_phone.setText(phone);
	}
	
	/**
	 * ��һ���ĵ���¼�
	 * @param view
	 */
	public void next(View view){
		showNext();
	}
	
	/**
	 * ��һ���ĵ���¼�
	 * @param view
	 */
	public void pre(View view){
		showPre();
	}

	@Override
	public void showNext() {
		String phone = et_setup3_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(Setup3Activity.this, "��ȫ���뻹û������", 1).show();
			return;
		}
		//���氲ȫ����
		Editor editor = sp.edit();
		editor.putString("safenumber", phone);
		editor.commit();
		Intent intent=new Intent(Setup3Activity.this, Setup4Activity.class);
		startActivity(intent);
		//�رյ�ǰҳ��
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent=new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		//�رյ�ǰҳ��
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	/**
	 * ѡ����ϵ�˵ĵ���¼�
	 * @param view
	 */
	public void selectContact(View view){
		Intent intent=new Intent(Setup3Activity.this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0&&resultCode==1) {
			if(data==null){
				return;
			}
			String phone = data.getStringExtra("phone").replace("-", "");
			et_setup3_phone.setText(phone);
		}
	}
}
