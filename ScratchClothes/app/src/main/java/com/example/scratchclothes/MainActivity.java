package com.example.scratchclothes;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements ViewSwitcher.ViewFactory {

    private ImageSwitcher imageSwitcher;
    private Gallery gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        imageSwitcher.setFactory(this);
        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(MainActivity.this));
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int drawableId = 0;
                try {
                    drawableId = R.mipmap.class.getDeclaredField("pre" + position).getInt(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                releaseBitmap();
                imageSwitcher.setImageResource(drawableId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("imagePosition", gallery.getSelectedItemPosition());
                intent.setClass(MainActivity.this, ScrawlActivity.class);
                startActivity(intent);
            }
        });
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

    private void releaseBitmap() {
        ImageAdapter adapter = (ImageAdapter) gallery.getAdapter();
        Map<Integer, Bitmap> datacache = adapter.getDatacache();
        int start = gallery.getFirstVisiblePosition() - 2;
        int end = gallery.getLastVisiblePosition() + 2;
        Bitmap bitmap;
        for (int del = 0; del < start; del++) {
            bitmap = datacache.get(del);
            if (bitmap != null) {
                datacache.remove(del);
                bitmap.recycle();
            }
        }
        freeBitmapFromIndex(end, datacache);
    }

    private void freeBitmapFromIndex(int end, Map<Integer, Bitmap> datacache) {
        Bitmap bitmap;
        for (int del = end + 1; del < datacache.size(); del++) {
            bitmap = datacache.get(del);
            if (bitmap != null) {
                datacache.remove(bitmap);
                bitmap.recycle();
            }
        }
    }


    private class ImageAdapter extends BaseAdapter {

        private Context context;
        private Map<Integer, Bitmap> datacache = new HashMap<Integer, Bitmap>();

        public ImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 11;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new Gallery.LayoutParams(120, 120));
            } else {
                imageView = (ImageView) convertView;
            }

            Bitmap current = datacache.get(position);
            if (current != null) {
                imageView.setImageBitmap(current);
            } else {
                int drawableId = 0;
                try {
                    drawableId = R.mipmap.class.getDeclaredField("pre" + position).getInt(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                current = readBitmap(context, drawableId);
                imageView.setImageBitmap(current);
                datacache.put(position, current);
            }
            return imageView;
        }

        public Map<Integer, Bitmap> getDatacache() {
            return datacache;
        }
    }


    @Override
    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(0xff000000);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    public static Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, options);
    }
}
