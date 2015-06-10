package com.zfj123.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zfj123.mobilesafe.bean.BlackNumberInfo;
import com.zfj123.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsSafeActivity extends Activity {
	
	public static final String TAG = "CallSmsSafeActivity";
	private ListView lv_call_sms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	
	private EditText et_black_number;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	
	private CallSmaSafeAdapter adapter;
	
	private LinearLayout ll_loading;
	private int offset=0;
	private int maxNumber=20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		ll_loading=(LinearLayout) findViewById(R.id.ll_loading);
		lv_call_sms_safe=(ListView) findViewById(R.id.lv_call_sms_safe);
		dao=new BlackNumberDao(this);
		ll_loading.setVisibility(View.VISIBLE);
		fillData();
		//listview注册滚动监听器
		lv_call_sms_safe.setOnScrollListener(new OnScrollListener() {
			
			/**
			 * 当滚动的状态发生变化时调用
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				//惯性滑行状态
				case OnScrollListener.SCROLL_STATE_FLING:
//					System.out.println("惯性滑行状态");
					break;

				//手指触摸滚动
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//					System.out.println("手指触摸滚动状态");
					break;

				//空闲状态
				case OnScrollListener.SCROLL_STATE_IDLE:
//					System.out.println("空闲状态");
					// 获取最后一个可见条目在集合里的位置
					int lastPosition = view.getLastVisiblePosition();
					if(lastPosition==(infos.size()-1)){
						System.out.println("列表被移动到最后一个位置，加载更多数据");
						offset+=maxNumber;
						ll_loading.setVisibility(View.VISIBLE);
						fillData();
					}
					break;

				default:
					break;
				}
			}
			
			/**
			 * 滚动的时候调用
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}

	private void fillData() {
		new Thread(){
			public void run() {
				if(infos==null){
					infos=dao.findPart(offset, maxNumber);
				}else{ //已经加载过数据了
					infos.addAll(dao.findPart(offset, maxNumber));
				}
				runOnUiThread( new Runnable() {
					public void run() {
						if(adapter==null){
							ll_loading.setVisibility(View.INVISIBLE);
							adapter=new  CallSmaSafeAdapter();
							lv_call_sms_safe.setAdapter(adapter);
						}else{
							ll_loading.setVisibility(View.INVISIBLE);
							adapter.notifyDataSetChanged();
						}
						
					}
				});
			};
		}.start();
	}
	
	public void addBlackNumber(View v){
		AlertDialog.Builder builder=new Builder(this);
		final AlertDialog dialog=builder.create();
		View view=View.inflate(this, R.layout.dialog_add_black_number, null);
		et_black_number=(EditText) view.findViewById(R.id.et_black_number);
		cb_phone=(CheckBox) view.findViewById(R.id.cb_phone);
		cb_sms=(CheckBox) view.findViewById(R.id.cb_sms);
		bt_ok=(Button) view.findViewById(R.id.ok);
		bt_cancel=(Button) view.findViewById(R.id.cancel);
		dialog.setView(view,0,0,0,0);
		dialog.show();
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String blacknumber = et_black_number.getText().toString().trim();
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplicationContext(), "黑名单号码为空", 1).show();
					return;
				}
				String mode="3";
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//全部拦截
					mode="3";
				}else if(cb_phone.isChecked()){
					//手机拦截
					mode="1";
				}else if(cb_sms.isChecked()){
					//短信拦截
					mode="2";
				}else{
					Toast.makeText(getApplicationContext(), "请选择拦截模式", 1).show();
					return;
				}
				//数据加载到数据库
				dao.add(blacknumber, mode);
				//更新listview集合里的内容
				BlackNumberInfo info=new BlackNumberInfo();
				info.setNumber(blacknumber);
				info.setMode(mode);
				infos.add(0, info);
				//通知listview适配器数据发生变化
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}
	
	private class CallSmaSafeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		/**
		 * 有多少条目被显示，就被调用多少次
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view ;
			final ViewHolder viewHolder;
			if(convertView==null){
//				Log.i(TAG, "创建view，posution:"+position);
				view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
				viewHolder=new ViewHolder();
				viewHolder.tv_black_number=(TextView) view.findViewById(R.id.tv_black_number);
				viewHolder.tv_black_mode=(TextView) view.findViewById(R.id.tv_black_mode);
				viewHolder.iv_delete=(ImageView) view.findViewById(R.id.iv_delete);
				//当孩子出生的时候找到他们的引用，存放在记事本，放在口袋里
				view.setTag(viewHolder);
			}else{
//				Log.i(TAG, "父用view，posution:"+position);
				view=convertView;
				viewHolder=(ViewHolder) convertView.getTag();
			}
			viewHolder.tv_black_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if("1".equals(mode)){
				viewHolder.tv_black_mode.setText("电话拦截");
			}else if("2".equals(mode)){
				viewHolder.tv_black_mode.setText("短信拦截");
			}else {
				viewHolder.tv_black_mode.setText("全部话拦截");
			}
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					Toast.makeText(CallSmsSafeActivity.this, viewHolder.tv_black_number.getText().toString(), 0).show();
					AlertDialog.Builder builder=new Builder(CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setMessage("你确认要删除这条记录吗?");
					builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//删除数据库的内容
							dao.delete(infos.get(position).getNumber());
							//更新界面
							infos.remove(position);
							//通知listview数据适配器更新
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			return view;
		}
		
		/**
		 *view对象的容器
		 *记录孩子的内存地址
		 *相当于一个记事本
		 */
		private class ViewHolder{
			private TextView  tv_black_number;
			private TextView tv_black_mode;
			private ImageView iv_delete;
		}
	}
	
	
}
