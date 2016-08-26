package com.example.surfaceview.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by lf489159 on 12/7/15.
 */
public class NetworkService extends Service{

    private Networker networker;
    private NetworkServiceBinder binder = new NetworkServiceBinder();
    private Set<NetworkListener> mListeners = new HashSet<NetworkListener>();
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    public void connect(String ip, int frameDataPort, BlockingQueue<byte[]> frameDataQueue) {
        networker = new Networker(ip, frameDataPort, frameDataQueue, mListeners);
        networker.start();
    }

    public boolean isConnected() {
        return networker == null ? false : networker.isNetworking();
    }

    public void disconnect() {
        if(networker != null) {
            networker.stopNetwork();
            networker = null;
        }
    }

    public void addNetworkListener(NetworkListener listener) {
        mListeners.add(listener);
    }

    public void removeNetworkListener(NetworkListener listener) {
        mListeners.remove(listener);
    }

    public void removeAllNetworkListener() {
        mListeners.clear();
    }


        @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        Log.d(TAG, "onDestroy");
    }

    public class NetworkServiceBinder extends Binder {

        public NetworkService getService() {
            return NetworkService.this;
        }

    }
}
