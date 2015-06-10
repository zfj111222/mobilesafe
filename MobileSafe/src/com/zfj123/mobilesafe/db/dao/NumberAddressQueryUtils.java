package com.zfj123.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	
	private static String  path="data/data/com.zfj123.mobilesafe/files/address.db";

	/**
	 * ��һ���������������һ�������ػ�ȥ
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number){
		String address=number;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		//path ��address.db������ݿ⵽/data/data/��������/files/address.db
		//�ֻ������������ʽ
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
				//������ 110 119
				address="�˾�����";
				break;
				
			case 4:
				//������ 5556
				address="ģ��������";
				break;
				
			case 5:
				//������ 10086
				address="�ͷ��绰";
				break;
				
			case 7:
				//������ 10086
				address="���غ���";
				break;
				
			case 8:
				//������ 10086
				address="���غ���";
				break;

			default:
				//��;�绰>10
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
