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
	 * ����ʶ����
	 */
	//1.����һ������ʶ����
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
				//������y�Ử������������
				if(Math.abs(velocityX)<200){
					Toast.makeText(getApplicationContext(), "����̫����", 0).show();
				}
				
				//����б�Ż������������
				if (Math.abs(e1.getRawY()-e2.getRawY())>100) {
					Toast.makeText(getApplicationContext(), "������������", 0).show();
				}
				
				if((e2.getRawX()-e1.getRawX())>200){
					//��ʾ��һ��ҳ�棬�������һ���
					System.out.println("��ʾ��һ��ҳ��");
					showPre();
					return true;
				}
				if((e1.getRawX()-e2.getRawX())>200){
					//��ʾ��һ��ҳ�棬�������󻬶�
					System.out.println("��ʾ��һ��ҳ��");
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
	
	//3.ʹ������ʶ����
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			detector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}

}
