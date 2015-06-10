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
	 * ��ȡ�ֻ�sim����Ϣ
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
			//û�а�
			siv_setup2_sim.setCheck(false);
		}else{
			//�󶨺���
			siv_setup2_sim.setCheck(true);
		}
		siv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_setup2_sim.isChecked()){
					siv_setup2_sim.setCheck(false);
					//����sim�������к�
					editor.putString("sim", null);
				}else{
					siv_setup2_sim.setCheck(true);
					//����sim�������к�
					String sim = tm.getSimSerialNumber();
					editor.putString("sim", sim);
				}
				editor.commit();
			}
		});
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
		//ȡ���Ƿ��sim
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			//û�а�sim
			Toast.makeText(Setup2Activity.this, "sim��û�а�", 1).show();
			return;
		}
		Intent intent=new Intent(Setup2Activity.this, Setup3Activity.class);
		startActivity(intent);
		//�رյ�ǰҳ��
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {

		Intent intent=new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		//�رյ�ǰҳ��
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	
	}
	
}
