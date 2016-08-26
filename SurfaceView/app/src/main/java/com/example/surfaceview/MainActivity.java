package com.example.surfaceview;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.surfaceview.common.Constants;
import com.example.surfaceview.network.NetworkListener;
import com.example.surfaceview.network.NetworkService;
import com.example.surfaceview.view.SurfaceViewCallBack;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetworkListener {

    private Button mConnectButton;
    private EditText mHostEditText;
    private EditText mPortEditText;
    private SharedPreferences mSharedPreferences;
    private ServiceConnection mServiceConnection;
    private NetworkService mNetworkService;
    private SurfaceViewCallBack mImageFrameCallBack;
    private SurfaceView mImageFrameSurfaceView;
    private BlockingQueue<byte[]> mFrameDataQueue = new ArrayBlockingQueue<byte[]>(5);
    private  final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mImageFrameSurfaceView = (SurfaceView) findViewById(R.id.imageFrameSurfaceView);
        mConnectButton = (Button) findViewById(R.id.button_connect);
        mHostEditText = (EditText) findViewById(R.id.editText_host);
        mPortEditText = (EditText) findViewById(R.id.editText_port);

        mHostEditText.setText(mSharedPreferences.getString(Constants.PREF_KEY_HOST, ""));
        mPortEditText.setText(mSharedPreferences.getString(Constants.PREF_KEY_FRAME_DATA_PORT, ""));
        mConnectButton.setOnClickListener(this);
        mImageFrameCallBack = new SurfaceViewCallBack(mFrameDataQueue);
        mImageFrameSurfaceView.getHolder().addCallback(mImageFrameCallBack);
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                NetworkService.NetworkServiceBinder binder = (NetworkService.NetworkServiceBinder) iBinder;
                mNetworkService = binder.getService();
                mNetworkService.addNetworkListener(mImageFrameCallBack);
                mNetworkService.addNetworkListener(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(new Intent(this, NetworkService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        /**
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_connect:
                onConnectButtonClick(v);
                break;
        }
    }

    private void onConnectButtonClick(View v) {
        if (!mHostEditText.getText().toString().trim().isEmpty() && !mPortEditText.getText().toString().trim().isEmpty()) {
            String host = mHostEditText.getText().toString().trim();
            String frameDataPort = mPortEditText.getText().toString().trim();
            if (Pattern.matches("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))", host)
                    && 0 < Integer.valueOf(frameDataPort)
                    && Integer.valueOf(frameDataPort) < 65535) {

                mSharedPreferences.edit()
                        .putString(Constants.PREF_KEY_HOST, host)
                        .putString(Constants.PREF_KEY_FRAME_DATA_PORT, frameDataPort)
                        .commit();

                if(mNetworkService.isConnected()) {
                    mNetworkService.disconnect();
                } else {
                    mNetworkService.connect(host, Integer.parseInt(frameDataPort), mFrameDataQueue);
                }
            } else {
                Toast.makeText(this, "The value of Host or Port is invalid", Toast.LENGTH_SHORT);
            }

        } else {
            Toast.makeText(this, "Please input the value of Host and Port", Toast.LENGTH_SHORT);
        }
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

    @Override
    public void onNetworkConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectButton.setText("Disconnect");
                mHostEditText.setEnabled(false);
                mPortEditText.setEnabled(false);
            }
        });
    }

    @Override
    public void onNetworkConnectionFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "connect to the server failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNetworkDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectButton.setText("Connect");
                mHostEditText.setEnabled(true);
                mPortEditText.setEnabled(true);
            }
        });

    }
}
