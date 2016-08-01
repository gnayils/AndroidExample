package com.itheima.safeguard.receiver;

import com.itheima.safeguard.data.C;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompletedReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = context.getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE);
		TelephonyManager teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String savedSimSerialNum = prefs.getString(C.prefs.SIM_SERIAL_NUM, "");
		if(savedSimSerialNum.isEmpty()) {
			return ;
		}
		teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(teleManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
			String currSimSerialNum = teleManager.getSimSerialNumber();
			if(!currSimSerialNum.equals(savedSimSerialNum)) {
				Toast.makeText(context, "sim card changed", Toast.LENGTH_LONG).show();
			}
		}
	}

}
