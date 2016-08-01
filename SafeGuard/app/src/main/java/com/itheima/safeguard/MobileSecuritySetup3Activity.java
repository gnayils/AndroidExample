package com.itheima.safeguard;

import com.itheima.safeguard.data.C;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MobileSecuritySetup3Activity extends
		MobileSecuritySetupBaseActivity {
	
	private EditText et_security_phone_number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_mobile_security_setup3);
		this.getActionBar().setTitle("3.设置安全号码");
		this.getActionBar().setIcon(
				new BitmapDrawable(this.getResources(), (Bitmap) this
						.getIntent().getParcelableExtra("icon")));
		this.et_security_phone_number = (EditText) this.findViewById(R.id.et_security_phone_number);
		this.et_security_phone_number.setText(this.prefs.getString(C.prefs.SECURITY_PHONE_NUMBER, null));
	}

	public void selectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		this.startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == resultCode && data != null) {
			String phoneNumber = data.getStringExtra("phoneNumber");
			this.et_security_phone_number.setText(phoneNumber.replaceAll("-", ""));
		}
	}

	public void next(View v) {
		next();
	}

	public void prev(View v) {
		prev();
	}

	protected void next() {
		String phoneNumber = this.et_security_phone_number.getText().toString().trim();
		if(TextUtils.isEmpty(phoneNumber)) {
			Toast.makeText(this, "请输入或选择一个安全号码", Toast.LENGTH_LONG).show();
			return;
		}
		this.prefs.edit().putString(C.prefs.SECURITY_PHONE_NUMBER, phoneNumber).commit();
		Intent intent = new Intent(this, MobileSecuritySetup4Activity.class);
		intent.putExtra("icon", this.getIntent().getParcelableExtra("icon"));
		this.startActivity(intent);

	}

	protected void prev() {
		this.finish();

	}
}
