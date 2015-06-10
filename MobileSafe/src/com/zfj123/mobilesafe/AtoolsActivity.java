package com.zfj123.mobilesafe;

import com.zfj123.mobilesafe.R.id;
import com.zfj123.mobilesafe.utils.SmsUtils;
import com.zfj123.mobilesafe.utils.SmsUtils.BackUpCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	
	private ProgressBar pb;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		pb=(ProgressBar) findViewById(R.id.progressBar1);
	}
	
	/**
	 * ����¼��������������ز�ѯ��ҳ��
	 * @param view
	 */
	public void numberQuery(View view ){
		Intent intent=new Intent(AtoolsActivity.this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}
	
	/**
	 * ����¼������ű���
	 * @param v
	 */
	public void smsBackup(View v){
		pd=new ProgressDialog(AtoolsActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("���ڱ��ݶ���");
		pd.show();
		new Thread(){
			@Override
			public void run() {
				try {
					SmsUtils.backupSms(AtoolsActivity.this,new BackUpCallBack() {
						
						@Override
						public void onSmsBackup(int progress) {
							pd.setProgress(progress);
							pb.setProgress(progress);
						}
						
						@Override
						public void beforeBackup(int max) {
							pd.setMax(max);
							pb.setMax(max);
						}
					});
					runOnUiThread(new  Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "���ű��ݳɹ�", 0).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new  Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "���ű���ʧ��", 0).show();
						}
					});
				}
				finally{
					pd.dismiss();
				}
			}
		}.start();
		
	}
	
	/**
	 * ����¼������Ż�ԭ
	 * @param v
	 */
	public void smsRestore(View v){
		SmsUtils.restoreSms(AtoolsActivity.this,true);
	}

}
