package com.zfj123.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 *���ŵĹ�����
 */
public class SmsUtils {
	
	/**
	 *���ݶ��ŵĻص��ӿ�
	 */
	public interface BackUpCallBack{
		
		/**
		 * ��ʼ���ݵ�ʱ�����ý����������ֵ
		 * @param max �ܽ���
		 */
		public void beforeBackup(int max);
		
		/**
		 * ���ݹ����У����ӽ���
		 * @param progress
		 */
		public void onSmsBackup(int progress);
	}

	/**
	 * �����û��Ķ���
	 * @param context ������
	 * @param BackUpCallBack ���ݶ��ŵĻص��ӿ�
	 */
	public static void backupSms(Context context,BackUpCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file=new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileOutputStream fos=new FileOutputStream(file);
		//���û��Ķ���һ����������������һ���ĸ�ʽд���ļ���
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri=Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body","address","type","date"}, null, null, null);
		int count = cursor.getCount();
//		pb.setMax(count);
//		pd.setMax(count);
		callBack.beforeBackup(count);
		serializer.attribute(null, "max", count+"");
		int progress=0;
		while(cursor.moveToNext()){
			Thread.sleep(500);
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			serializer.startTag(null, "sms");
			
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.endTag(null, "sms");
			//���ݹ��������ӽ���
			progress++;
//			pb.setProgress(progress);
//			pd.setProgress(progress);
			callBack.onSmsBackup(progress);
		}
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
		cursor.close();
	}
	
	/**
	 * ��ԭ����
	 * @param context
	 * @param flag �Ƿ�����ԭ���Ķ���
	 */
	public static void restoreSms(Context context,boolean flag){
		Uri uri=Uri.parse("content://sms/");
		if(flag){
			context.getContentResolver().delete(uri, null, null);
		}
		//1.��ȡsd���ϵ�xml�ļ�
		/**
		 * xml������
		 */
		XmlPullParser pullParser = Xml.newPullParser();
		
		//2.��ȡmax
		
		//3.��ȡû����������Ϣ�� body��date��address,type
		
		//4.�Ѷ��Ų��뵽ϵͳ����Ӧ��
		ContentValues values=new ContentValues();
		values.put("body", "wo shi duan xin nei rong");
		values.put("date", "1433326623539");
		values.put("type", "1");
		values.put("address", "110");
		context.getContentResolver().insert(uri, values);
		Toast.makeText(context, "���Ż�ԭ�ɹ�", 1).show();
	}
}
