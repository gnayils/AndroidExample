package com.itheima.safeguard;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_traffic_manager);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
		for(ApplicationInfo ai : installedApplications) {
			int uid = ai.uid;
			long tx = TrafficStats.getUidTxBytes(uid);
			long rx = TrafficStats.getUidRxBytes(uid);	
		}
		
	}
}
