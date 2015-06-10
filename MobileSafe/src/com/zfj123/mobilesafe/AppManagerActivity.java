package com.zfj123.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.zfj123.mobilesafe.bean.AppInfo;
import com.zfj123.mobilesafe.db.dao.AppLockDao;
import com.zfj123.mobilesafe.engine.AppInfoProvider;
import com.zfj123.mobilesafe.utils.DensityUtils;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "AppManagerActivity";
	/**
	 * ж�س���
	 */
	private LinearLayout ll_uninstall;
	/**
	 * ��������
	 */
	private LinearLayout ll_start;
	/**
	 * �������
	 */
	private LinearLayout ll_share;
	
	/**
	 * ���������Ŀ
	 */
	private AppInfo appInfo;
	
	private AppLockDao dao;
	
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	private TextView tv_status;
	private AppManagerAdapter adapter;
	/**
	 * ���е�Ӧ�ó������Ϣ
	 */
	private List<AppInfo> appInfos;
	
	/**
	 * �û�Ӧ�ó�����Ϣ
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * ϵͳӦ�ó�����Ϣ
	 */
	private List<AppInfo> systemAppInfos;
	/**
	 * ��������������
	 */
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		dao=new AppLockDao(this);
		tv_avail_rom=(TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd=(TextView) findViewById(R.id.tv_avail_sd);
		lv_app_manager=(ListView) findViewById(R.id.lv_app_manager);
		tv_status=(TextView) findViewById(R.id.tv_status);
		tv_status.setVisibility(View.INVISIBLE);
		ll_loading=(LinearLayout) findViewById(R.id.ll_loading);
		long sdSize=getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long romSiez=getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
		tv_avail_sd.setText("SD�����ÿռ䣺"+Formatter.formatFileSize(this, sdSize));
		tv_avail_rom.setText("�ڴ���ÿռ䣺"+Formatter.formatFileSize(this, romSiez));
		
		fillData();
		//��listviewע�����������
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				//������ʱ����õķ���
				//1.firstVisibleItem ��һ���ɼ���Ŀ��listview�������λ��
				if(userAppInfos!=null&&systemAppInfos!=null){
					tv_status.setVisibility(View.VISIBLE);
				if(firstVisibleItem>userAppInfos.size()){
					tv_status.setText("ϵͳ����"+systemAppInfos.size()+")");
				}else{
					tv_status.setText("�û�����"+userAppInfos.size()+")");
				}
				}
			}
		});
		
		//����listview�ĵ���¼�
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0){
					return;
				}else if(position==userAppInfos.size()+1){
					return;
				}else if(position<=userAppInfos.size()){//�û�����
					int newposition=position-1;
					appInfo=userAppInfos.get(newposition);
				}else{//ϵͳ����
					int newposition=position-1-userAppInfos.size()-1;
					appInfo=systemAppInfos.get(newposition);
				}
				dismissPopupWindow();
				View contentView=View.inflate(getApplicationContext(),
						R.layout.popup_app_item, null);
				ll_uninstall=(LinearLayout) contentView.findViewById(R.id.ll_uninstall);
				ll_start=(LinearLayout) contentView.findViewById(R.id.ll_start);
				ll_share=(LinearLayout) contentView.findViewById(R.id.ll_share);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, -2);
				//����Ч���Ĳ��ű���Ҫ���б�����ɫ
				//͸��ɫҲ����ɫ
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				int [] location=new int[2];
				view.getLocationInWindow(location);
				int dip=60;
				int px = DensityUtils.dip2px(getApplicationContext(), dip);
				System.out.println("px="+px);
				popupWindow.showAtLocation(parent, 
						Gravity.LEFT|Gravity.TOP, location[0]+px, location[1]);
				ScaleAnimation sa=new ScaleAnimation(
						0.3f, 1.0f, 0.3f, 1.0f, 
						Animation.RELATIVE_TO_SELF, 0.0f, 
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				AlphaAnimation aa=new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);
				AnimationSet set=new AnimationSet(false);
				set.addAnimation(sa);
				set.addAnimation(aa);
				contentView.startAnimation(set);
			}
		});
		
		//������ ������Ŀ�����������
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0){
					return true;
				}else if(position==userAppInfos.size()+1){
					return true;
				}else if(position<=userAppInfos.size()){//�û�����
					int newposition=position-1;
					appInfo=userAppInfos.get(newposition);
				}else{//ϵͳ����
					int newposition=position-1-userAppInfos.size()-1;
					appInfo=systemAppInfos.get(newposition);
				}
				System.out.println("�����:"+appInfo.getName());
				HolderView holderView = (HolderView) view.getTag();
				//�ж���Ŀ�Ƿ���ڳ��������ݿ�����
				if(dao.find(appInfo.getPackName())){
					//���������򣬽�����������½���Ϊ�򿪵�С��ͼƬ
					holderView.iv_status.setImageResource(R.drawable.lock);
					System.out.println(appInfo.getName()+"����");
					dao.delete(appInfo.getPackName());
				}else{
					//�������򣬸��½���Ϊ�رյ���
					holderView.iv_status.setImageResource(R.drawable.unlock);
					System.out.println(appInfo.getName()+"����");
					dao.add(appInfo.getPackName());
				}
				adapter.notifyDataSetChanged();
				return true;
			}
		});
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				appInfos=AppInfoProvider.getAppInfos(getApplicationContext());
				userAppInfos=new ArrayList<AppInfo>();
				systemAppInfos=new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if(appInfo.isUserAPP()){
						userAppInfos.add(appInfo);
					}else{
						systemAppInfos.add(appInfo);
					}
				}
				//����listview������������
				runOnUiThread(new  Runnable() {
					public void run() {
						if (adapter == null) {
							adapter=new AppManagerAdapter();
							lv_app_manager.setAdapter(adapter);
							ll_loading.setVisibility(View.INVISIBLE);
						}else{
							adapter.notifyDataSetChanged();
							ll_loading.setVisibility(View.INVISIBLE);
						}
					}
				});
			};
		}.start();
	}
	
	private class AppManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return userAppInfos.size()+systemAppInfos.size()+2;
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
			AppInfo appInfo;
			if(position==0){
				TextView tv=new TextView(getApplicationContext());
				tv.setText("�û�����"+userAppInfos.size()+")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}else if(position==userAppInfos.size()+1){
				TextView tv=new TextView(getApplicationContext());
				tv.setText("ϵͳ����"+systemAppInfos.size()+")");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}else if(position<=userAppInfos.size()){
				//�û�����
				int newposition=position-1;//��Ϊ����һ��textview�ı�
				appInfo=userAppInfos.get(newposition);
			}else{
				//ϵͳ����
				int newposition=position-1-userAppInfos.size()-1;
				appInfo=systemAppInfos.get(newposition);
			}
			View view;
			HolderView holderView;
//			if(position<userAppInfos.size()){
//				//��Щλ�������û�������ʾ
//				appInfo=userAppInfos.get(position);
//			}else{
//				//��Щλ������ϵͳ������ʾ
//				int newposition=position-userAppInfos.size();
//				appInfo=systemAppInfos.get(newposition);
//			}
			if(convertView!=null&&convertView instanceof RelativeLayout){
				//������Ҫ����Ƿ�Ϊ�գ���Ҫ�ж��Ƿ��Ǻ��ʵ����Ͳ�ȥ����
				view=convertView;
				holderView=(HolderView) convertView.getTag();
			}else{
				view=View.inflate(getApplicationContext(), R.layout.list_item_appinfo, null);
				holderView=new HolderView();
				holderView.iv_app_icon=(ImageView) view.findViewById(R.id.iv_app_icon);
				holderView.tv_app_name=(TextView) view.findViewById(R.id.tv_app_name);
				holderView.tv_app_location=(TextView) view.findViewById(R.id.tv_app_location);
				holderView.iv_status=(ImageView) view.findViewById(R.id.iv_status);
				view.setTag(holderView);
			}
			holderView.iv_app_icon.setImageDrawable(appInfo.getIcon());
			holderView.tv_app_name.setText(appInfo.getName());
			if(appInfo.isInRom()){
				holderView.tv_app_location.setText("�ֻ��ڴ�");
			}else{
				holderView.tv_app_location.setText("�ֻ��ⲿ�洢");
			}
//			holderView.iv_status.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					System.out.println("������");
//				}
//			});
			if(dao.find(appInfo.getPackName())){
				holderView.iv_status.setImageResource(R.drawable.lock);
			}else{
				holderView.iv_status.setImageResource(R.drawable.unlock);
			}
			return view;
		}
		
	}
	
	static class HolderView{
		ImageView iv_app_icon;
		TextView tv_app_name;
		TextView tv_app_location;
		ImageView iv_status;
	}
	
	/**
	 * ��ȡĳ��Ŀ¼�Ŀ��ÿռ�
	 * @param path
	 * @return
	 */
	public long getAvailSpace(String path){
		StatFs statFs=new StatFs(path);
		//��ȡ�����ĸ���
		statFs.getBlockCount();
		//��ȡ�����Ĵ�С
		int size = statFs.getBlockSize();
		//��ȡ���õķ�������
		int count = statFs.getAvailableBlocks();
		return size*count;
	}
	
	private void dismissPopupWindow() {
		if(popupWindow!=null&&popupWindow.isShowing()){
			//�Ѿõĵ�������ر�
			popupWindow.dismiss();
			popupWindow=null;
		}
	}
	
	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}

	/**
	 * ���ֶ�Ӧ�ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_uninstall:
//			Log.i(TAG, "ж�أ�"+appInfo.getName());
			if(appInfo.isUserAPP()){
				uninstallApplication();
			}else{
				Toast.makeText(getApplicationContext(), "ϵͳӦ��ֻ��rootȨ�޲���ж��", 0).show();
//				Runtime.getRuntime().exec("�����д���");
			}
			break;

		case R.id.ll_start:
//			Log.i(TAG, "������"+appInfo.getName());
			startApplication();
			break;

		case R.id.ll_share:
//			Log.i(TAG, "����"+appInfo.getName());
			shareApplication();
			break;

		default:
			break;
		}
	}

	/**
	 * ����һ��Ӧ�ó���
	 */
	private void shareApplication() {
		Intent intent=new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ������������ǣ�"+appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ж��Ӧ��
	 */
	private void uninstallApplication() {
//		<action android:name="android.intent.action.VIEW"/> 
//		<action android:name="android.intent.action.DELETE"/> 
//		<category android:name="android.intent.category.DEFAULT"/> 
//		<data android:scheme="package"/>
		Intent intent=new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appInfo.getPackName()));
		startActivityForResult(intent, 0);
	}

	/**
	 * ����һ��Ӧ�ó���
	 */
	private void startApplication() {
		//��ѯ�����������activity
		PackageManager pm=getPackageManager();
//		Intent intent=new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
		//��ѯ���������ֻ��Ͼ�������������activity
//		List<ResolveInfo> infos = pm.queryIntentActivities(intent,PackageManager.GET_INTENT_FILTERS);
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackName());
		if(intent!=null){
			startActivity(intent);
		}else{
			Toast.makeText(getApplicationContext(), "�Բ����޷�������Ӧ��", 1).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
