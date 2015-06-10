package com.zfj123.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * �Զ���TextView��һ�������н���
 * @author Administrator
 */
public class FocusedTextView extends TextView {

	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	/**
	 * ��ǰ��û�н��㣬ֻ����ƭ��Androidϵͳ
	 */
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}
}
