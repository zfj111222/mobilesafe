package com.zfj123.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.zfj123.mobilesafe.bean.BlackNumberInfo;
import com.zfj123.mobilesafe.db.BlackNumberDBOpenHelper;

/**
 * ���������ݿ����ɾ�Ĳ�
 * @author Administrator
 */
public class BlackNumberDao {
	
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper=new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * ��ѯ�����������Ƿ����
	 * @param number
	 * @return
	 */
	public boolean find(String number){
		boolean result=false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select * from blacknumber where number=? order by _id desc", 
				new String[]{number});
		if(cursor.moveToNext()){
			result=true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ѯ���������������ģʽ
	 * @param number
	 * @return ���غ�������������ģʽ,���Ǻ��������뷵�ؿ�
	 */
	public String findMode(String number){
		String result=null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select mode from blacknumber where number=? order by _id desc", 
				new String[]{number});
		if(cursor.moveToNext()){
			result=cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��Ӻ���������
	 * @param number ����������
	 * @param mode ����ģʽ
	 * 1Ϊ�绰���� 2Ϊ��������  3Ϊȫ������
	 */
	public void add(String number,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	
	/**
	 * �޸ĺ��������������ģʽ
	 * @param number ����������
	 * @param mode �µ�����ģʽ
	 */
	public void update(String number,String newMode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("mode", newMode);
		db.update("blacknumber", values, "number=?", 
				new String[]{number});
		db.close();
	}
	
	/**
	 * ɾ�����������������ģʽ
	 * @param number ����������
	 * @param mode �µ�����ģʽ
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}

	/**
	 * ��ѯȫ������������
	 */
	public List<BlackNumberInfo> findAll(){
		List<BlackNumberInfo> result=new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor=db.query("blacknumber",new String[]{"number","mode"}, 
				null, null, null, null, null);
		int count=cursor.getCount();
		System.out.println("�ж��������ݣ�"+count);
		SystemClock.sleep(3000);
		while(cursor.moveToNext()){
			BlackNumberInfo info=new BlackNumberInfo();
			String number=cursor.getString(0);
			String mode=cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ѯ���ֺ���������
	 * @param offset ����һ��λ�ÿ�ʼ��ʼ����
	 * @param maxnumber һ�������Ҷ�����
	 * @retun result List<BlackNumberInfo>
	 */
	public List<BlackNumberInfo> findPart(int offset,int maxnumber){
		List<BlackNumberInfo> result=new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select *  from blacknumber order by _id desc limit ? offset ?", 
				new String[]{String.valueOf(maxnumber),String.valueOf(offset)});
		int count=cursor.getCount();
		System.out.println("�ж��������ݣ�"+count);
		SystemClock.sleep(500);
		while(cursor.moveToNext()){
			BlackNumberInfo info=new BlackNumberInfo();
			String number=cursor.getString(0);
			String mode=cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}
}
