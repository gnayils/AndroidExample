package com.itheima.safeguard.service;

import java.util.List;

import com.itheima.safeguard.EnterPasswordActivity;
import com.itheima.safeguard.db.dao.AppLockInfoDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class WatchDogService extends Service {

	private ActivityManager am;
	private boolean watching;
	private AppLockInfoDao dao;
	private List<String> appLockInfoList;
	private MyBroadcastReceiver receiver;
	private String releaseLockForPkgName = "";
	private Intent enterPasswordIntent;
	public static final String ACTION_RELEASE_CURRENT_LOCK = "com.itheima.safeguard.RELEASE_CURRENT_LOCK";
	public static final String ACTION_APP_LOCK_RECORD_CHANGED = "com.itheima.safeguard.APP_LOCK_RECORD_CHANGED";
	
	@Override
	public void onCreate() {
		this.am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		this.dao = new AppLockInfoDao(this);
		this.appLockInfoList = dao.getAll();
		this.receiver = new MyBroadcastReceiver();
		this.enterPasswordIntent = new Intent(getApplicationContext(), EnterPasswordActivity.class);
		this.enterPasswordIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_RELEASE_CURRENT_LOCK);
		filter.addAction(ACTION_APP_LOCK_RECORD_CHANGED);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		this.registerReceiver(receiver, filter);
		this.watching = true;
		new Thread(){
			
			private String currentPkgName = "";

			@Override
			public void run() {
				while(watching) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					System.out.println(runningTaskInfo.topActivity.getPackageName());
					if(currentPkgName.equals(runningTaskInfo.topActivity.getPackageName())) {
						continue;
					} else {
						if(appLockInfoList.contains(runningTaskInfo.topActivity.getPackageName())) {
							if(releaseLockForPkgName.equals(runningTaskInfo.topActivity.getPackageName())) {
								continue;
							}
							enterPasswordIntent.putExtra("pkg_name", runningTaskInfo.topActivity.getPackageName());
							startActivity(enterPasswordIntent);
						}
						releaseLockForPkgName = null;
						this.currentPkgName = runningTaskInfo.topActivity.getPackageName();
					}
				}
			}
		}.start();
	}
	
	
	
	@Override
	public void onDestroy() {
		this.watching = false;
		this.unregisterReceiver(receiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(ACTION_RELEASE_CURRENT_LOCK.equals(intent.getAction())) {
				releaseLockForPkgName = intent.getStringExtra("pkg_name");
			} else if(ACTION_APP_LOCK_RECORD_CHANGED.equals(intent.getAction())){
				appLockInfoList = dao.getAll();
			} else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
				releaseLockForPkgName = null;
			} else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				
			}
		}
		
	}
}
