package com.itheima.safeguard.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.android.internal.telephony.ITelephony;
import com.itheima.safeguard.db.dao.BlockNumberDao;
import com.itheima.safeguard.entity.BlockNumber;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class AutoCleanService extends Service {
	
	private ScreenOffReceiver sor;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		sor = new ScreenOffReceiver();
		IntentFilter f = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		this.registerReceiver(sor, f);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(sor);
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
			for(RunningAppProcessInfo rai : runningAppProcesses) {
				am.killBackgroundProcesses(rai.processName);
			}
		}
		
	}

}
