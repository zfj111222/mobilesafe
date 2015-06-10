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
 * 黑名单数据库的增删改查
 * @author Administrator
 */
public class BlackNumberDao {
	
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper=new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * 查询黑名单号码是否存在
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
	 * 查询黑名单号码的拦截模式
	 * @param number
	 * @return 返回黑名单号码拦截模式,不是黑名单号码返回空
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
	 * 添加黑名单号码
	 * @param number 黑名单号码
	 * @param mode 拦截模式
	 * 1为电话拦截 2为短信拦截  3为全部拦截
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
	 * 修改黑名单号码的拦截模式
	 * @param number 黑名单号码
	 * @param mode 新的拦截模式
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
	 * 删除黑名单号码的拦截模式
	 * @param number 黑名单号码
	 * @param mode 新的拦截模式
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}

	/**
	 * 查询全部黑名单号码
	 */
	public List<BlackNumberInfo> findAll(){
		List<BlackNumberInfo> result=new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor=db.query("blacknumber",new String[]{"number","mode"}, 
				null, null, null, null, null);
		int count=cursor.getCount();
		System.out.println("有多少条数据："+count);
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
	 * 查询部分黑名单号码
	 * @param offset 从那一个位置开始开始查找
	 * @param maxnumber 一次最多查找多少条
	 * @retun result List<BlackNumberInfo>
	 */
	public List<BlackNumberInfo> findPart(int offset,int maxnumber){
		List<BlackNumberInfo> result=new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor=db.rawQuery("select *  from blacknumber order by _id desc limit ? offset ?", 
				new String[]{String.valueOf(maxnumber),String.valueOf(offset)});
		int count=cursor.getCount();
		System.out.println("有多少条数据："+count);
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
