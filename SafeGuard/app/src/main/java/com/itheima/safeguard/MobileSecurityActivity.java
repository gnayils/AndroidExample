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
import android.widget.ImageView;
import android.widget.TextView;

public class MobileSecurityActivity extends Activity {
	
	private SharedPreferences prefs;
	private TextView tv_security_phone_number;
	private ImageView iv_turn_on_safe_guard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_mobile_security);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));
		this.tv_security_phone_number = (TextView) this.findViewById(R.id.tv_security_phone_number);
		this.iv_turn_on_safe_guard = (ImageView) this.findViewById(R.id.iv_turn_on_safe_guard);
		this.prefs = this.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		this.initComponentState();
		if(!prefs.getBoolean(C.prefs.MOBILE_SECURITY_SETUP_COMPLETED, false)) {
			this.setup();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.initComponentState();
	}
	
	private void initComponentState() {
		this.tv_security_phone_number.setText(this.prefs.getString(C.prefs.SECURITY_PHONE_NUMBER, ""));
		ComponentName compName = new ComponentName(this.getPackageName(), MyDeviceAdminReceiver.class.getName());
		DevicePolicyManager dpm = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
		prefs.edit().putBoolean(C.prefs.TURN_ON_SAFE_GUARD, dpm.isAdminActive(compName)).commit();
		this.iv_turn_on_safe_guard.setBackground(this.getResources().getDrawable(this.prefs.getBoolean(C.prefs.TURN_ON_SAFE_GUARD, false) ? R.drawable.lock : R.drawable.unlock));
	}
	
	private void setup() {
		Intent intent = new Intent(this, MobileSecuritySetup1Activity.class);
		intent.putExtra("icon", this.getIntent().getParcelableExtra("icon"));
		this.startActivity(intent);
	}
	
	public void resetup(View v) {
		this.setup();
	}
}
