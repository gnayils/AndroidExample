package com.itheima.safeguard;

import java.util.ArrayList;
import java.util.List;

import com.itheima.safeguard.data.C;
import com.itheima.safeguard.entity.TaskInfo;
import com.itheima.safeguard.utils.SystemTool;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TaskManagerActivity extends Activity {

	private TextView tv_proc_count_in_running;
	private TextView tv_memory_info;
	private ListViewDelegate lvd;
	private SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_task_manager);		
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));
		this.prefs = this.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		this.tv_proc_count_in_running = (TextView) this.findViewById(R.id.tv_proc_count_in_running);
		this.tv_memory_info = (TextView) this.findViewById(R.id.tv_memory_info);
		this.lvd = new ListViewDelegate();
		refreshInfo();
	}
	
	private void refreshInfo() {
		this.tv_proc_count_in_running.setText("运行中的进程：" + SystemTool.getRunningAppProcessList(this).size());
		MemoryInfo mi = SystemTool.getMemoryInfo(this);
		this.tv_memory_info.setText("系统内存：" +Formatter.formatFileSize(this, mi.availMem) + "/" + Formatter.formatFileSize(this, mi.totalMem));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		 refreshInfo() ;
		 lvd.refreshAdapter();
	}
	
	private class ListViewDelegate {
		private ListView lv_task_info;
		private LinearLayout ll_loading_mask;
		private TextView tv_title;
		private MyListViewAdapter adapter;
		
		public ListViewDelegate() {
			this.lv_task_info = (ListView) findViewById(R.id.lv_task_info);
			this.ll_loading_mask = (LinearLayout) findViewById(R.id.ll_loading_mask);
			this.tv_title = (TextView) findViewById(R.id.tv_title);
			this.adapter = new MyListViewAdapter();
			this.lv_task_info.setAdapter(adapter);
			this.lv_task_info.setOnScrollListener(new MyOnScrollListener());
			this.lv_task_info.setOnItemClickListener(new MyOnItemClickListener());
		}
		
		public void refreshAdapter() {
			this.adapter.loadData();
		}
		
		private class MyOnItemClickListener implements OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo ti = (TaskInfo) adapter.getItem(position);
				if(ti == null || getPackageName().equals(ti.getPackageName())) {
					return ;
				}
				ViewHolder vh = (ViewHolder) view.getTag();
				ti.setChecked(!ti.isChecked());
				vh.checkbox.setChecked(ti.isChecked());
			}
		}
		
		private class MyOnScrollListener implements  OnScrollListener{

			@Override
			public void onScrollStateChanged(AbsListView view,
					int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem > adapter.userTaskInfoList.size()) {
					tv_title.setText("系统进程：" + adapter.sysTaskInfoList.size());
				} else {
					tv_title.setText("用户进程：" + adapter.userTaskInfoList.size());
				}
			}
		}
		
		private class MyListViewAdapter extends BaseAdapter {
			
			private List<TaskInfo> tiList = new ArrayList<TaskInfo>();
			private List<TaskInfo> userTaskInfoList = new ArrayList<TaskInfo>();
			private List<TaskInfo> sysTaskInfoList = new ArrayList<TaskInfo>();
			
			public MyListViewAdapter() {
				loadData();
			}
			
			private void loadData() {
				new Thread(){
					
					@Override
					public void run() {
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								ll_loading_mask.setVisibility(View.VISIBLE);
								lv_task_info.setVisibility(View.INVISIBLE);
							}
							
						});
						tiList.clear();
						userTaskInfoList.clear();
						sysTaskInfoList.clear();
						tiList.addAll(SystemTool.getTaskInfo(getBaseContext()));
						for(TaskInfo ti : tiList) {
							if(ti.isUserTask()) {
								userTaskInfoList.add(ti);
							} else {
								sysTaskInfoList.add(ti);
							}
						}
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								
								ll_loading_mask.setVisibility(View.INVISIBLE);
								lv_task_info.setVisibility(View.VISIBLE);
								MyListViewAdapter.this.notifyDataSetChanged();
							}
							
						});

					}
				}.start();
			}

			@Override
			public int getCount() {
				if(prefs.getBoolean(C.prefs.DISPLAY_SYS_PROC, false)) {
					return 1 + userTaskInfoList.size() + 1 + sysTaskInfoList.size();
				}
				return 1 + userTaskInfoList.size();
			}

			@Override
			public Object getItem(int position) {
				if(position == 0) {
					return null;
				}
				int np = position - 1;
				if(np> -1 && np < userTaskInfoList.size()) {
					return userTaskInfoList.get(np);
				} 
				if(position == (1 + userTaskInfoList.size())) {
					return null;
				} 
				np = (position - (1 + userTaskInfoList.size() + 1));
				if(np > -1 && np < sysTaskInfoList.size() ){
					return sysTaskInfoList.get(np);
				}
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(position == 0) {
					TextView tv =  new TextView(getBaseContext());
					tv.setTextSize(16);
					tv.setTextColor(Color.WHITE);
					tv.setBackgroundColor(Color.GRAY);
					tv.setText("用户进程（" + userTaskInfoList.size() + "）");
					return tv;
				}
				if(position == (1 + userTaskInfoList.size())) {
					TextView tv =  new TextView(getBaseContext());
					tv.setTextSize(16);
					tv.setTextColor(Color.WHITE);
					tv.setBackgroundColor(Color.GRAY);
					tv.setText("系统进程（" + sysTaskInfoList.size() + "）");
					return tv;
				}
				ViewHolder vh = null;
				if(convertView == null || convertView instanceof TextView) {
					vh = new ViewHolder();
					convertView = View.inflate(TaskManagerActivity.this, R.layout.task_info_item, null);
					vh.icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
					vh.name = (TextView) convertView.findViewById(R.id.tv_app_name);
					vh.usedMemory = (TextView) convertView.findViewById(R.id.tv_used_memory);
					vh.checkbox = (CheckBox) convertView.findViewById(R.id.cb_check);
					convertView.setTag(vh);
				} else {
					vh = (ViewHolder) convertView.getTag();
				}
				TaskInfo ti = (TaskInfo) getItem(position);
				vh.icon.setImageDrawable(ti.getIcon());
				vh.name.setText(ti.getName());
				vh.usedMemory.setText("内存占用：" + Formatter.formatFileSize(getBaseContext(), ti.getUsedMemory()));
				if(getPackageName().equals(ti.getPackageName())) {
					vh.checkbox.setVisibility(View.INVISIBLE);
					vh.checkbox.setChecked(false);
					ti.setChecked(false);
				} else {
					vh.checkbox.setChecked(ti.isChecked());
				}
				return convertView;
			}

			public void selectAll() {
				for(TaskInfo ti : tiList) {
					if(getPackageName().equals(ti.getPackageName())) {
						ti.setChecked(false);
					} else {
						ti.setChecked(true);
					}
				}
				notifyDataSetChanged();
			}

			public void selectOpposite() {
				for(TaskInfo ti : tiList) {
					if(getPackageName().equals(ti.getPackageName())) {
						ti.setChecked(false);
					} else {
						ti.setChecked(!ti.isChecked());
					}	
				}
				notifyDataSetChanged();
			}

			public void clear() {
				ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
				for (int i = userTaskInfoList.size() - 1; i > -1; i--) {
					TaskInfo ti = userTaskInfoList.get(i);
					if(ti.isChecked()) {
						am.killBackgroundProcesses(ti.getPackageName());
						userTaskInfoList.remove(i);
					}
				}
				for (int i = sysTaskInfoList.size() - 1; i > -1; i--) {
					TaskInfo ti = sysTaskInfoList.get(i);
					if(ti.isChecked()) {
						am.killBackgroundProcesses(ti.getPackageName());
						sysTaskInfoList.remove(i);
					}
				}
				this.loadData();
			}
		}
		
		class ViewHolder {
			ImageView icon;
			TextView name;
			TextView usedMemory;
			CheckBox checkbox;
		}

		private void selectAll() {
			adapter.selectAll();
		}

		public void selectOpposite() {
			adapter.selectOpposite();
		}

		public void clear() {
			adapter.clear();
		}
	}
	
	public void click_btn_select_all(View v) {
		lvd.selectAll();
	}
	
	public void click_btn_select_opposite(View v) {
		lvd.selectOpposite();
	}
	
	public void click_btn_clear(View v) {
		lvd.clear();
		refreshInfo();
	}
	
	public void click_btn_setting(View v) {
		Intent intent = new Intent(this, TaskManagerSettingActivity.class);
		this.startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		lvd.adapter.notifyDataSetChanged();
	}
}