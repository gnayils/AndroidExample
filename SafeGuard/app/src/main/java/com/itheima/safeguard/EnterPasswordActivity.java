package com.itheima.safeguard;

import com.itheima.safeguard.service.WatchDogService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EnterPasswordActivity extends Activity {
	
	private EditText et_enter_pwd;
	private String pkg_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setFinishOnTouchOutside(false);
		this.setContentView(R.layout.activity_enter_password);
		this.et_enter_pwd = (EditText) this.findViewById(R.id.et_enter_pwd);
		this.pkg_name = this.getIntent().getStringExtra("pkg_name");
		PackageManager pm = this.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(pkg_name, 0);
			this.setTitle(ai.loadLabel(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void btn_ok_click(View view) {
		if(et_enter_pwd.getText().toString().trim().isEmpty()) {
			Toast.makeText(this, "«Î ‰»Î√‹¬Î", 0).show();
		} else if(!"123".equals(et_enter_pwd.getText().toString().trim())){
			Toast.makeText(this, "√‹¬Î¥ÌŒÛ", 0).show();
		} else {
			finish();
			Intent intent = new Intent(WatchDogService.ACTION_RELEASE_CURRENT_LOCK);
			intent.putExtra("pkg_name", pkg_name);
			this.sendBroadcast(intent);
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addCategory(Intent.CATEGORY_MONKEY);
		this.startActivity(intent);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}
}
