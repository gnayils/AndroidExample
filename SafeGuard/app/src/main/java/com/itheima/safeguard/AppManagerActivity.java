package com.itheima.safeguard;

import java.util.ArrayList;
import java.util.List;

import com.itheima.safeguard.db.dao.AppLockInfoDao;
import com.itheima.safeguard.entity.AppInfo;
import com.itheima.safeguard.utils.SystemTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity {

	private TextView tv_rom_avail_space;
	private TextView tv_external_storage_avail_space;
	private ListView lv_app_info;
	private LinearLayout ll_loading_mask;
	private TextView tv_title;
	private List<AppInfo> appInfoList;
	private List<AppInfo> sysAppInfoList;
	private List<AppInfo> userAppInfoList;
	private PopupWindow popupMenu;
	private MyListAdapter mla;
	private AppLockInfoDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_app_manager);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));
		this.dao = new AppLockInfoDao(this);
		this.tv_rom_avail_space = (TextView) this.findViewById(R.id.tv_rom_avail_space);
		this.tv_external_storage_avail_space = (TextView) this.findViewById(R.id.tv_external_storage_avail_space);
		this.ll_loading_mask = (LinearLayout) findViewById(R.id.ll_loading_mask);
		this.tv_title = (TextView) this.findViewById(R.id.tv_title);
		this.tv_rom_avail_space.setText("手机内存：" + SystemTool.getAvailableSpace(this, Environment.getExternalStorageDirectory().getAbsolutePath()));
		this.tv_external_storage_avail_space.setText("外部存储：" + SystemTool.getAvailableSpace(this, Environment.getDataDirectory().getAbsolutePath()));
		this.lv_app_info = (ListView) this.findViewById(R.id.lv_app_info);
		this.lv_app_info.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupMenu();
				if(userAppInfoList != null && sysAppInfoList!= null) {
					if(firstVisibleItem <= userAppInfoList.size()) {
						tv_title.setText("用户应用（" + userAppInfoList.size() + "）");
					} else {
						tv_title.setText("系统应用（" + sysAppInfoList.size() + "）");
					}
				}
			}
			
		});
		this.lv_app_info.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo ai = null;
				if(position > 0 && position <= userAppInfoList.size()) {
					ai =  userAppInfoList.get(position - 1);
				} else if(position > userAppInfoList.size() + 1 && position < 1+userAppInfoList.size() + 1 + sysAppInfoList.size()) {
					ai = sysAppInfoList.get(position - 2 - userAppInfoList.size());
				}
				final AppInfo finalAi = ai;
				if(ai != null) {
					dismissPopupMenu();
					View contentView = View.inflate(getApplicationContext(), R.layout.app_manager_menu, null);
					LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
					LinearLayout ll_launch = (LinearLayout) contentView.findViewById(R.id.ll_launch);
					LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
					ll_uninstall.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dismissPopupMenu();
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.setAction("android.intent.action.DELETE");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setData(Uri.parse("package:" + finalAi.getPackageName()));
							startActivityForResult(intent, 0);
						}
						
					});
					ll_launch.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dismissPopupMenu();
							
