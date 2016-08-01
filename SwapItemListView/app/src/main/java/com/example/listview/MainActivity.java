package com.example.listview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new MyAdapter();
        MyListView mlv = (MyListView) findViewById(R.id.my_list_view);
        mlv.setAdapter(adapter);
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

    private class MyAdapter extends MyListView.MyListViewAdapter {

        private ArrayList<Map<String, Object>> array;

        public MyAdapter() {
            array = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < 26; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", "Title " + /**(char) ('A' + i)*/i);
                map.put("desc", "Description " + /**(char) ('A' + i)*/i);
                map.put("icon", R.mipmap.ic_launcher);
                array.add(map);
            }
        }

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return array.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.desc = (TextView) convertView.findViewById(R.id.desc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, Object> map = getItem(position);
            holder.icon.setImageResource((int) map.get("icon"));
            holder.title.setText((String) map.get("title"));
            holder.desc.setText((String) map.get("desc"));
            return convertView;
        }

        @Override
        public void onItemChange(int from, int to) {
            Map<String, Object> temp = array.get(from);
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(array, i, i + 1);
                }
            } else if (from > to) {
                for (int i = from; i > to; i--) {
                    Collections.swap(array, i, i - 1);
                }
            }
            array.set(to, temp);
        }

        class ViewHolder {
            ImageView icon;
            TextView title;
            TextView desc;
        }

    }
}
