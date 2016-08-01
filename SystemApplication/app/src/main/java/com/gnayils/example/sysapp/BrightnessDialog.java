package com.gnayils.example.sysapp;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/** A dialog that provides controls for adjusting the screen brightness. */
public class BrightnessDialog extends Dialog implements
        BrightnessController.BrightnessStateChangeCallback {
	public static final int PRIVATE_FLAG_SHOW_FOR_ALL_USERS = 0x00000010;
	public static final int TYPE_VOLUME_OVERLAY = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW+20;
    private static final String TAG = "BrightnessDialog";
    private static final boolean DEBUG = false;

    protected Handler mHandler = new Handler();

    private BrightnessController mBrightnessController;
    private final int mBrightnessDialogLongTimeout;
    private final int mBrightnessDialogShortTimeout;

    private final Runnable mDismissDialogRunnable = new Runnable() {
        public void run() {
            if (BrightnessDialog.this.isShowing()) {
                BrightnessDialog.this.dismiss();
            }
        };
    };


    public BrightnessDialog(Context ctx) {
        super(ctx);
        Resources r = ctx.getResources();
        mBrightnessDialogLongTimeout =
                r.getInteger(R.integer.quick_settings_brightness_dialog_long_timeout);
        mBrightnessDialogShortTimeout =
                r.getInteger(R.integer.quick_settings_brightness_dialog_short_timeout);
    }


    /**
     * Create the brightness dialog and any resources that are used for the
     * entire lifetime of the dialog.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setType(TYPE_VOLUME_OVERLAY);
//        window.getAttributes().privateFlags |=
//                PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.quick_settings_brightness_dialog);
        setCanceledOnTouchOutside(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mBrightnessController = new BrightnessController(getContext(),
                (ImageView) findViewById(R.id.brightness_icon),
                (ToggleSlider) findViewById(R.id.brightness_slider));
        dismissBrightnessDialog(mBrightnessDialogLongTimeout);
        mBrightnessController.addStateChangedCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBrightnessController.unregisterCallbacks();
        removeAllBrightnessDialogCallbacks();
    }

    public void onBrightnessLevelChanged() {
        dismissBrightnessDialog(mBrightnessDialogShortTimeout);
    }

    private void dismissBrightnessDialog(int timeout) {
        removeAllBrightnessDialogCallbacks();
        mHandler.postDelayed(mDismissDialogRunnable, timeout);
    }

    private void removeAllBrightnessDialogCallbacks() {
        mHandler.removeCallbacks(mDismissDialogRunnable);
    }

}