//							Intent intent = new Intent();
//							intent.setAction("android.intent.action.MAIN");
//							intent.addCategory("android.intent.category.LAUNCHER");
//							List<ResolveInfo> riList = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
							
							Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(finalAi.getPackageName());
							if(intent == null) {
								Toast.makeText(getApplicationContext(), "这个应用不能启动", 0).show();
							} else {
								startActivity(intent);
							}
						}
						
					});
					ll_share.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dismissPopupMenu();
							Intent intent = new Intent();
							intent.setAction("android.intent.action.SEND");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setType("text/plain");
							intent.putExtra(Intent.EXTRA_TEXT, "推荐一款App：" + finalAi.getName());
							startActivity(intent);
						}
						
					});
					popupMenu = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					popupMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					int[] location = new int[2];
					view.getLocationInWindow(location);
					popupMenu.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, SystemTool.dip2px(getApplicationContext(), 60), location[1] );
					ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(300);
					TranslateAnimation ta = new TranslateAnimation(0, 0, 0, 0);
					ta.setDuration(300);
					AlphaAnimation aa = new AlphaAnimation(0, 1.0f);
					aa.setDuration(300);
					AnimationSet as = new AnimationSet(false);
					as.addAnimation(sa);
					as.addAnimation(ta);
					as.addAnimation(aa);
					contentView.startAnimation(as);
				}
			}
		});
		
		this.lv_app_info.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
				AppInfo ai = null;
				if(position > 0 && position <= userAppInfoList.size()) {
					ai =  userAppInfoList.get(position - 1);
				} else if(position > userAppInfoList.size() + 1 && position < 1+userAppInfoList.size() + 1 + sysAppInfoList.size()) {
					ai = sysAppInfoList.get(position - 2 - userAppInfoList.size());
				}
				if(ai != null) {
					if(dao.isExists(ai.getPackageName())) {
						dao.delete(ai.getPackageName());
					} else {
						dao.add(ai.getPackageName());
					}
					mla.notifyDataSetChanged();
				}
				return true;
			}
		}); 
		
		new Thread(new FetchAppInfoTask()).start();
	}

	private class FetchAppInfoTask implements Runnable {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ll_loading_mask.setVisibility(View.VISIBLE);
					lv_app_info.setVisibility(View.INVISIBLE);
					tv_title.setVisibility(View.INVISIBLE);
				}
			});
			appInfoList = SystemTool.getAppInfo(AppManagerActivity.this);
			userAppInfoList = new ArrayList<AppInfo>();
			sysAppInfoList = new ArrayList<AppInfo>();
			for (AppInfo ai : appInfoList) {
				if (ai.isInstalledByUser()) {
					userAppInfoList.add(ai);
				} else {
					sysAppInfoList.add(ai);
				}
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mla == null) {
						mla = new MyListAdapter();
						lv_app_info.setAdapter(mla);
					} else {
						mla.notifyDataSetChanged();
					}
					ll_loading_mask.setVisibility(View.INVISIBLE);
					lv_app_info.setVisibility(View.VISIBLE);
					tv_title.setVisibility(View.VISIBLE);
				}
			});
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			new Thread(new FetchAppInfoTask()).start();
		}
	}

	private class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 1 + userAppInfoList.size() + 1 + sysAppInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			if (position > 0 && position <= userAppInfoList.size()) {
				return userAppInfoList.get(position - 1);
			} else if (position > userAppInfoList.size() + 1
					&& position < getCount()) {
				return sysAppInfoList
						.get(position - 2 - userAppInfoList.size());
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView tv = new TextView(getBaseContext());
				tv.setTextSize(16);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户应用（" + userAppInfoList.size() + "）");
				return tv;
			} else if (position == userAppInfoList.size() + 1) {
				TextView tv = new TextView(getBaseContext());
				tv.setTextSize(16);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统应用（" + sysAppInfoList.size() + "）");
				return tv;
			} else {

				ViewHolder vh;
				if (convertView == null || convertView instanceof TextView) {
					vh = new ViewHolder();
					convertView = View.inflate(AppManagerActivity.this,
							R.layout.app_info_item, null);
					vh.app_name = (TextView) convertView
							.findViewById(R.id.tv_app_name);
					vh.install_location = (TextView) convertView
							.findViewById(R.id.tv_install_location);
					vh.app_icon = (ImageView) convertView
							.findViewById(R.id.iv_app_icon);
					vh.lock_state = (ImageView) convertView.findViewById(R.id.iv_lock_state);
					convertView.setTag(vh);
				} else {
					vh = (ViewHolder) convertView.getTag();
				}
				AppInfo ai = (AppInfo) this.getItem(position);
				vh.app_name.setText(ai.getName());
				vh.install_location.setText(ai.isInstalledOnRom() ? "手机内存"
						: "外部存储");
				vh.app_icon.setImageDrawable(ai.getIcon());
				vh.lock_state.setImageResource(dao.isExists(ai.getPackageName()) ? R.drawable.lock : R.drawable.unlock);
				return convertView;
			}
		}

		class ViewHolder {
			TextView app_name;
			TextView install_location;
			ImageView app_icon;
			ImageView lock_state;
		}

	}

	@Override
	protected void onDestroy() {
		dismissPopupMenu();
		super.onDestroy();
	}

	private void dismissPopupMenu() {
		if (popupMenu != null && popupMenu.isShowing()) {
			popupMenu.dismiss();
		}
	}
};
