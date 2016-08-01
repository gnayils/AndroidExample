package com.itheima.safeguard.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.itheima.safeguard.db.dao.BlockNumberDao;
import com.itheima.safeguard.entity.BlockNumber;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class CallSmsGuardService extends Service {

	private TelephonyManager tm;
	private BlockNumberDao dao = new BlockNumberDao(CallSmsGuardService.this);
	private BlockSmsReceiver bsr = new BlockSmsReceiver();;
	private BlockPhoneListener bpl = new BlockPhoneListener();;
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(bpl, PhoneStateListener.LISTEN_CALL_STATE);
		IntentFilter f = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		f.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		this.registerReceiver(bsr, f);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(bsr);
		tm.listen(bpl, PhoneStateListener.LISTEN_NONE);
	}
	
	private class BlockPhoneListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				BlockNumber bn = dao.get(incomingNumber);
				if(bn != null) {
					if(bn.getMode() == BlockNumber.BLOCK_PHONE || bn.getMode() == BlockNumber.BLOCK_ALL) {
						try {
							getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new ContentObserver(new Handler()){

								@Override
								public void onChange(boolean selfChange) {
									ContentResolver cr = CallSmsGuardService.this.getContentResolver();
									cr.delete(CallLog.Calls.CONTENT_URI, "number=?", new String[]{incomingNumber});
									getContentResolver().unregisterContentObserver(this);
								}
								
							});
							
							Class<?> clazz = CallSmsGuardService.class.getClassLoader().loadClass("android.os.ServiceManager");
							Method method = clazz.getDeclaredMethod("getService", String.class);
							IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
							ITelephony.Stub.asInterface(ibinder).endCall();
						} catch (Exception e) {
							e.printStackTrace();
						} 
						
					}
				}
				break;
			}
		}
	}
	
	private class BlockSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for(Object pdu : pdus) {
				SmsMessage msg = SmsMessage.createFromPdu((byte[])pdu);
				String address = msg.getOriginatingAddress();
				BlockNumber bn = dao.get(address);
				if(bn != null) {
					if(bn.getMode() == BlockNumber.BLOCK_SMS || bn.getMode() == BlockNumber.BLOCK_ALL) {
						this.abortBroadcast();
					}
				}
				
			}
		}
		
	}

}
