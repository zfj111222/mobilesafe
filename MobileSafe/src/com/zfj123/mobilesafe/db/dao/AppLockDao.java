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
 *程序锁的dao
 */
public class AppLockDao {
	
	private AppLockDBOpenHelper helper;
	private Context context;
	
	/**
	 * AppLockDao构造方法
	 * @param context 上下文
	 */ 
	public AppLockDao(Context context){
		helper=new AppLockDBOpenHelper(context);
		this.context=context;
	}
	
	/**
	 * 添加一个要锁定应用程序包名
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
	 * 删除一个要锁定应用程序包名
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
	 * 查询一条程序锁包名记录是否存在
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
	 * 查询全部包名
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
