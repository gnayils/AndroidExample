package com.itheima.safeguard;

import com.itheima.safeguard.data.C;
import com.itheima.safeguard.receiver.MyDeviceAdminReceiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MobileSecuritySetup4Activity extends
		MobileSecuritySetupBaseActivity {
	
	private static final int REQUEST_CODE_ENABLE_ADMIN = 0;
	private boolean isAdminEnabled;
	private CheckBox cb_turn_on_safe_guard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_mobile_security_setup4);
		this.getActionBar().setTitle("4.即将设置完成");
		this.getActionBar().setIcon(
				new BitmapDrawable(this.getResources(), (Bitmap) this
						.getIntent().getParcelableExtra("icon")));
		this.cb_turn_on_safe_guard = (CheckBox) this.findViewById(R.id.cb_turn_on_safe_guard);
		this.isAdminEnabled = this.prefs.getBoolean(C.prefs.TURN_ON_SAFE_GUARD, false);
		this.cb_turn_on_safe_guard.setChecked(this.isAdminEnabled);
		this.cb_turn_on_safe_guard.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				ComponentName compName = new ComponentName(MobileSecuritySetup4Activity.this.getPackageName(), MyDeviceAdminReceiver.class.getName());
                if (isChecked) {
                	if(!MobileSecuritySetup4Activity.this.isAdminEnabled) {
	                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
	                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启防盗保护，你的手机更安全");
	                    startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                	}
                } else {
                	if(MobileSecuritySetup4Activity.this.isAdminEnabled) {
	                	DevicePolicyManager dpm = (DevicePolicyManager) MobileSecuritySetup4Activity.this.getSystemService(Context.DEVICE_POLICY_SERVICE);
	                	dpm.removeActiveAdmin(compName);
	                	MobileSecuritySetup4Activity.this.isAdminEnabled = false;
                	}
                }
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_ENABLE_ADMIN ) {
			if(resultCode == 0) {
				this.cb_turn_on_safe_guard.setChecked(false);
			} else {
				this.isAdminEnabled = true;
			}
		}
	}

	public void done(View v) {
		this.prefs.edit()
		.putBoolean(C.prefs.TURN_ON_SAFE_GUARD, this.cb_turn_on_safe_guard.isChecked())
		.putBoolean(C.prefs.MOBILE_SECURITY_SETUP_COMPLETED, true).commit();
		Intent intent = new Intent(this, MobileSecurityActivity.class);
		this.startActivity(intent);
	}

	public void prev(View v) {
		prev();
	}

	protected void prev() {
		this.finish();
	}
}
