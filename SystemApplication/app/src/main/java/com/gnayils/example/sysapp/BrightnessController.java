package com.gnayils.example.sysapp;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.widget.ImageView;

public class BrightnessController implements ToggleSlider.Listener {
    private static final String TAG = "StatusBar.BrightnessController";

    private final int mMinimumBacklight;
    private final int mMaximumBacklight;
    private DeviceManager mDm;

    private final Context mContext;
    private final ImageView mIcon;
    private final ToggleSlider mControl;
    private final boolean mAutomaticAvailable;
    //private final IPowerManager mPower;
    //private final CurrentUserTracker mUserTracker;
    private final Handler mHandler;
    private final BrightnessObserver mBrightnessObserver;

    private ArrayList<BrightnessStateChangeCallback> mChangeCallbacks =
            new ArrayList<BrightnessStateChangeCallback>();

    public interface BrightnessStateChangeCallback {
        public void onBrightnessLevelChanged();
    }

    /** ContentObserver to watch brightness **/
    private class BrightnessObserver extends ContentObserver {

        private final Uri BRIGHTNESS_MODE_URI =
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
        private final Uri BRIGHTNESS_URI =
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);

        public BrightnessObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (selfChange) return;
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
                updateMode();
            } else if (BRIGHTNESS_URI.equals(uri)) {
                updateSlider();
            } else {
                updateMode();
                updateSlider();
            }
            for (BrightnessStateChangeCallback cb : mChangeCallbacks) {
                cb.onBrightnessLevelChanged();
            }
        }

        public void startObserving() {
            final ContentResolver cr = mContext.getContentResolver();
            cr.unregisterContentObserver(this);
            cr.registerContentObserver(
                    BRIGHTNESS_MODE_URI,
                    false, this);
            cr.registerContentObserver(
                    BRIGHTNESS_URI,
                    false, this);
        }

        public void stopObserving() {
            final ContentResolver cr = mContext.getContentResolver();
            cr.unregisterContentObserver(this);
        }

    }

    public BrightnessController(Context context, ImageView icon, ToggleSlider control) {
        mContext = context;
        mIcon = icon;
        mControl = control;
        mHandler = new Handler();
        mDm = new DeviceManager(mContext);
        /*
        mUserTracker = new CurrentUserTracker(mContext) {
            @Override
            public void onUserSwitched(int newUserId) {
                updateMode();
                updateSlider();
            }
        };*/
        mBrightnessObserver = new BrightnessObserver(mHandler);
        mBrightnessObserver.startObserving();

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mMinimumBacklight = 5;
        mMaximumBacklight = 255;
        mAutomaticAvailable = false;
        //mAutomaticAvailable = context.getResources().getBoolean(
        //        com.android.internal.R.bool.config_automatic_brightness_available);

        // Update the slider and mode before attaching the listener so we don't receive the
        // onChanged notifications for the initial values.
        updateMode();
        updateSlider();

        control.setOnChangedListener(this);
    }

    public void addStateChangedCallback(BrightnessStateChangeCallback cb) {
        mChangeCallbacks.add(cb);
    }

    public boolean removeStateChangedCallback(BrightnessStateChangeCallback cb) {
        return mChangeCallbacks.remove(cb);
    }

    @Override
    public void onInit(ToggleSlider control) {
        // Do nothing
    }

    /** Unregister all call backs, both to and from the controller */
    public void unregisterCallbacks() {
        mBrightnessObserver.stopObserving();
        mChangeCallbacks.clear();
        //mUserTracker.stopTracking();
    }

    public void onChanged(ToggleSlider view, boolean tracking, boolean automatic, int value) {
        setMode(automatic ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        updateIcon(automatic);
        if (!automatic) {
            final int val = value + mMinimumBacklight;
            mDm.setBrightness(val);
            if (!tracking) {
                AsyncTask.execute(new Runnable() {
                        public void run() {
                            Settings.System.putInt(mContext.getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, val);
                        }
                    });
            }
        }

        for (BrightnessStateChangeCallback cb : mChangeCallbacks) {
            cb.onBrightnessLevelChanged();
        }
    }

    private void setMode(int mode) {
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
    }

    private void updateIcon(boolean automatic) {
        if (mIcon != null) {
            mIcon.setImageResource(automatic ?
                    R.drawable.ic_qs_brightness_auto_on :
                    R.drawable.ic_qs_brightness_auto_off);
        }
    }

    /** Fetch the brightness mode from the system settings and update the icon */
    private void updateMode() {
        if (mAutomaticAvailable) {
            int automatic;
            try {
                automatic = Settings.System.getInt(mContext.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (SettingNotFoundException snfe) {
                automatic = 0;
            }
            mControl.setChecked(automatic != 0);
            updateIcon(automatic != 0);
        } else {
            mControl.setChecked(false);
            updateIcon(false /*automatic*/);
        }
    }

    /** Fetch the brightness from the system settings and update the slider */
    private void updateSlider() {
        int value;
        try {
            value = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException ex) {
            value = mMaximumBacklight;
        }
        mControl.setMax(mMaximumBacklight - mMinimumBacklight);
        mControl.setValue(value - mMinimumBacklight);
    }

}
