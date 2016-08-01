package com.itheima.safeguard;

import com.itheima.safeguard.R;
import com.itheima.safeguard.data.C;
import com.itheima.safeguard.service.AutoCleanService;
import com.itheima.safeguard.ui.CheckBoxItemView;
import com.itheima.safeguard.utils.SystemTool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskManagerSettingActivity extends Activity {

	private SharedPreferences prefs;
	private CheckBoxItemView civ_display_sys_proc;
	private CheckBoxItemView civ_clean_at_screen_off;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_task_manager_setting);
		this.prefs = this.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		this.civ_display_sys_proc = (CheckBoxItemView) this.findViewById(R.id.civ_display_sys_proc);
		this.civ_clean_at_screen_off = (CheckBoxItemView) this.findViewById(R.id.civ_clean_at_screen_off);
		this.civ_display_sys_proc.setChecked(prefs.getBoolean(C.prefs.DISPLAY_SYS_PROC, false));
		this.civ_display_sys_proc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean(C.prefs.DISPLAY_SYS_PROC, isChecked).commit();
			}
		});
		
		this.civ_clean_at_screen_off.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent(TaskManagerSettingActivity.this, AutoCleanService.class);
				if(isChecked) {
					startService(intent);
				} else {
					stopService(intent);
				}
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		this.civ_clean_at_screen_off.setChecked(SystemTool.isServiceRunning(this, AutoCleanService.class));
	}
}
