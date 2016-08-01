package com.itheima.safeguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.safeguard.data.C;
import com.itheima.safeguard.receiver.MyDeviceAdminReceiver;
import com.itheima.safeguard.utils.Md5Tool;

public class HomeActivity extends Activity {

	private GridView gv_module_home;
	private String[] moduleNames = {
			"手机防盗","通讯卫士","软件管理",
			"进程管理","流量统计","手机杀毒",
			"缓存管理","高级工具","设置中心",
	};
	private int[] moduleIcons = {
			R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
			R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
			R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings
	};
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_home);
		this.getActionBar().setTitle("功能列表");
		this.prefs = this.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		this.gv_module_home = (GridView) this.findViewById(R.id.gv_module_home);
		this.gv_module_home.setAdapter(new MyAdapter());
		this.gv_module_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView) view.findViewById(R.id.tv_name);
				ImageView iv  = (ImageView) view.findViewById(R.id.iv_icon);
				String title = tv.getText().toString();
				Bitmap icon = ((BitmapDrawable)iv.getBackground()).getBitmap();
				switch (position) {
				case 0:
					startMobileSecurityActivity(title, icon, false);
					break;
				case 1:
					startCallSmsGuardActivity(title, icon);
					break;
				case 2:
					startAppManagerActivity(title, icon);
					break;
				case 3:
					startTaskManagerActivity(title, icon);
					break;
				case 4:
					startTrafficManagerActivity(title, icon);
					break;
				case 5:
					startAntiVirusActivity(title, icon);
					break;
				case 6:
					startCleanCacheActivity(title, icon);
					break;
				case 7:
					startAdvancedToolActivity(title, icon);
					break;
				case 8:
					startSettingActivity(title, icon);
					break;

				default:
					break;
				}
			}

		});
	}
	
	protected void startCleanCacheActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, CleanCacheActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}

	protected void startAntiVirusActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, AntiVirusActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}

	protected void startTrafficManagerActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, TrafficManagerActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}

	protected void startTaskManagerActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}

	protected void startAppManagerActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, AppManagerActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}

	protected void startCallSmsGuardActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, CallSmsGuardActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}

	private void startMobileSecurityActivity(final String title, final Bitmap icon, boolean authenticated) {
		if(authenticated) {
			Intent intent = new Intent(this, MobileSecurityActivity.class);
			intent.putExtra("title", title);
			intent.putExtra("icon", icon);
			this.startActivity(intent);
		} else {
			final String password = this.prefs.getString(C.prefs.PASSWORD, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			if(TextUtils.isEmpty(password)) {
				final View contentView = View.inflate(this, R.layout.dialog_set_password, null);
				builder.setTitle("设置密码");
				builder.setView(contentView);
				final AlertDialog dialog = builder.show();
				
				Button btn_negative = (Button) contentView.findViewById(R.id.btn_negative);
				btn_negative.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				Button btn_positive = (Button) contentView.findViewById(R.id.btn_positive);
				btn_positive.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						EditText et_enter_pwd = (EditText) contentView.findViewById(R.id.et_enter_pwd);
						EditText et_confirm_pwd = (EditText) contentView.findViewById(R.id.et_confirm_pwd);
						if(TextUtils.isEmpty(et_enter_pwd.getText().toString()) || TextUtils.isEmpty(et_confirm_pwd.getText().toString())) {
							Toast.makeText(HomeActivity.this, "请输入密码和确认密码", Toast.LENGTH_LONG).show();
							return;
						} 
						System.out.println(et_enter_pwd.getText() + " <> " + et_confirm_pwd.getText());
						if(!et_enter_pwd.getText().toString().equals(et_confirm_pwd.getText().toString())) {
							Toast.makeText(HomeActivity.this, "密码和确认密码不一致", Toast.LENGTH_LONG).show();
							return;
						}
						HomeActivity.this.prefs.edit()
						.putString(C.prefs.PASSWORD, Md5Tool.encrypt(et_enter_pwd.getText().toString()))
						.commit();
						dialog.dismiss();
						startMobileSecurityActivity(title, icon, true);
					}
				});
			} else {
				final View contentView = View.inflate(this, R.layout.dialog_enter_password, null);
				builder.setTitle("输入密码");
				builder.setView(contentView);
				final AlertDialog dialog = builder.show();
				
				Button btn_negative = (Button) contentView.findViewById(R.id.btn_negative);
				btn_negative.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				Button btn_positive = (Button) contentView.findViewById(R.id.btn_positive);
				btn_positive.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						EditText et_enter_pwd = (EditText) contentView.findViewById(R.id.et_enter_pwd);
						
						if(TextUtils.isEmpty(et_enter_pwd.getText().toString())) {
							Toast.makeText(HomeActivity.this, "请输入密码", Toast.LENGTH_LONG).show();;
							return;
						}
						if(!Md5Tool.encrypt(et_enter_pwd.getText().toString()).equals(password)) {
							Toast.makeText(HomeActivity.this, "密码错误，请重新输入", Toast.LENGTH_LONG).show();
							return;
						}
						dialog.dismiss();
						startMobileSecurityActivity(title, icon, true);
					}
					
				});
			}
		}
	}


	private void startAdvancedToolActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, AdvancedToolActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}
	
	private void startSettingActivity(String title, Bitmap icon) {
		Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("icon", icon);
		this.startActivity(intent);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return moduleNames.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(HomeActivity.this, R.layout.activity_home_item, null);
				holder.iv = (ImageView)convertView.findViewById(R.id.iv_icon);
				holder.tv = (TextView)convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iv.setBackground(HomeActivity.this.getResources().getDrawable(moduleIcons[position]));
			holder.tv.setText(moduleNames[position]);
			return convertView;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return this.moveTaskToBack(false);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private static class ViewHolder {
		ImageView iv;
		TextView tv;
	}
}


