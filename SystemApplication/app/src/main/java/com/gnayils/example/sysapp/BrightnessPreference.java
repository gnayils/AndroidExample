
package com.gnayils.example.sysapp;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;

public class BrightnessPreference extends Preference {
	private BrightnessDialog mBrightnessDialog;
	private Context mContext;
    public BrightnessPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onClick() {
        //Intent intent = new Intent(Intent.ACTION_SHOW_BRIGHTNESS_DIALOG);
        //getContext().sendBroadcastAsUser(intent, UserHandle.CURRENT_OR_SELF);
    	if (mBrightnessDialog == null) {
            mBrightnessDialog = new BrightnessDialog(mContext);
            mBrightnessDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mBrightnessDialog = null;
                }
            });
        }

        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
    }
}
