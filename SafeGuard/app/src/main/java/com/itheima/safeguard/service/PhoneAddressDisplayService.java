package com.itheima.safeguard.service;

import java.util.Random;

import com.itheima.safeguard.R;
import com.itheima.safeguard.SettingActivity;
import com.itheima.safeguard.data.C;
import com.itheima.safeguard.db.dao.PhoneAddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneAddressDisplayService extends Service{

	private TelephonyManager tm;
	private MyPhoneStateListener mpsl;
	private OutgoingCallReceiver ocr;
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ocr = new OutgoingCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		this.registerReceiver(ocr, filter);
		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		mpsl = new MyPhoneStateListener();
		tm.listen(mpsl, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(mpsl, PhoneStateListener.LISTEN_NONE);
		this.unregisterReceiver(ocr);
	}
	
	private static class PhoneAddressDisplayToast {

		private View toastView;
		private TextView message;
		private LayoutParams lr = new LayoutParams();
		
		private PhoneAddressDisplayToast(Context context) {
			toastView = View.inflate(context, R.layout.toast_view, null);
			message = (TextView) toastView.findViewById(R.id.tv_phone_address);
			toastView.setOnTouchListener(new MyOnTouchListener());
			toastView.setOnClickListener(new MyOnClickListener());
			lr.width = LayoutParams.WRAP_CONTENT;
			lr.height = LayoutParams.WRAP_CONTENT;
			lr.flags = LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_NOT_FOCUSABLE;
			lr.type = LayoutParams.TYPE_PRIORITY_PHONE;
	        lr.windowAnimations = android.R.style.Animation_Toast;
	        lr.format = PixelFormat.TRANSLUCENT;
	        lr.gravity = Gravity.TOP|Gravity.LEFT;
	        String loc = sf.getString(C.prefs.PHONE_ADDRESS_DISPLAY_LOCATION, "");
			if(!loc.isEmpty()) {
				String[] xy = loc.split(",");
				lr.x = Integer.parseInt(xy[0]);
				lr.y = Integer.parseInt(xy[1]);
			}
		}
		
		private class MyOnClickListener implements View.OnClickListener {

			Point screen = new Point();
			long[] click = new long[2];
			
			private MyOnClickListener() {
				wm.getDefaultDisplay().getSize(screen);
			}
			
			@Override
			public void onClick(View v) {
				System.arraycopy(click, 1, click, 0, click.length - 1);
				click[click.length - 1] = SystemClock.uptimeMillis();
				if(click[0] >= SystemClock.uptimeMillis() - 500) {
					 lr.x = (screen.x / 2 - v.getWidth() / 2);
					 wm.updateViewLayout(v, lr);
					 sf.edit().putString(C.prefs.PHONE_ADDRESS_DISPLAY_LOCATION, lr.x + "," + lr.y).commit();
				}
			}
			
		}
		
		private class MyOnTouchListener implements View.OnTouchListener {
			
			float startX, startY;
			Point screen = new Point();
			
			private MyOnTouchListener() {
				wm.getDefaultDisplay().getSize(screen);
			}
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getRawX();
					startY = event.getRawY();
					break;	
				case MotionEvent.ACTION_MOVE:
					lr.x += (event.getRawX() - startX);
					lr.y += (event.getRawY() - startY);
					lr.x = lr.x < 0 ? 0 : lr.x;
					lr.y = lr.y < 0 ? 0 : lr.y;
					lr.x = lr.x > screen.x - v.getWidth() ? screen.x - v.getWidth() : lr.x;
					lr.y = lr.y > screen.y - v.getHeight() ? screen.y - v.getHeight() : lr.y;
					startX = event.getRawX();
					startY = event.getRawY();
					break;	
				case MotionEvent.ACTION_UP:
					sf.edit().putString(C.prefs.PHONE_ADDRESS_DISPLAY_LOCATION, lr.x + "," + lr.y).commit();
					break;
				default:
					break;
				}
				wm.updateViewLayout(v, lr);
				return false;
			}
			
		}

		private static PhoneAddressDisplayToast padt;
		private static WindowManager wm;
		private static SharedPreferences sf;
		private static boolean isDisplaying;
		
		public static void show(Context context, String message) {
			if(sf == null) {
				sf = context.getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
			}
			if(wm == null) {
				wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			}
			if(padt == null) {
				padt = new PhoneAddressDisplayToast(context);
			}
			padt.message.setText(message);
			padt.toastView.setBackgroundResource(SettingActivity.PHONE_ADDRESS_DISPLAY_STYLE[sf.getInt(C.prefs.PHONE_ADDRESS_DISPLAY_STYLE, 0)]);
			if(!isDisplaying) {
				wm.addView(padt.toastView, padt.lr);
				isDisplaying = !isDisplaying;
			}
		}
		
		public static void hide(Context context){
			if(wm == null) {
				wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			}
			if(padt != null) {
				if(isDisplaying) {
					wm.removeView(padt.toastView);
					isDisplaying = !isDisplaying;
				}
			}
		}
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				PhoneAddressDisplayToast.hide(PhoneAddressDisplayService.this);
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				String phoneAddress = PhoneAddressDao.findAddress(PhoneAddressDisplayService.this, incomingNumber);
				PhoneAddressDisplayToast.show(PhoneAddressDisplayService.this, phoneAddress);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			default:
				break;
			}
		}
		
	}
	
	private class OutgoingCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String phoneNumber = this.getResultData();
			String phoneAddress = PhoneAddressDao.findAddress(context, phoneNumber);
			PhoneAddressDisplayToast.show(PhoneAddressDisplayService.this, phoneAddress);
		}
		
	}
}
