
package com.zfj123.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Administrator
 *
 */
public class MD5Utils {

	/**
	 * {@link MD5Utils}���ܷ���
	 * @param password
	 * @return
	 */
	public static String md5Password(String password){
		StringBuffer buffer=new StringBuffer();
		try {
			//�õ�һ����ϢժҪ��
			MessageDigest digest=MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			//��ÿһ��byte��һ��&����0xff
			for (byte b : result) {
				//������
				int number=b&0xff;
				//oHexString():
				//��ʮ�����ƣ����� 16���޷���������ʽ
				//����һ�������������ַ�����ʾ��ʽ��
				String str=Integer.toHexString(number);
				if (str.length()==1) {
					buffer.append("0");
				}
				buffer.append(str);
			}
			//��׼��md5���ܵĽ��
//		System.out.println(buffer.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return buffer.toString();
	}
	
}
