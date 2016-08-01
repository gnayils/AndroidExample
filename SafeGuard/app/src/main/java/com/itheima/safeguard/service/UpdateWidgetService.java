package com.itheima.safeguard.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.itheima.safeguard.R;
import com.itheima.safeguard.receiver.MyWidget;
import com.itheima.safeguard.utils.SystemTool;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	Timer t;
	ScreenOffReceiver off;
	ScreenOnReceiver on;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		t = new Timer();
		t.schedule(new MyTimerTask(), 0, 3000);
		
		IntentFilter fOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
		on = new ScreenOnReceiver();
		this.registerReceiver(on, fOn);
		
		IntentFilter fOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		off = new ScreenOffReceiver();
		this.registerReceiver(off, fOff);
	}

	@Override
	public void onDestroy() {
		if(on != null) {
			this.unregisterReceiver(on);
		}
		if(off != null) {
			this.unregisterReceiver(off);
		}
		if(t != null) {
			t.cancel();
		}
		super.onDestroy();
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(t != null) {
				t.cancel();
			}
		}
	}
	
	private class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			t = new Timer();
			t.schedule(new MyTimerTask(), 0, 3000);
		}
	}
	
	private class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			AppWidgetManager awm = AppWidgetManager.getInstance(UpdateWidgetService.this);
			ComponentName cn = new ComponentName(UpdateWidgetService.this, MyWidget.class);
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
			views.setTextViewText(R.id.process_count, "正在运行的进程：" + SystemTool.getRunningAppProcessList(UpdateWidgetService.this).size());
			views.setTextViewText(R.id.process_memory, "可用内存：" + Formatter.formatFileSize(UpdateWidgetService.this, SystemTool.getMemoryInfo(UpdateWidgetService.this).availMem));
			Intent intent = new Intent();
			intent.setAction("com.itheima.safeguard.KILL_ALL_BACKGROUND_RPOCESSES");
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
			awm.updateAppWidget(cn, views);
		}
		
	}
}
