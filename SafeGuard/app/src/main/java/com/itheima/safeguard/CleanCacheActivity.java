package com.itheima.safeguard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageStats;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CleanCacheActivity extends Activity {

	private MyPackageStatsObserver psObserver = new MyPackageStatsObserver();
	private MyPackageDataObserver pdObserver = new MyPackageDataObserver();
	private List<Object[]> list = new ArrayList<Object[]>();
	private ProgressBar pb_scanning;
	private TextView tv_scanning;
	private ListView lv_app_cache;
	private MyAdapter adapter = new MyAdapter();
	private Task task;
	private final Lock lock = new ReentrantLock();
	private final Condition invokeCompleted = lock.newCondition();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_clean_cache);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(
				new BitmapDrawable(this.getResources(), (Bitmap) this
						.getIntent().getParcelableExtra("icon")));
		this.pb_scanning = (ProgressBar) this.findViewById(R.id.pb_scanning);
		this.tv_scanning = (TextView) this.findViewById(R.id.tv_scanning);
		this.lv_app_cache = (ListView) this.findViewById(R.id.lv_app_cache);
		this.lv_app_cache.setAdapter(adapter);
		task = new Task();
		task.execute(null, null);
	}
	
	public void click_btn_clear_all(View v) {
		try {
			Method method = getPackageManager().getClass().getMethod("freeStorageAndNotify", new Class<?>[]{Long.TYPE, IPackageDataObserver.class});
			method.invoke(getPackageManager(), Long.MAX_VALUE, pdObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if(convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.app_cache_item, null);
				vh = new ViewHolder();
				vh.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
				vh.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
				vh.tv_cache = (TextView) convertView.findViewById(R.id.tv_cache);
				vh.iv_clean = (ImageView) convertView.findViewById(R.id.iv_clean);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			Object[] info = (Object[]) getItem(position);
			final PackageInfo pi = (PackageInfo) info[0];
			PackageStats ps = (PackageStats) info[1];
			vh.iv_app_icon.setImageDrawable(pi.applicationInfo.loadIcon(getPackageManager()));
			vh.tv_app_name.setText(pi.applicationInfo.loadLabel(getPackageManager()));
			vh.tv_cache.setText(Formatter.formatFileSize(getApplicationContext(), ps.cacheSize));
			vh.iv_clean.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
//						Method method = getPackageManager().getClass().getMethod("deleteApplicationCacheFiles", new Class<?>[]{String.class, IPackageDataObserver.class});
//						method.invoke(getPackageManager(), pi.packageName, pdObserver);
		                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		                intent.addCategory("android.intent.category.DEFAULT");
		                intent.setData(Uri.parse("package:" + pi.packageName));
		                startActivity(intent);
					
				}
				
			});
			return convertView;
		}
		
		class ViewHolder {
			ImageView iv_app_icon;
			TextView tv_app_name;
			TextView tv_cache;
			ImageView iv_clean;
		}
	}

	private class Task extends AsyncTask<Void, Object[], Void> {

		private int progress;
		private List<PackageInfo> pkgInfoList;
		@Override
		protected void onPreExecute() {
			pkgInfoList = getPackageManager().getInstalledPackages(0);
			pb_scanning.setMax(pkgInfoList.size());
			progress = 0;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Method getPackageSizeInfo = getPackageManager().getClass().getMethod("getPackageSizeInfo", new Class<?>[]{String.class, IPackageStatsObserver.class});
				for(PackageInfo pi : pkgInfoList) {
					lock.lock();
					getPackageSizeInfo.invoke(getPackageManager(), pi.packageName, psObserver);
					sleep(30);
					invokeCompleted.await();
					publishProgress(new Object[]{++progress, pi, psObserver.getPStats()});
					lock.unlock();
				}
			} catch (Exception e) {
				e.printStackTrace();
				cancel(false);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Object[]... values) {
			for(Object[] v : values) {
				PackageInfo pi = (PackageInfo) v[1];
				PackageStats ps = (PackageStats) v[2];
				pb_scanning.setProgress((Integer)v[0]);
				tv_scanning.setText("扫描应用: " + pi.applicationInfo.loadLabel(getPackageManager()));
				if(ps.cacheSize > 1) {
					list.add(new Object[]{v[1], v[2]});
					adapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			tv_scanning.setText("扫描完成");
			adapter.notifyDataSetChanged();
		}
		
		@Override
		protected void onCancelled() {
		}
		
		private void sleep(int time) {
//			try {
//				Thread.sleep(time);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}

	}
	
	private class MyPackageStatsObserver extends IPackageStatsObserver.Stub {

		private PackageStats pStats;
		
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			lock.lock();
			this.pStats = pStats;
			invokeCompleted.signal();
			lock.unlock();
		}
		
		public PackageStats getPStats() {
			return pStats;
		}
	}
	
	private class MyPackageDataObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			System.out.println(packageName + ": " + succeeded);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(CleanCacheActivity.this, "清理成功", 1).show();
					list.clear();
					adapter.notifyDataSetChanged();
				}
			});
		}
	}
}
