package com.itheima.safeguard.receiver;

import com.itheima.safeguard.R;
import com.itheima.safeguard.data.C;
import com.itheima.safeguard.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = context.getSharedPreferences(C.prefs.NAME,
				Context.MODE_PRIVATE);
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
			String address = msg.getOriginatingAddress();
			String msgBody = msg.getDisplayMessageBody();
			if (prefs.getBoolean(C.prefs.TURN_ON_SAFE_GUARD, false)) {
				if (address.contains(prefs.getString(C.prefs.SECURITY_PHONE_NUMBER, ""))) {
					if ("#*location*#".equals(msgBody)) {
						Intent serviceIntent = new Intent(context, GPSService.class);
						context.startService(serviceIntent);
						String lastLocation = prefs.getString(C.prefs.LAST_LOCATION, null);
						if(lastLocation == null) {
							lastLocation = "getting location, please wait...";
						}
						SmsManager.getDefault().sendTextMessage(address, null, lastLocation, null, null);
						this.abortBroadcast();
					} else if ("#*alarm*#".equals(msgBody)) {
						MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
						player.setLooping(false);
						player.setVolume(0.5f, 0.5f);
						player.start();
						this.abortBroadcast();
					} else if ("#*wipedata*#".equals(msgBody)) {
						DevicePolicyManager dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
						ComponentName compName = new ComponentName(context.getPackageName(), MyDeviceAdminReceiver.class.getName());
						if(dpm.isAdminActive(compName)) {
							//Toast.makeText(context, "#*wipedata*#", Toast.LENGTH_SHORT).show();
							//dpm.wipeData(0);
						}
						this.abortBroadcast();
					} else if ("#*lockscreen*#".equals(msgBody)) {
						DevicePolicyManager dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
						ComponentName compName = new ComponentName(context.getPackageName(), MyDeviceAdminReceiver.class.getName());
						if(dpm.isAdminActive(compName)) {
							dpm.resetPassword("", 0);
							dpm.lockNow();
							
						}
						this.abortBroadcast();
					}
				}
			}
		}
	}

}
