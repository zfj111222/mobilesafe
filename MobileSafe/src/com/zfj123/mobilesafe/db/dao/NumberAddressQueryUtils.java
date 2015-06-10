package com.zfj123.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	
	private static String  path="data/data/com.zfj123.mobilesafe/files/address.db";

	/**
	 * 传一个号码进来，返回一个归属地回去
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number){
		String address=number;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		//path 把address.db这个数据库到/data/data/《包名》/files/address.db
		//手机号码的正则表达式
		if(number.matches("^1[34568]\\d{9}$")){
		String sql="select location from data2 where id=(select outkey from data1 where id=?)";
		Cursor cursor = database.rawQuery(sql, new String[]{number.substring(0, 7)});
		while(cursor.moveToNext()){
			String location = cursor.getString(0);
			address=location;
		}
		cursor.close();
		}else{
			switch (number.length()) {
			case 3:
				//可能是 110 119
				address="匪警号码";
				break;
				
			case 4:
				//可能是 5556
				address="模拟器号码";
				break;
				
			case 5:
				//可能是 10086
				address="客服电话";
				break;
				
			case 7:
				//可能是 10086
				address="本地号码";
				break;
				
			case 8:
				//可能是 10086
				address="本地号码";
				break;

			default:
				//长途电话>10
				if(number.length()>=10&&number.startsWith("0")){
					String sql="select location from data2 where area=?";
					Cursor cursor = database.rawQuery(sql, 
							new String[]{number.substring(1, 3)});
					while(cursor.moveToNext()&&cursor!=null){
						String location = cursor.getString(0);
						address=location.substring(0, location.length()-2);
					}
					cursor.close();
					
					cursor = database.rawQuery(sql, 
							new String[]{number.substring(1, 4)});
					while(cursor.moveToNext()&&cursor!=null){
						String location = cursor.getString(0);
						address=location.substring(0, location.length()-2);
					}
					cursor.close();
				}
				break;
			}
		}
		return address;
	}
}
