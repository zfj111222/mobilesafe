package com.zfj123.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.zfj123.mobilesafe.db.AppLockDBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *��������dao
 */
public class AppLockDao {
	
	private AppLockDBOpenHelper helper;
	private Context context;
	
	/**
	 * AppLockDao���췽��
	 * @param context ������
	 */ 
	public AppLockDao(Context context){
		helper=new AppLockDBOpenHelper(context);
		this.context=context;
	}
	
	/**
	 * ���һ��Ҫ����Ӧ�ó������
	 * @param packname
	 */
	public void add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		Intent intent=new Intent();
		intent.setAction("com.zfj123.mobilesafe.applockchange");
		this.context.sendBroadcast(intent);
	}
	
	/**
	 * ɾ��һ��Ҫ����Ӧ�ó������
	 * @param packname
	 */
	public void delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		Intent intent=new Intent();
		intent.setAction("com.zfj123.mobilesafe.applockchange");
		this.context.sendBroadcast(intent);
	}
	
	/**
	 * ��ѯһ��������������¼�Ƿ����
	 * @param packname
	 */
	public boolean find(String packname){
		boolean result=false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname},
				null, null, null);
		while(cursor.moveToNext()){
			result=true;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ѯȫ������
	 * @param packname
	 */
	public List<String> findAll(){
		List<String> protectPacknames=new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packname"}, 
				null,null,null, null, null);
		while(cursor.moveToNext()){
			protectPacknames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return protectPacknames;
	}
}
