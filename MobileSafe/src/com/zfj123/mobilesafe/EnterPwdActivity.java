package com.zfj123.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends Activity {
	
	private EditText et_password;
	private String packname;
	private TextView tv_name;
	private ImageView  iv_icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);
		et_password=(EditText) findViewById(R.id.et_password);
		tv_name=(TextView) findViewById(R.id.tv_name);
		iv_icon=(ImageView) findViewById(R.id.iv_icon);
		Intent intent=getIntent();
		//��ǰҪ������Ӧ�ó������
		packname = intent.getStringExtra("packname");
		PackageManager pm=getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);
			tv_name.setText(info.loadLabel(pm));
			iv_icon.setImageDrawable(info.loadIcon(pm));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void click(View v){
		String pwd=et_password.getText().toString().trim();
		if(TextUtils.isEmpty(pwd)){
			Toast.makeText(getApplicationContext(), "���벻��Ϊ��", 0).show();
			return;
		}
		if("123".equals(pwd)){
			//���߿��Ź�������������Ѿ�������ȷ��������ʱֹͣ������
			//�Զ���㲥
			Intent intent=new Intent();
			intent.setAction("com.zfj123.mobilesafe.stopdog");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			finish();
		}else{
			Toast.makeText(getApplicationContext(), "����", 0).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		//�ص�����
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
		Intent intent=new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		//���е�activity��С��������ִ��ondestory��ִֻ��onstop����
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
