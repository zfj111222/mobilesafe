package com.zfj123.mobilesafe;


import java.util.ArrayList;
import java.util.List;

import com.zfj123.mobilesafe.bean.TaskInfo;
import com.zfj123.mobilesafe.engine.TaskInfoProvider;
import com.zfj123.mobilesafe.utils.SystemInfoUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity {
	
	private TextView tv_process_count;
	private TextView tv_men_info;
	private LinearLayout ll_loading;
	private ListView lv_task_manager;
	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	private TaskManagerAdapter adapter;
	private TextView tv_status;
	private int procee;
	private long availMem;
	private long totalMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_process_count=(TextView) findViewById(R.id.tv_process_count);
		tv_men_info=(TextView) findViewById(R.id.tv_men_info);
		ll_loading=(LinearLayout) findViewById(R.id.ll_loading);
		lv_task_manager=(ListView) findViewById(R.id.lv_task_manager);
		tv_status=(TextView) findViewById(R.id.tv_status);
		setTitle();
		fillData();
		lv_task_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(userTaskInfos!=null&&systemTaskInfos!=null){
					if(firstVisibleItem>userTaskInfos.size()){
						tv_status.setText("ϵͳ����:"+systemTaskInfos.size());
					}else{
						tv_status.setText("�û�����:"+userTaskInfos.size());
					}
				}
			}
		});
		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskInfo;
				if(position==0){//�û����̱�ǩ
					return;
				}else if(position==(userTaskInfos.size()+1)){//ϵͳӦ�ó����ǩ
					return;
				}else if(position<=userTaskInfos.size()){
					taskInfo=userTaskInfos.get(position-1);
				}else {
					taskInfo=systemTaskInfos.get(position-1-userTaskInfos.size()-1);
				}
				if (getPackageName().equals(taskInfo.getPackageName())) {
					return;
				}
				HodlerView hodlerView=(HodlerView) view.getTag();
				if(taskInfo.isCheck()){
					taskInfo.setCheck(false);
					hodlerView.cb_status.setChecked(false);
				}else{
					taskInfo.setCheck(true);
					hodlerView.cb_status.setChecked(true);
				}
			}
		});
	}

	private void setTitle() {
		procee = SystemInfoUtils.getProcessCount(this);
		availMem = SystemInfoUtils.getAvailMem(this);
		totalMem = SystemInfoUtils.getTotalMem(this);
		tv_process_count.setText("�����еĽ���:"+procee+"��");
		tv_men_info.setText("ʣ��/���ڴ�:"+
		Formatter.formatFileSize(this, availMem)+"/"+
				Formatter.formatFileSize(this, totalMem));
	}

	/**
	 * listview�������
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
				userTaskInfos=new ArrayList<TaskInfo>();
				systemTaskInfos=new ArrayList<TaskInfo>();
				for (TaskInfo info : allTaskInfos) {
					if(info.isUserTask()){
						userTaskInfos.add(info);
					}else{
						systemTaskInfos.add(info);
					}
				}
				//���½���
				runOnUiThread( new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter==null){
							adapter=new TaskManagerAdapter();
							lv_task_manager.setAdapter(adapter);
						}else{
							lv_task_manager.setAdapter(adapter);
						}
						setTitle();
					}
				});
			};
		}.start();
	}
	
	private class TaskManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			SharedPreferences sp=getSharedPreferences("config", MODE_PRIVATE);
			if(sp.getBoolean("showsystem", false)){
				return userTaskInfos.size()+1+systemTaskInfos.size()+1;
			}
			else{
				return userTaskInfos.size()+1;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo taskInfo;
			if(position==0){//�û����̱�ǩ
				TextView tv=new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û����̣�"+userTaskInfos.size()+"��");
				return tv;
			}else if(position==(userTaskInfos.size()+1)){//ϵͳӦ�ó����ǩ
				TextView tv=new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ���̣�"+systemTaskInfos.size()+"��");
				return tv;
			}else if(position<=userTaskInfos.size()){
				taskInfo=userTaskInfos.get(position-1);
			}else {
				taskInfo=systemTaskInfos.get(position-1-userTaskInfos.size()-1);
			}
			View view;
			HodlerView hodlerView;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view=convertView;
				hodlerView=(HodlerView) convertView.getTag();
			}else{
				view=View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
				hodlerView=new HodlerView();
				hodlerView.iv_icon=(ImageView) view.findViewById(R.id.iv_task_icon);
				hodlerView.tv_name=(TextView) view.findViewById(R.id.tv_task_name);
				hodlerView.tv_memsize=(TextView) view.findViewById(R.id.tv_task_memsize);
				hodlerView.cb_status=(CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(hodlerView);
			}
			hodlerView.iv_icon.setImageDrawable(taskInfo.getIcon());
			hodlerView.tv_name.setText(taskInfo.getName());
			hodlerView.tv_memsize.setText("�ڴ�ռ��"+Formatter.formatFileSize(getApplicationContext()
					, taskInfo.getMemsiez()));
			hodlerView.cb_status.setChecked(taskInfo.isCheck());
			if (getPackageName().equals(taskInfo.getPackageName())) {
				hodlerView.cb_status.setVisibility(View.GONE);
			}else{
				hodlerView.cb_status.setVisibility(View.VISIBLE);
			}
			return view;
		}
		
	}
	
	class HodlerView{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memsize;
		CheckBox cb_status;
	}
	
	/**
	 *ѡ��ȫ�� 
	 */
	public void selectAll(View v){
		for (TaskInfo info : allTaskInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setCheck(true);
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 *ѡ���෴
	 */
	public void selectOppo(View v){
		for (TaskInfo info : allTaskInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setCheck(!info.isCheck());
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 *һ������
	 */
	public void killAll(View v){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count=0;
		long saveMem=0;
		//��¼��ɱ������Ŀ
		List<TaskInfo> killedInfo=new ArrayList<TaskInfo>();
		for (TaskInfo info : allTaskInfos) {
			if(info.isCheck()){//����ѡ�ģ�ɱ������
				am.killBackgroundProcesses(info.getPackageName());
				if (info.isUserTask()) {
					userTaskInfos.remove(info);
				}else{
					systemTaskInfos.remove(info);
				}
				killedInfo.add(info);
				count++;
				saveMem+=info.getMemsiez();
			}
		}
		allTaskInfos.retainAll(killedInfo);
		adapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), 
				"ɱ���ˣ�"+count+"�����̣��ͷ��ˣ�"+
		Formatter.formatFileSize(getApplicationContext(), saveMem)+"m�ڴ�", 1).show();
		procee-=count;
		availMem+=saveMem;
		tv_process_count.setText("�����еĽ���:"+procee+"��");
		tv_men_info.setText("ʣ��/���ڴ�:"+
		Formatter.formatFileSize(this, availMem)+"/"+
				Formatter.formatFileSize(this, totalMem));
	}
	
	/**
	 *��������
	 */
	public void enterSetting(View v){
		Intent intent=new Intent(this,TaskSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		adapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
