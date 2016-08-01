package com.itheima.safeguard;

import com.itheima.safeguard.ui.CheckBoxItemView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

public class MobileSecuritySetup1Activity extends
		MobileSecuritySetupBaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_mobile_security_setup1);
		this.getActionBar().setTitle("1.欢迎使用手机防盗");
		this.getActionBar().setIcon(
				new BitmapDrawable(this.getResources(), (Bitmap) this
						.getIntent().getParcelableExtra("icon")));
	}
	
	

	public void next(View v) {
		next();
	}

	@Override
	protected void next() {
		Intent intent = new Intent(this, MobileSecuritySetup2Activity.class);
		intent.putExtra("icon", this.getIntent().getParcelableExtra("icon"));
		this.startActivity(intent);
	}
}
