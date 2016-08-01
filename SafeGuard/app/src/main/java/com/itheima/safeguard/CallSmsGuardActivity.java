package com.itheima.safeguard;

import java.util.List;

import com.itheima.safeguard.db.dao.BlockNumberDao;
import com.itheima.safeguard.entity.BlockNumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsGuardActivity extends Activity {

	private ListView lv_block_number;
	private List<BlockNumber> bnList;
	private BlockNumberDao dao;
	private MyListAdapter myListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_call_sms_guard);
		this.getActionBar().setTitle(this.getIntent().getStringExtra("title"));
		this.getActionBar().setIcon(new BitmapDrawable(this.getResources(), (Bitmap)this.getIntent().getParcelableExtra("icon")));

		this.dao = new BlockNumberDao(this);
		this.bnList = dao.getPart(0, 10);
		this.lv_block_number = (ListView) this.findViewById(R.id.lv_block_number);
		this.myListAdapter = new MyListAdapter();
		this.lv_block_number.setAdapter(myListAdapter);
		this.lv_block_number.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					if(view.getLastVisiblePosition() == bnList.size() - 1) {
						List<BlockNumber> list = dao.getPart(bnList.size(), 10);
						if(list != null && !list.isEmpty()) {
							bnList.addAll(list);
							myListAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getApplicationContext(), "no more data", 0).show();
						}
					}
					break;

				default:
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}
	
	private class MyListAdapter extends BaseAdapter {

		
		@Override
		public int getCount() {
			return bnList.size();
		}

		@Override
		public Object getItem(int position) {
			return bnList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if(convertView == null) {
				convertView = View.inflate(CallSmsGuardActivity.this, R.layout.normal_item, null);
				holder = new Holder();
				holder.title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.desc = (TextView) convertView.findViewById(R.id.tv_desc);
				holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.icon.setImageResource(R.drawable.delete_selector);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.title.setText(bnList.get(position).getNumber());
			holder.desc.setText(BlockNumber.BLOCK_OPTION.get(bnList.get(position).getMode()));
			holder.icon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsGuardActivity.this);
					builder.setTitle("删除确认");
					builder.setMessage("是否要删除这一条记录？");
					builder.setNegativeButton("取消", null);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(bnList.get(position).getNumber());
							bnList.remove(position);
							MyListAdapter.this.notifyDataSetChanged();
							dialog.dismiss();
						}
					});
					builder.show();
				}
			});
			return convertView;
		}
		
		class Holder {
			TextView title;
			TextView desc;
			ImageView icon;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_call_sms_guard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add_block_number) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("添加黑名单");
			View contentView = View.inflate(this, R.layout.dialog_add_block_number, null);
			builder.setView(contentView);
			final AlertDialog dialog = builder.show();
			final EditText et_phone_number = (EditText) contentView.findViewById(R.id.et_phone_number);
			final CheckBox blockPhone = (CheckBox) contentView.findViewById(R.id.cb_block_phone);
			final CheckBox blockSms = (CheckBox) contentView.findViewById(R.id.cb_block_sms);
			Button neg = (Button) contentView.findViewById(R.id.btn_negative);
			neg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			Button pos = (Button) contentView.findViewById(R.id.btn_positive);
			pos.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String phoneNumber = null;
					if((phoneNumber = et_phone_number.getText().toString().trim()).isEmpty()) {
						Toast.makeText(getApplicationContext(), "请输入要拦截的电话号码", Toast.LENGTH_LONG).show();
						return;
					}
					int blockMode = BlockNumber.BLOCK_PHONE;
					if(blockPhone.isChecked() && blockSms.isChecked()) {
						blockMode = BlockNumber.BLOCK_ALL;
					} else if(blockPhone.isChecked()) {
						blockMode = BlockNumber.BLOCK_PHONE;
					} else if(blockSms.isChecked()) {
						blockMode = BlockNumber.BLOCK_SMS;
					} else {
						Toast.makeText(getApplicationContext(), "请勾选拦截模式", Toast.LENGTH_LONG).show();
						return;
					}
					dao.add(phoneNumber, blockMode);
					bnList.add(0, new BlockNumber(phoneNumber, blockMode));
					myListAdapter.notifyDataSetChanged();
					dialog.dismiss();
				}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
