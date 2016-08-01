package com.itheima.safeguard.utils;

import java.util.ArrayList;
import java.util.List;

import com.itheima.safeguard.R;
import com.itheima.safeguard.entity.AppInfo;
import com.itheima.safeguard.entity.TaskInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.StatFs;
import android.text.format.Formatter;

public class SystemTool {

	public static boolean isServiceRunning(Context context,
			Class<?> serviceClass) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> sriList = am.getRunningServices(1000);
		for (RunningServiceInfo sri : sriList) {
			if (sri.service.getClassName().equals(serviceClass.getName())) {
				return true;
			}
		}
		return false;
	}

	public static String getAvailableSpace(Context context, String path) {
		StatFs fs = new StatFs(path);
		long availableBlocksCount = fs.getAvailableBlocks();
		long size = fs.getBlockSize();
		return Formatter.formatFileSize(context, availableBlocksCount * size);
	}

	public static List<AppInfo> getAppInfo(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		for (PackageInfo pi : packageInfoList) {
			AppInfo ai = new AppInfo();
			ai.setName(pi.applicationInfo.loadLabel(pm).toString());
			ai.setIcon(pi.applicationInfo.loadIcon(pm));
			ai.setPackageName(pi.packageName);
			ai.setInstalledOnRom((ApplicationInfo.FLAG_EXTERNAL_STORAGE & pi.applicationInfo.flags) == 0);
			ai.setInstalledByUser((ApplicationInfo.FLAG_SYSTEM & pi.applicationInfo.flags) == 0);
			appInfoList.add(ai);
		}
		return appInfoList;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static List<RunningAppProcessInfo> getRunningAppProcessList(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list;
	}

	public static MemoryInfo getMemoryInfo(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi;
	}
	
	public static List<TaskInfo> getTaskInfo(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> rapiList = getRunningAppProcessList(context);
		List<TaskInfo> tiList = new ArrayList<TaskInfo>();
		for(RunningAppProcessInfo rapi : rapiList) {
			TaskInfo ti = new TaskInfo();
			String packageName = rapi.processName;
			ti.setPackageName(packageName);
			Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{rapi.pid});
			ti.setUsedMemory(memoryInfos[0].getTotalPrivateDirty() << 10);
			ApplicationInfo ai;
			try {
				ai = pm.getApplicationInfo(packageName, 0);
				ti.setName(ai.loadLabel(pm).toString());
				ti.setIcon(ai.loadIcon(pm));
				ti.setUserTask((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				ti.setName(packageName);
				ti.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
			}
			tiList.add(ti);
		}
		return tiList;
	}

}
