package com.zfj123.mobilesafe.bean;

import android.graphics.drawable.Drawable;


/**
 *������Ϣ��ҵ��bean
 */
public class TaskInfo {

	private Drawable icon;
	private String name;
	private String packageName ;
	private long memsiez;
	private boolean check;
	/**
	 * true �û����� false ϵͳ����
	 */
	private boolean isUserTask;
	
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


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public long getMemsiez() {
		return memsiez;
	}


	public void setMemsiez(long memsiez) {
		this.memsiez = memsiez;
	}


	public boolean isUserTask() {
		return isUserTask;
	}


	public void setUserTask(boolean isUserTask) {
		this.isUserTask = isUserTask;
	}


	public boolean isCheck() {
		return check;
	}


	public void setCheck(boolean check) {
		this.check = check;
	}


	@Override
	public String toString() {
		return "TackInfo [icon=" + icon + ", name=" + name + ", packageName="
				+ packageName + ", memsiez=" + memsiez + ", isUserTask="
				+ isUserTask + "]";
	}
	
}
