package com.zfj123.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * ���ݿⴴ���Ĺ��췽�� 
	 * @param context   ���ݿ����� applock.db
	 */
	public AppLockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}

	/**
	 * ��ʼ�����ݿ�ı�ṹ
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock( _id integer primary "+
				"key autoincrement ,packname varchar(20))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
