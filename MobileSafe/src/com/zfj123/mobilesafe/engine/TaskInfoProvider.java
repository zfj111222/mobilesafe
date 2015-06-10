package com.zfj123.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.zfj123.mobilesafe.R;
import com.zfj123.mobilesafe.bean.TaskInfo;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

/**
 *�ṩ�ֻ�����Ľ�����Ϣ
 */
public class TaskInfoProvider {

	/**
	 * ��ȡ���еĽ�����Ϣ
	 * @param context ������
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context){
		List<TaskInfo> taskInfos=new ArrayList<TaskInfo>();
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		System.out.println("infos��size"+infos.size());
		for (RunningAppProcessInfo info : infos) {
			TaskInfo taskInfo=new TaskInfo();
			String packname=info.processName;
			taskInfo.setPackageName(packname);
			MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			long memsize=memoryInfo[0].getTotalPrivateDirty()*1024;
			taskInfo.setMemsiez(memsize);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);
				if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
					//�û�����
					taskInfo.setUserTask(true);
				}else{
					//ϵͳ����
					taskInfo.setUserTask(false);
				}
				taskInfos.add(taskInfo);
			} catch (Exception e) {
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				taskInfo.setName(packname);
			}
		}
		return taskInfos;
	}
}
