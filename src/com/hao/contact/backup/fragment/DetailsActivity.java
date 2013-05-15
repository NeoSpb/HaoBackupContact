package com.hao.contact.backup.fragment;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.hao.contact.backup.R;
import com.hao.contact.backup.WflAdapter;
import com.hao.contact.backup.model.ContactHandler;
import com.hao.contact.backup.model.ContactInfo;

public class DetailsActivity extends Activity {
	List<ContactInfo> allConatcts = null;
	private ContactHandler mContactHandler;
	protected ProgressDialog m_pDialog;
	private final static int SHOW_PROGRESS_DIALOG = 0;
	private final static int DISMISS_PROGRESS_DIALOG = 1;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PROGRESS_DIALOG:
				showProgressDialog();
				break;
			case DISMISS_PROGRESS_DIALOG:
				m_pDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bakcup_fragment_layout);
		// 获取联系人处理实例
		String path = getIntent().getExtras().getString("path");
		Log.i("wu0wu", "path=" + path);
		mContactHandler = ContactHandler.getInstance();
		try {
			allConatcts = mContactHandler.restoreContacts(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Button btnBackup = (Button) findViewById(R.id.backup_btn);
		btnBackup.setText("恢复");
		btnBackup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
				restoreContacts();
				// m_pDialog.dismiss();
			}
		});
		
		ListView list = (ListView) findViewById(R.id.backup_list);
		final WflAdapter adapter = new WflAdapter(this, allConatcts);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				allConatcts.get(arg2).isSelected = !allConatcts.get(arg2).isSelected;
				adapter.notifyDataSetChanged();
			}
		});
	}

	protected void showProgressDialog() {
		// 创建ProgressDialog对象
		m_pDialog = new ProgressDialog(this);
		// 设置进度条风格，风格为圆形，旋转的
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 标题
		m_pDialog.setTitle("提示");
		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage("正在备份中...");
		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(true);
		// 让ProgressDialog显示
		m_pDialog.show();
	}

	private void restoreContacts() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for (int i = 0; i < allConatcts.size(); i++) {
					if (allConatcts.get(i).isSelected) {
						Log.i("wu0wu", "restore name="+allConatcts.get(i).getName());
						mContactHandler.addContacts(DetailsActivity.this, allConatcts.get(i));
					}
				}
				Message msg = new Message();
				msg.what = DISMISS_PROGRESS_DIALOG;
				mHandler.sendMessageDelayed(msg, 2000);
			}
		}).start();
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}