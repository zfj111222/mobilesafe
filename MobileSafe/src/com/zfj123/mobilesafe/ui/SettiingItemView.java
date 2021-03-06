package com.zfj123.mobilesafe.ui;

import com.zfj123.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义组合控件,它里面有两个Textview ，一个check，一个view
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
	 * 带有两个参数的构造方法，布局文件使用的时候调用
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
	 * 初始化布局文件
	 * @param context
	 */
	private void iniView(Context context) {
		//把一个布局文件---》view并且加载在SettiingItemView
		View.inflate(context, R.layout.setting_item_view, SettiingItemView.this);
		cb_status=(CheckBox) this.findViewById(R.id.cb_status);
		tv_desc=(TextView) this.findViewById(R.id.tv_desc);
		tv_title=(TextView) this.findViewById(R.id.tv_title);
	}
	
	/**
	 * 校验组合控件是否选中
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}

	/**
	 * 设置组合控件的状态
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
	 * 设置组合控件的描述信息
	 */
	public void setDesc(String set){
		tv_desc.setText(set);
	}
}
