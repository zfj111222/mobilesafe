package com.zfj123.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.zfj123.mobilesafe.R.color;
import com.zfj123.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {
	
	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private TextView tv_update_info;
	private SharedPreferences sp;
	//TODO
	private String version;
	private String description;
	private String apkurl;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG://显示升级的对话框
				Log.i(TAG, "显示升级的对话框");
				showUpdateDialog();
				break;
				
			case ENTER_HOME://进入主页面
				enterHome();
				break;
			
			case URL_ERROR://URL错误
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();
				break;
			
			case NETWORK_ERROR://网络错误
				enterHome();
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				break;
				
			case JSON_ERROR://JSON错误
				enterHome();
				Toast.makeText(getApplicationContext(), "JSON错误", 0).show();
				break;
				
			default:
				break;
			}
		}

	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp=getSharedPreferences("config", MODE_PRIVATE);
        boolean update = sp.getBoolean("update", false);
        tv_splash_version=(TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号"+getVersionName());
        tv_update_info=(TextView) findViewById(R.id.tv_update_info);
        tv_update_info.setVisibility(View.INVISIBLE);
        //创建快捷图标
        installShortCut();
        
        //拷贝数据库
        copyDB();
        
        //检查升级
        if(update){
        	//检查升级
        	checkUpdate();
        }else{
        	//自动升级已经关闭
        	handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					//进入主页面
					enterHome();
				}
			}, 2000);
        	
        }
        AlphaAnimation aa=new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(1000);
        findViewById(R.id.rl_root_splash).startAnimation(aa);
    }
    
    /**
     * 创建快捷图标
     */
    private void installShortCut() {
    	boolean shortcut = sp.getBoolean("shortcut", false);
    	if(shortcut){
    	Editor editor = sp.edit();
    	//发送广播的意图，告诉桌面创建图标
		Intent intent=new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//快捷方式 要包含三个重要的信息
		//1.名称 2.图标 3.干什么事
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		//桌面点击图标对应的意图
		Intent shortcutIntent=new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName("com.zfj123.mobilesafe", "com.zfj123.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
    	}
	}

	/**
     * path 把address.db这个数据库到:data/data/com.zfj123.mobilesafe/files/address.db
     */
    private void copyDB() {
    	//只要你拷贝了一次，我们就不要你拷贝了
    	InputStream is;
			File file=new File(getFilesDir(), "address.db");
			if(file.exists()&&file.length()>0){
				//正常了，不需要拷贝了
				Log.i(TAG, "不需要拷贝了");
			}else{
				Log.i(TAG, "拷贝数据库");
				try {
				is=getAssets().open("address.db");
				FileOutputStream fos=new FileOutputStream(file);
				byte[] buffer=new byte[1024];
				int len=0;
				while((len=(is.read(buffer)))!=-1){
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("数据库没有");
				e.printStackTrace();
			}
			}
			
	}

	/**
	 * 弹出升级对话框
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("升级提醒");
//		builder.setCancelable(false);//强制升级
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//进入主页面
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setMessage(description);
		builder.setPositiveButton("立刻升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载APK，并且替换安装
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					//sdcard存在
					//afinal
					FinalHttp finalHttp=new FinalHttp();
					finalHttp.download(apkurl, Environment.
							getExternalStorageDirectory()+"/mobilesafe.apk", 
							new AjaxCallBack<File>() {

								@Override
								public void onFailure(Throwable t, int errorNo,
										String strMsg) {
									t.printStackTrace();
									Toast.makeText(getApplicationContext(), "下载失败", 0).show();
									super.onFailure(t, errorNo, strMsg);
								}

								@Override
								public void onLoading(long count, long current) {
									//当前下载百分比
									tv_update_info.setVisibility(View.VISIBLE);
									int progress=(int) (current*100/count);
									tv_update_info.setText("下载进度"+progress+"%");
									super.onLoading(count, current);
								}

								@Override
								public void onSuccess(File t) {
									installAPK(t);
									super.onSuccess(t);
								}

								/**
								 * 安装apk
								 */
								private void installAPK(File t) {
									Intent intent=new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t), 
											"application/vnd.android.package-archive");
									startActivity(intent);
								}
							});
				}else{
					Toast.makeText(getApplicationContext(), "没有sd卡，请安装后再试", 0).show();
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
	}

	private void enterHome() {
		Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
	};
    
    /**
     * 检查是否有新版本，如果有就升级
     */
    private void checkUpdate() {
		new Thread(){

			public void run() {
				long startTime=System.currentTimeMillis();
				Message mes=Message.obtain();
				try {
					//URL
					URL url=new URL(getString(R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code=conn.getResponseCode();
					if(code==200){
						//联网成功
						InputStream is = conn.getInputStream();
						//把流转成String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "得到的字符串:"+result);
						//json解析
						JSONObject obj=new JSONObject(result);
						version = (String) obj.get("version");
						description = obj.getString("description");
						apkurl = obj.getString("apkurl");
						//校验是否有新版本
						if(getVersionName().equals(version)){
							//版本一致，没有新版本，进入主页面
							mes.what=ENTER_HOME;
						}else{
							//有新版本，弹出升级对话框
							mes.what=SHOW_UPDATE_DIALOG;
						}
					}
				} catch(MalformedURLException e){
					e.printStackTrace();
					mes.what=URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					mes.what=NETWORK_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					mes.what=JSON_ERROR;
				}finally{
					long endTime=System.currentTimeMillis();
					//我们花了多少时间
					long dTime=endTime-startTime;
					//2000
					if(dTime<2000){
						try {
							Thread.sleep(2000-dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(mes);
				}
				
			};
		}.start();
	}

	/**
     * 得到应用程序的版本名称
     */
    private String getVersionName(){
    	//用来管理手机的apk
    	PackageManager pm=getPackageManager();
    	try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			String version=info.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	return "";
    }
}
