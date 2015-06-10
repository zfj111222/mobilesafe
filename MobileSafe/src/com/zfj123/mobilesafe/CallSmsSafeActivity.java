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
		//listviewע�����������
		lv_call_sms_safe.setOnScrollListener(new OnScrollListener() {
			
			/**
			 * ��������״̬�����仯ʱ����
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				//���Ի���״̬
				case OnScrollListener.SCROLL_STATE_FLING:
//					System.out.println("���Ի���״̬");
					break;

				//��ָ��������
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//					System.out.println("��ָ��������״̬");
					break;

				//����״̬
				case OnScrollListener.SCROLL_STATE_IDLE:
//					System.out.println("����״̬");
					// ��ȡ���һ���ɼ���Ŀ�ڼ������λ��
					int lastPosition = view.getLastVisiblePosition();
					if(lastPosition==(infos.size()-1)){
						System.out.println("�б��ƶ������һ��λ�ã����ظ�������");
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
			 * ������ʱ�����
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
				}else{ //�Ѿ����ع�������
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
					Toast.makeText(getApplicationContext(), "����������Ϊ��", 1).show();
					return;
				}
				String mode="3";
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//ȫ������
					mode="3";
				}else if(cb_phone.isChecked()){
					//�ֻ�����
					mode="1";
				}else if(cb_sms.isChecked()){
					//��������
					mode="2";
				}else{
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", 1).show();
					return;
				}
				//���ݼ��ص����ݿ�
				dao.add(blacknumber, mode);
				//����listview�����������
				BlackNumberInfo info=new BlackNumberInfo();
				info.setNumber(blacknumber);
				info.setMode(mode);
				infos.add(0, info);
				//֪ͨlistview���������ݷ����仯
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
		 * �ж�����Ŀ����ʾ���ͱ����ö��ٴ�
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view ;
			final ViewHolder viewHolder;
			if(convertView==null){
//				Log.i(TAG, "����view��posution:"+position);
				view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
				viewHolder=new ViewHolder();
				viewHolder.tv_black_number=(TextView) view.findViewById(R.id.tv_black_number);
				viewHolder.tv_black_mode=(TextView) view.findViewById(R.id.tv_black_mode);
				viewHolder.iv_delete=(ImageView) view.findViewById(R.id.iv_delete);
				//�����ӳ�����ʱ���ҵ����ǵ����ã�����ڼ��±������ڿڴ���
				view.setTag(viewHolder);
			}else{
//				Log.i(TAG, "����view��posution:"+position);
				view=convertView;
				viewHolder=(ViewHolder) convertView.getTag();
			}
			viewHolder.tv_black_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if("1".equals(mode)){
				viewHolder.tv_black_mode.setText("�绰����");
			}else if("2".equals(mode)){
				viewHolder.tv_black_mode.setText("��������");
			}else {
				viewHolder.tv_black_mode.setText("ȫ��������");
			}
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					Toast.makeText(CallSmsSafeActivity.this, viewHolder.tv_black_number.getText().toString(), 0).show();
					AlertDialog.Builder builder=new Builder(CallSmsSafeActivity.this);
					builder.setTitle("����");
					builder.setMessage("��ȷ��Ҫɾ��������¼��?");
					builder.setPositiveButton("ȷ��", new AlertDialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//ɾ�����ݿ������
							dao.delete(infos.get(position).getNumber());
							//���½���
							infos.remove(position);
							//֪ͨlistview��������������
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.show();
				}
			});
			return view;
		}
		
		/**
		 *view���������
		 *��¼���ӵ��ڴ��ַ
		 *�൱��һ�����±�
		 */
		private class ViewHolder{
			private TextView  tv_black_number;
			private TextView tv_black_mode;
			private ImageView iv_delete;
		}
	}
	
	
}
