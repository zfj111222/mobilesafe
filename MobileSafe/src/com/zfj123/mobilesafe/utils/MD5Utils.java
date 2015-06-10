
package com.zfj123.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Administrator
 *
 */
public class MD5Utils {

	/**
	 * {@link MD5Utils}加密方法
	 * @param password
	 * @return
	 */
	public static String md5Password(String password){
		StringBuffer buffer=new StringBuffer();
		try {
			//得到一个信息摘要起
			MessageDigest digest=MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			//把每一个byte做一个&运算0xff
			for (byte b : result) {
				//与运算
				int number=b&0xff;
				//oHexString():
				//以十六进制（基数 16）无符号整数形式
				//返回一个整数参数的字符串表示形式。
				String str=Integer.toHexString(number);
				if (str.length()==1) {
					buffer.append("0");
				}
				buffer.append(str);
			}
			//标准的md5加密的结果
//		System.out.println(buffer.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return buffer.toString();
	}
	
}
