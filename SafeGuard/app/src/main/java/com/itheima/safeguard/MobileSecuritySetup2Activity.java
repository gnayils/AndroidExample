package com.itheima.safeguard;

import com.itheima.safeguard.data.C;
import com.itheima.safeguard.ui.CheckBoxItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MobileSecuritySetup2Activity extends
		MobileSecuritySetupBaseActivity {

	private TelephonyManager teleManager;
	private CheckBoxItemView si_sim_bind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_mobile_security_setup2);
		this.getActionBar().setTitle("2.手机卡绑定");
		this.getActionBar().setIcon(
				new BitmapDrawable(this.getResources(), (Bitmap) this
						.getIntent().getParcelableExtra("icon")));
		this.teleManager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		this.simBind();
	}

	private void simBind() {
		this.si_sim_bind = (CheckBoxItemView) this
				.findViewById(R.id.si_sim_bind);
		String simSerialNum = prefs.getString(C.prefs.SIM_SERIAL_NUM, null);
		this.si_sim_bind.setChecked(TextUtils.isEmpty(simSerialNum) ? false : true);
		this.si_sim_bind
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = prefs.edit();
						if (isChecked) {
							if (teleManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
								editor.putString(C.prefs.SIM_SERIAL_NUM,
										teleManager.getSimSerialNumber());
							} else {
								buttonView.setChecked(false);
								AlertDialog.Builder builder = new AlertDialog.Builder(
										MobileSecuritySetup2Activity.this);
								builder.setTitle("绑定提示");
								if (teleManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
									builder.setMessage("想要使用此功能，请先插入SIM卡");
								} else {
									builder.setMessage("SIM卡不可用");
								}
								builder.setPositiveButton("确定", null);
								builder.show();
							}
						} else {
							editor.putString(C.prefs.SIM_SERIAL_NUM, null);
						}
						editor.commit();
					}

				});

	}

	public void next(View v) {
		next();
	}

	public void prev(View v) {
		prev();
	}

	protected void next() {
		if (teleManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
			String simSerialNum = this.prefs.getString(C.prefs.SIM_SERIAL_NUM, null);
			if(TextUtils.isEmpty(simSerialNum)) {
				Toast.makeText(this, "请先绑定SIM卡", Toast.LENGTH_LONG).show();
				return;
			}
		} 
		Intent intent = new Intent(this, MobileSecuritySetup3Activity.class);
		intent.putExtra("icon", this.getIntent().getParcelableExtra("icon"));
		this.startActivity(intent);
	}

	protected void prev() {
		this.finish();
	}
}
