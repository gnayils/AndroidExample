package com.itheima.safeguard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import com.itheima.safeguard.data.C;
import com.itheima.safeguard.db.dao.AppLockInfoDao;
import com.itheima.safeguard.service.PhoneAddressDisplayService;
import com.itheima.safeguard.service.WatchDogService;
import com.itheima.safeguard.utils.StreamTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int CHECK_UPDATE = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int SHOW_CONFIRM_UPDATE_DIALOG = 2;
	protected static final int SHOW_UPDATE_DIALOG = 3;
	protected static final int URL_EXCEPTION = 4;
	protected static final int IO_EXCEPTION = 5;
	protected static final int JSON_EXCEPTION = 6;
	private String desc;
	private String apkurl;
	private String version;
	private TextView tv_version_splash;
	private ProgressBar pb_check_update_splash;
	private SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.activity_splash);
		prefs = this.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		installShortcut();
		this.tv_version_splash = (TextView) this.findViewById(R.id.tv_version_splash);
		this.tv_version_splash.setText("Version：" + getVersionName());
		this.pb_check_update_splash = (ProgressBar) this.findViewById(R.id.pb_check_update_splash);
		if(prefs.getBoolean(C.prefs.AUTO_UPDATE, false)) {
			handler.sendEmptyMessage(CHECK_UPDATE);
		} else {
			handler.sendEmptyMessageDelayed(ENTER_HOME, 2000);
		}
	}

	private void installShortcut() {
		if(prefs.getBoolean(C.prefs.SHORTCUT_CREATED, false)) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
	
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.itheima.safeguard.SplashActivity");
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		
		sendBroadcast(intent);
		prefs.edit().putBoolean(C.prefs.SHORTCUT_CREATED, true).commit();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_UPDATE:
				new Thread(){
					
					public void run() {
						SplashActivity.this.checkUpdate();
					};
					
				}.start();
				break;
			case ENTER_HOME:
				SplashActivity.this.enterHome();
				break;
			case SHOW_CONFIRM_UPDATE_DIALOG:
				SplashActivity.this.showConfirmUpdateDialog(msg);
				break;
			case SHOW_UPDATE_DIALOG:
				SplashActivity.this.showUpdateDialog(msg);
				break;
			case URL_EXCEPTION:
				Toast.makeText(SplashActivity.this, "URL_EXCEPTION", Toast.LENGTH_SHORT).show();;
				SplashActivity.this.enterHome();
				break;
			case IO_EXCEPTION:
				Toast.makeText(SplashActivity.this, "IO_EXCEPTION", Toast.LENGTH_SHORT).show();
				SplashActivity.this.enterHome();
				break;
			case JSON_EXCEPTION:
				Toast.makeText(SplashActivity.this, "JSON_EXCEPTION", Toast.LENGTH_SHORT).show();
				SplashActivity.this.enterHome();
				break;
			default:
				break;
			}
		}
	};
	
	private void checkUpdate() {
		long startTime = SystemClock.uptimeMillis();
		this.pb_check_update_splash.setVisibility(View.VISIBLE);
		int what = ENTER_HOME;
		try {
			URL url = new URL(SplashActivity.this.getString(R.string.updateurl));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(2000);
			int returnCode = conn.getResponseCode();
			if(returnCode == 200) {
				InputStream is = conn.getInputStream();
				String returnMsg = StreamTool.readFromStream(is);
				JSONObject json = new JSONObject(returnMsg);
				 version = json.getString("version");
				 desc = json.getString("description");
				 apkurl = json.getString("apkurl");
				if(version.equals(SplashActivity.this.getVersionName())) {
					what = ENTER_HOME;
				} else {
					what = SHOW_CONFIRM_UPDATE_DIALOG;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			what = URL_EXCEPTION;
		} catch (IOException e) {
			e.printStackTrace();
			what = IO_EXCEPTION;
		} catch (JSONException e) {
			e.printStackTrace();
			what = JSON_EXCEPTION;
		} finally {
			long endTime = SystemClock.uptimeMillis();
			long elapseTime = endTime - startTime;
			long remainTime = 2000 - elapseTime;
			if(remainTime < 0) {
				remainTime = 0;
			}
			if(what != ENTER_HOME) {
				remainTime = 0;
			}
			handler.sendEmptyMessageDelayed(what, remainTime);
		}
	}
	
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		this.startActivity(intent);
		this.finish();		
	}

	private void showConfirmUpdateDialog(final Message msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("应用升级");
		builder.setMessage(desc);
		builder.setCancelable(false);
		builder.setPositiveButton("现在升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				handler.sendEmptyMessage(SHOW_UPDATE_DIALOG);
			}
		});
		builder.setNegativeButton("暂不升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				SplashActivity.this.enterHome();
			}
		});
		builder.show();
	}
	
	private void showUpdateDialog(Message msg) {
		final ProgressDialog pd = new ProgressDialog(SplashActivity.this, ProgressDialog.STYLE_SPINNER);
		pd.setTitle("在线升级");
		pd.setMessage("下载更新包中...");
		pd.setIndeterminate(false);
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		FinalHttp finalHttp = new FinalHttp();
		final HttpHandler httpHandler = finalHttp.download(apkurl, Environment.getExternalStorageDirectory() + "/SafeGuard2.0.apk", new AjaxCallBack<File>() {

			@Override
			public void onFailure(Throwable t, int errorNo,
					String strMsg) {
			}

			@Override
			public void onLoading(long count, long current) {
				pd.setMax((int) count >> 20);
				pd.setProgress((int) current >> 20);
			}

			@Override
			public void onStart() {
				pd.show();
			}

			@Override
			public void onSuccess(File t) {
				pd.dismiss();
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
				SplashActivity.this.startActivity(intent);
			}
		});
		pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				httpHandler.cancel(true);
				dialog.dismiss();
				SplashActivity.this.enterHome();
			}
		});
	}

	private String getVersionName() {
		PackageManager pm = this.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(this.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
}
