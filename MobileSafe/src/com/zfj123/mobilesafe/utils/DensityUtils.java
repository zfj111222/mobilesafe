package com.zfj123.mobilesafe.utils;

import android.content.Context;

public class DensityUtils {
	
	/**
	 * �����ֻ��ֱ��ʴ�dip�ĵ�λת��px�����أ���λ
	 * @param context
	 * @param dpValue
	 * @return scale px���ص�λ
	 */
	public static int dip2px(Context context,float dpValue){
		final float scale=context.getResources().getDisplayMetrics().density;
		return (int)(dpValue*scale+0.5f);
	}
	
	/**
	 * �����ֻ��ֱ��ʴ�px�����أ�ת����dip��λ
	 * @param context
	 * @param pxValue
	 * @return scale dip��λ
	 */
	public static int px2dip(Context context,float pxValue){
		final float scale=context.getResources().getDisplayMetrics().density;
		return (int) (pxValue/scale+0.5f);
	}
}
