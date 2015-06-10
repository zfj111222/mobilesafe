package com.zfj123.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 *应用程序信息的业务beab
 */
public class AppInfo {
	
	/**
	 * 应用程序图标
	 */
	private Drawable icon;
	/**
	 * 应用程序名称
	 */
	private String name;
	/**
	 * 应用程序包名
	 */
	private String packName;
	/**
	 * 是安装在手机内存还是sd卡
	 */
	private boolean inRom;
	/**
	 * 是系统应用还是用户应用
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
