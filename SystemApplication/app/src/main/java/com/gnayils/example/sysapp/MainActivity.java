package com.gnayils.example.sysapp;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends PreferenceActivity {
	private final static String KEY_DREAM = "key_dream";
	private final static String KEY_WAKEUP = "key_wakeup";
	private final static String KEY_POWEROFF = "key_poweroff";
	private DeviceManager mDm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mDm = new DeviceManager(this);
        addPreferencesFromResource(R.xml.preference);
    }
    
    @Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
    	if(preference == null || preference.getKey() == null)
    		return false;
		if (preference.getKey().equals(KEY_DREAM)) {
			mDm.dream();
		} else if (preference.getKey().equals(KEY_WAKEUP)) {
			mDm.wakeup();
		} else if (preference.getKey().equals(KEY_POWEROFF)) {
			mDm.poweroff();
		} else {
			return false;
		}
		return true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
