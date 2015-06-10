package com.zfj123.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	
	protected SharedPreferences sp;
	
	/**
	 * 手势识别器
	 */
	//1.定义一个手势识别器
	private GestureDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp=getSharedPreferences("config", MODE_PRIVATE);
		detector=new GestureDetector(BaseSetupActivity.this, new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				//屏蔽在y轴滑动很慢的情形
				if(Math.abs(velocityX)<200){
					Toast.makeText(getApplicationContext(), "滑动太慢了", 0).show();
				}
				
				//屏蔽斜着滑动这样的情况
				if (Math.abs(e1.getRawY()-e2.getRawY())>100) {
					Toast.makeText(getApplicationContext(), "不能这样滑动", 0).show();
				}
				
				if((e2.getRawX()-e1.getRawX())>200){
					//显示上一个页面，从左往右滑动
					System.out.println("显示上一个页面");
					showPre();
					return true;
				}
				if((e1.getRawX()-e2.getRawX())>200){
					//显示下一个页面，从右往左滑动
					System.out.println("显示下一个页面");
					showNext();
					return true;
				}
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
	}
	
	public  abstract void showNext();
	public abstract void showPre();
	
	/**
	 * 下一步的点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
	}
	
	/**
	 * 上一步的点击事件
	 * @param view
	 */
	public void pre(View view){
		showPre();
	}
	
	//3.使用手势识别器
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			detector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}

}
