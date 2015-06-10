package com.zfj123.mobilesafe.receiver;

import com.zfj123.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//����������õ��Ĳ���ȥ�绰����
		String phone = getResultData();
		//��ѯ���ݿ�
		String address = NumberAddressQueryUtils.queryNumber(phone);
		Toast.makeText(context, address, 1).show();
	}

}
