package com.example.satellitemenu;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SatelliteMenu sm = (SatelliteMenu) this.findViewById(R.id.satellitemenu);
        List<SatelliteMenu.MenuItem> miList = new ArrayList<SatelliteMenu.MenuItem>();
        miList.add(new SatelliteMenu.MenuItem(R.mipmap.ic_1));
        miList.add(new SatelliteMenu.MenuItem(R.mipmap.ic_2));
        miList.add(new SatelliteMenu.MenuItem(R.mipmap.ic_3));
        miList.add(new SatelliteMenu.MenuItem(R.mipmap.ic_4));
        miList.add(new SatelliteMenu.MenuItem(R.mipmap.ic_5));
        miList.add(new SatelliteMenu.MenuItem(R.mipmap.ic_6));
        sm.addMenuItem(miList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
