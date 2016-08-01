package com.itheima.safeguard;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.itheima.safeguard.db.dao.AntiVirusDao;
import com.itheima.safeguard.utils.Md5Tool;
import com.itheima.safeguard.utils.SystemTool;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {

	private ImageView iv_scanning;
	private ProgressBar pd_scanning;
	private TextView tv_progress;
	private LinearLayout ll_scan_result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_anti_virus);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		pd_scanning = (ProgressBar) findViewById(R.id.pd_scanning);
		tv_progress = (TextView) findViewById(R.id.tv_progress);
		ll_scan_result = (LinearLayout) findViewById(R.id.ll_scan_result);
		
		ScanVirusTask task = new ScanVirusTask();
		task.execute(null, null);
	}
	
	private class ScanVirusTask extends AsyncTask<String, Object[], Boolean> {
		
		private List<PackageInfo> packageInfoList;
		private AntiVirusDao dao;
		private Random random =new Random();
		
		@Override
		protected void onPreExecute() {
			
			dao = new AntiVirusDao(getApplication());
			packageInfoList = getPackageManager().getInstalledPackages(0);
			RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			ra.setDuration(1000);
			ra.setRepeatCount(Animation.INFINITE);
			ra.setRepeatMode(Animation.RESTART);
			ra.setInterpolator(new LinearInterpolator());
			pd_scanning.setMax(packageInfoList.size());
			iv_scanning.setVisibility(View.VISIBLE);
			iv_scanning.startAnimation(ra);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if(packageInfoList != null) {
				tv_progress.setText("正在初始化云查杀引擎...");
				sleep(800);
				for(int i=0; i<packageInfoList.size(); i++) {
					PackageInfo pi = packageInfoList.get(i);
					String md5 = Md5Tool.calcFileMd5(pi.applicationInfo.sourceDir);
					publishProgress(new Object[] {
							i+1,
							pi.applicationInfo.loadIcon(getPackageManager()),
							pi.applicationInfo.loadLabel(getPackageManager()),
							pi.packageName,
							dao.isVirus(md5)
							});
					sleep(random.nextInt(5) * 100 + 100);
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Object[]... pInfo) {
			for(Object[] info : pInfo) {
				pd_scanning.setProgress(Integer.valueOf(info[0].toString()));
				tv_progress.setText("正在扫描：" + info[2].toString());
				LinearLayout.LayoutParams lr = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				
				LinearLayout ll = new LinearLayout(getBaseContext());
				ll.setLayoutParams(lr);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				
				LinearLayout.LayoutParams lr1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
				lr1.weight = 1;
				lr1.setMargins(8, 8, 8, 8);
				TextView tv1 = new TextView(AntiVirusActivity.this);
				tv1.setSingleLine(true);
				tv1.setEllipsize(TextUtils.TruncateAt.END);
				tv1.setTextSize(16);
				tv1.setTextColor(Boolean.valueOf(info[4].toString()) ? Color.RED : Color.BLACK);
				tv1.setGravity(Gravity.CENTER_VERTICAL);
				tv1.setText(info[2].toString());
				Drawable icon = (Drawable)info[1];
				icon.setBounds(0, 0, (int)tv1.getTextSize() + 10, (int)tv1.getTextSize() + 10);
				tv1.setCompoundDrawables(icon,null,null,null);
				tv1.setLayoutParams(lr1);
				ll.addView(tv1);
				
				LinearLayout.LayoutParams lr2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lr2.setMargins(8, 8, 8, 8);
				TextView tv2 = new TextView(AntiVirusActivity.this);
				tv2.setTextSize(16);
				tv2.setTextColor(Boolean.valueOf(info[4].toString()) ? Color.RED : Color.BLACK);
				tv2.setText(Boolean.valueOf(info[4].toString()) ? "发现病毒" : "扫描正常");
				tv2.setLayoutParams(lr2);
				ll.addView(tv2);
				
				ll_scan_result.addView(ll, 0);
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			dao.close();
			iv_scanning.clearAnimation();
			iv_scanning.setVisibility(View.INVISIBLE);
			tv_progress.setText("扫描完成");
			
		}
		
		private void sleep(int time) {
//			try {
//				Thread.sleep(time);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
}
