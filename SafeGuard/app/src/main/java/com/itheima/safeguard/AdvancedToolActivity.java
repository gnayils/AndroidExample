package com.itheima.safeguard;

import java.io.IOException;
import java.util.Calendar;

import com.itheima.safeguard.ui.NormalItemView;
import com.itheima.safeguard.utils.SmsTool;
import com.itheima.safeguard.utils.SmsTool.OnBackupRestoreListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AdvancedToolActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_advanced_tool);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));
	}
	
	public void click_niv_phone_address_search(View v) {
		Intent intent = new Intent(AdvancedToolActivity.this, PhoneAddressSearchActivity.class);
		AdvancedToolActivity.this.startActivity(intent);
	}
	
	public void click_niv_sms_backup(final View v) {
		final ProgressDialog pd = new ProgressDialog(AdvancedToolActivity.this);
		pd.setMessage("短信备份中");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){

			@Override
			public void run() {
				SmsTool.backup(getBaseContext(), new OnBackupRestoreListener(){
					
					@Override
					public void onBackupRestoreCount(int count) {
						pd.setMax(count);
					}

					@Override
					public void onBackupRestoreProgress(int progress) {
						pd.setProgress(progress);
					}

					@Override
					public void onBackupRestoreSuccess() {
						runOnUiThread(new Runnable(){
							
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "短信备份成功", Toast.LENGTH_LONG).show();
								NormalItemView niv = (NormalItemView) v;
								niv.setDesc("备份日期：" + Calendar.getInstance().getTime().toString());
							}
						});
					}

					@Override
					public void onBackupRestoreFailed(String errorMsg) {
						runOnUiThread(new Runnable(){
							
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "短信备份失败", Toast.LENGTH_LONG).show();
							}
						});
					}

					@Override
					public void onBackupRestoreFinish() {
						pd.dismiss();
					}
				});
			}
		}.start();
	}
	
	public void click_niv_sms_restore(final View v) {
		final ProgressDialog pd = new ProgressDialog(AdvancedToolActivity.this);
		pd.setMessage("短信还原中");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){

			@Override
			public void run() {
				SmsTool.restore(getBaseContext(), new OnBackupRestoreListener(){
					
					@Override
					public void onBackupRestoreCount(int count) {
						pd.setMax(count);
					}

					@Override
					public void onBackupRestoreProgress(int progress) {
						pd.setProgress(progress);
					}

					@Override
					public void onBackupRestoreSuccess() {
						runOnUiThread(new Runnable(){
							
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "短信还原成功", Toast.LENGTH_LONG).show();
								NormalItemView niv = (NormalItemView) v;
								niv.setDesc("还原日期：" + Calendar.getInstance().getTime().toString());
							}
						});
					}

					@Override
					public void onBackupRestoreFailed(String errorMsg) {
						runOnUiThread(new Runnable(){
							
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "短信还原失败", Toast.LENGTH_LONG).show();
							}
						});
					}

					@Override
					public void onBackupRestoreFinish() {
						pd.dismiss();
					}
				});
			}
		}.start();
	}
}