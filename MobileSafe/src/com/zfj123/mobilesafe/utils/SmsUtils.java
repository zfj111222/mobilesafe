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
 *短信的工具类
 */
public class SmsUtils {
	
	/**
	 *备份短信的回调接口
	 */
	public interface BackUpCallBack{
		
		/**
		 * 开始备份的时候，设置进度条的最大值
		 * @param max 总进度
		 */
		public void beforeBackup(int max);
		
		/**
		 * 备份过程中，增加进度
		 * @param progress
		 */
		public void onSmsBackup(int progress);
	}

	/**
	 * 备份用户的短信
	 * @param context 上下文
	 * @param BackUpCallBack 备份短信的回调接口
	 */
	public static void backupSms(Context context,BackUpCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file=new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileOutputStream fos=new FileOutputStream(file);
		//把用户的短信一条条读出来，按照一定的格式写到文件里
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
			//备份过程中增加进度
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
	 * 还原短信
	 * @param context
	 * @param flag 是否清理原来的短信
	 */
	public static void restoreSms(Context context,boolean flag){
		Uri uri=Uri.parse("content://sms/");
		if(flag){
			context.getContentResolver().delete(uri, null, null);
		}
		//1.读取sd卡上的xml文件
		/**
		 * xml解析器
		 */
		XmlPullParser pullParser = Xml.newPullParser();
		
		//2.读取max
		
		//3.读取没遇过短信信息， body，date，address,type
		
		//4.把短信插入到系统短信应用
		ContentValues values=new ContentValues();
		values.put("body", "wo shi duan xin nei rong");
		values.put("date", "1433326623539");
		values.put("type", "1");
		values.put("address", "110");
		context.getContentResolver().insert(uri, values);
		Toast.makeText(context, "短信还原成功", 1).show();
	}
}
