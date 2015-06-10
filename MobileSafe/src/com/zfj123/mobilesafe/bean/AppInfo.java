package com.zfj123.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 *Ӧ�ó�����Ϣ��ҵ��beab
 */
public class AppInfo {
	
	/**
	 * Ӧ�ó���ͼ��
	 */
	private Drawable icon;
	/**
	 * Ӧ�ó�������
	 */
	private String name;
	/**
	 * Ӧ�ó������
	 */
	private String packName;
	/**
	 * �ǰ�װ���ֻ��ڴ滹��sd��
	 */
	private boolean inRom;
	/**
	 * ��ϵͳӦ�û����û�Ӧ��
	 */
	private boolean userAPP;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isUserAPP() {
		return userAPP;
	}
	public void setUserAPP(boolean userAPP) {
		this.userAPP = userAPP;
	}
	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", name=" + name + ", packName="
				+ packName + ", inRom=" + inRom + ", userAPP=" + userAPP + "]";
	}
	
}
