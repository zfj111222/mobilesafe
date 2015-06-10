package com.zfj123.mobilesafe.utils;

import android.content.Context;

public class DensityUtils {
	
	/**
	 * 根据手机分辨率从dip的单位转成px（像素）单位
	 * @param context
	 * @param dpValue
	 * @return scale px像素单位
	 */
	public static int dip2px(Context context,float dpValue){
		final float scale=context.getResources().getDisplayMetrics().density;
		return (int)(dpValue*scale+0.5f);
	}
	
	/**
	 * 根据手机分辨率从px（像素）转换成dip单位
	 * @param context
	 * @param pxValue
	 * @return scale dip单位
	 */
	public static int px2dip(Context context,float pxValue){
		final float scale=context.getResources().getDisplayMetrics().density;
		return (int) (pxValue/scale+0.5f);
	}
}
