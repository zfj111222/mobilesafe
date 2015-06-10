package com.zfj123.mobilesafe.ui;

import com.zfj123.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �Զ�����Ͽؼ�,������������Textview ��һ��check��һ��view
 * @author Administrator
 *
 */
public class SettiingItemView extends RelativeLayout {
	
	private CheckBox cb_status;
	private TextView tv_desc;
	private TextView tv_title;
	
	private String desc_on;
	private String desc_off;

	/**
	 * �������������Ĺ��췽���������ļ�ʹ�õ�ʱ�����
	 * @param context
	 * @param attrs
	 */
	public SettiingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
//		int count = attrs.getAttributeCount();
//		for (int i = 0; i <count; i++) {
//			String value = attrs.getAttributeValue(i);
//			System.out.println(value);
//		}
		String title = attrs.getAttributeValue( "http://schemas.android.com/apk/res/com.zfj123.mobilesafe", "siv_title");
		desc_on=attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zfj123.mobilesafe", "desc_on");
		desc_off=attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zfj123.mobilesafe", "desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettiingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	public SettiingItemView(Context context) {
		super(context);
		iniView(context);
	}


	/**
	 * ��ʼ�������ļ�
	 * @param context
	 */
	private void iniView(Context context) {
		//��һ�������ļ�---��view���Ҽ�����SettiingItemView
		View.inflate(context, R.layout.setting_item_view, SettiingItemView.this);
		cb_status=(CheckBox) this.findViewById(R.id.cb_status);
		tv_desc=(TextView) this.findViewById(R.id.tv_desc);
		tv_title=(TextView) this.findViewById(R.id.tv_title);
	}
	
	/**
	 * У����Ͽؼ��Ƿ�ѡ��
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}

	/**
	 * ������Ͽؼ���״̬
	 */
	public void setCheck(boolean checked){
		if(checked){
			setDesc(desc_on);
		}else{
			setDesc(desc_off);
		}
		cb_status.setChecked(checked);
	}
	
	/**
	 * ������Ͽؼ���������Ϣ
	 */
	public void setDesc(String set){
		tv_desc.setText(set);
	}
}
