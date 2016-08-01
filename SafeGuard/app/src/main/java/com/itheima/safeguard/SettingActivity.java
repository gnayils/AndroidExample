package com.itheima.safeguard;

import java.util.HashMap;
import java.util.Map;

import com.itheima.safeguard.data.C;
import com.itheima.safeguard.service.CallSmsGuardService;
import com.itheima.safeguard.service.PhoneAddressDisplayService;
import com.itheima.safeguard.service.WatchDogService;
import com.itheima.safeguard.ui.CheckBoxItemView;
import com.itheima.safeguard.ui.NormalItemView;
import com.itheima.safeguard.utils.SystemTool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity {
	
	private SharedPreferences prefs;
	private CheckBoxItemView civ_auto_update_setting;
	private CheckBoxItemView civ_phone_address_display_setting;
	private NormalItemView niv_phone_address_display_style_setting;
	private CheckBoxItemView civ_block_sms_phone_setting;
	private CheckBoxItemView civ_app_lock_setting;
	private static final String[] PHONE_ADDRESS_DISPLAY_STYLE_NAME = new String[]{"半透明","活力橙", "卫士蓝","金属灰","苹果绿"};
	public static final int[] PHONE_ADDRESS_DISPLAY_STYLE = new int[]{R.drawable.call_locate_white, 
		R.drawable.call_locate_orange,R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_setting);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));
		this.prefs = this.getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE);
		this.civ_auto_update_setting = (CheckBoxItemView) this.findViewById(R.id.civ_auto_update_setting);
		this.civ_phone_address_display_setting = (CheckBoxItemView) this.findViewById(R.id.civ_phone_address_display_setting);
		this.niv_phone_address_display_style_setting = (NormalItemView) this.findViewById(R.id.niv_phone_address_display_style_setting);
		this.civ_block_sms_phone_setting = (CheckBoxItemView) this.findViewById(R.id.civ_block_sms_phone_setting);
		this.civ_app_lock_setting = (CheckBoxItemView) this.findViewById(R.id.civ_app_lock_setting);
		this.autoUpdate();
		this.phoneAddressDisplay();
		this.phoneAddressDisaplayStyle();
		this.blockPhoneSms();
		this.blockOpenApp();
	}

	private void autoUpdate() {
		this.civ_auto_update_setting.setChecked(prefs.getBoolean(C.prefs.AUTO_UPDATE, false)); 
		this.civ_auto_update_setting.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor editor = prefs.edit();
				editor.putBoolean(C.prefs.AUTO_UPDATE, isChecked);
				editor.commit();
			}
			
		});
	}

	private void phoneAddressDisplay() {
		boolean isRunning = SystemTool.isServiceRunning(this, PhoneAddressDisplayService.class);
		this.civ_phone_address_display_setting.setChecked(isRunning);
		this.niv_phone_address_display_style_setting.setEnabled(isRunning);
		this.civ_phone_address_display_setting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(SettingActivity.this, PhoneAddressDisplayService.class);
				if(isChecked) {
					SettingActivity.this.startService(intent);
				} else {
					SettingActivity.this.stopService(intent);
				}
				niv_phone_address_display_style_setting.setEnabled(isChecked);
			}
		});
	}
	


	private void phoneAddressDisaplayStyle() {
		this.niv_phone_address_display_style_setting.setDesc(PHONE_ADDRESS_DISPLAY_STYLE_NAME[prefs.getInt(C.prefs.PHONE_ADDRESS_DISPLAY_STYLE, 0)]);
		this.niv_phone_address_display_style_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(PHONE_ADDRESS_DISPLAY_STYLE_NAME, prefs.getInt(C.prefs.PHONE_ADDRESS_DISPLAY_STYLE, 0), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						prefs.edit().putInt(C.prefs.PHONE_ADDRESS_DISPLAY_STYLE, which).commit();
						niv_phone_address_display_style_setting.setDesc(PHONE_ADDRESS_DISPLAY_STYLE_NAME[which]);
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
		
	}
	
	private void blockPhoneSms() {
		boolean isRunning = SystemTool.isServiceRunning(this, CallSmsGuardService.class);
		this.civ_block_sms_phone_setting.setChecked(isRunning);
		this.civ_block_sms_phone_setting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(SettingActivity.this, CallSmsGuardService.class);
				if(isChecked) {
					SettingActivity.this.startService(intent);
				} else {
					SettingActivity.this.stopService(intent);
				}
			}
		});
	}
	


	private void blockOpenApp() {
		boolean isRunning = SystemTool.isServiceRunning(this, WatchDogService.class);
		this.civ_app_lock_setting.setChecked(isRunning);
		this.civ_app_lock_setting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(SettingActivity.this, WatchDogService.class);
				if(isChecked) {
					SettingActivity.this.startService(intent);
				} else {
					SettingActivity.this.stopService(intent);
				}
			}
		});
	}
}


