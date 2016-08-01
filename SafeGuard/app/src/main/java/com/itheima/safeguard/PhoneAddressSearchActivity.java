package com.itheima.safeguard;

import java.util.regex.Pattern;

import com.itheima.safeguard.db.dao.PhoneAddressDao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneAddressSearchActivity extends Activity {

	private EditText et_phone_number;
	private TextView tv_phone_address;
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_phone_address_search);
		this.getActionBar().setTitle("归属地查询");
		this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		this.et_phone_number = (EditText) this.findViewById(R.id.et_phone_number);
		this.tv_phone_address = (TextView) this.findViewById(R.id.tv_phone_address);
		this.et_phone_number.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s.length() > 2) {
					String phoneAddress = PhoneAddressDao.findAddress(PhoneAddressSearchActivity.this, s.toString());
					PhoneAddressSearchActivity.this.tv_phone_address.setText(phoneAddress);
				} else {
					PhoneAddressSearchActivity.this.tv_phone_address.setText("查询结果");
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
			
		});
	}
	
	public void click_btn_search(View v) {
		if(this.et_phone_number.getText().toString().trim().isEmpty()) {
			Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			this.et_phone_number.startAnimation(shake);
			this.vibrator.vibrate(new long[]{0, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70 },  -1);
		} else {
			String phoneNumber = this.et_phone_number.getText().toString().trim();
			String phoneAddress = PhoneAddressDao.findAddress(this, phoneNumber);
			this.tv_phone_address.setText(phoneAddress);
		}
	}
}
