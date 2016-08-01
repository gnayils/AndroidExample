package com.gnayils.example.sysapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.service.dreams.IDreamManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class must compile with the android source code
 */
public class DeviceManager {

    private static final String TAG = DeviceManager.class.getName();

    private Context mContext;
    private final IDreamManager mDreamManager;
    private final IPowerManager mPowerManager;
    private final DeviceManager.DreamInfoComparator mComparator;

    public DeviceManager(Context context) {
        this.mContext = context;
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
        this.mPowerManager = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
        this.mComparator = new DeviceManager.DreamInfoComparator(this.getDefaultDream());
    }

    public void dream() {
        if(this.mDreamManager != null) {
            try {
                if(this.getActiveDream() == null) {
                    List e = this.getDreamInfos();
                    if(e.size() != 0) {
                        this.setActiveDream((ComponentName)e.get(0));
                    } else {
                        Log.w(TAG, "Failed to dream");
                    }
                }

                this.mDreamManager.dream();
            } catch (RemoteException var2) {
                Log.w(TAG, "Failed to dreaandroid.provider.Settings.Systemm", var2);
            }

        }
    }

    public void wakeup() {
        try {
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis());
        } catch (RemoteException var2) {
            Log.w(TAG, "Failed to wakeup", var2);
        }

    }

    public void poweroff() {
        try {
            this.mPowerManager.shutdown(false, false);
        } catch (RemoteException var2) {
            Log.w(TAG, "Failed to poweroff", var2);
        }

    }

    public void setBrightness(int brightness) {
        try {
            this.mPowerManager.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException var3) {
            Log.w(TAG, "Failed to set screen brightness", var3);
        }
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness", brightness, -2);
    }

    private ComponentName getDreamComponentName(ResolveInfo resolveInfo) {
        return resolveInfo != null && resolveInfo.serviceInfo != null?new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name):null;
    }

    private List<ComponentName> getDreamInfos() {
        Log.w(TAG, "getDreamInfos()");
        ComponentName activeDream = this.getActiveDream();
        PackageManager pm = this.mContext.getPackageManager();
        Intent dreamIntent = new Intent("android.service.dreams.DreamService");
        List resolveInfos = pm.queryIntentServices(dreamIntent, 128);
        ArrayList cn = new ArrayList(resolveInfos.size());
        Iterator i$ = resolveInfos.iterator();

        while(i$.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo)i$.next();
            if(resolveInfo.serviceInfo != null) {
                cn.add(this.getDreamComponentName(resolveInfo));
            }
        }

        Collections.sort(cn, this.mComparator);
        return cn;
    }

    private void setActiveDream(ComponentName dream) {
        Log.w(TAG, "setActiveDream(" + dream + ")");
        if(this.mDreamManager != null) {
            try {
                ComponentName[] e = new ComponentName[]{dream};
                this.mDreamManager.setDreamComponents(dream == null?null:e);
            } catch (RemoteException var3) {
                Log.w(TAG, "Failed to set active dream to " + dream, var3);
            }

        }
    }

    private ComponentName getActiveDream() {
        if(this.mDreamManager == null) {
            return null;
        } else {
            try {
                ComponentName[] e = this.mDreamManager.getDreamComponents();
                return e != null && e.length > 0?e[0]:null;
            } catch (RemoteException var2) {
                Log.w(TAG, "Failed to get active dream", var2);
                return null;
            }
        }
    }

    private ComponentName getDefaultDream() {
        if(this.mDreamManager == null) {
            return null;
        } else {
            try {
                return this.mDreamManager.getDefaultDreamComponent();
            } catch (RemoteException var2) {
                Log.w(TAG, "Failed to get default dream", var2);
                return null;
            }
        }
    }

    private static class DreamInfoComparator implements Comparator<ComponentName> {
        private final ComponentName mDefaultDream;

        public DreamInfoComparator(ComponentName defaultDream) {
            this.mDefaultDream = defaultDream;
        }

        public int compare(ComponentName lhs, ComponentName rhs) {
            return this.sortKey(lhs).compareTo(this.sortKey(rhs));
        }

        private String sortKey(ComponentName componentName) {
            StringBuilder sb = new StringBuilder();
            sb.append((char)(componentName.equals(this.mDefaultDream)?'0':'1'));
            return sb.toString();
        }
    }
}
