package com.example.surfaceview.network;

/**
 * Created by lf489159 on 6/15/16.
 */
public interface NetworkListener {

    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_CONNECTION_FAILED = 2;

    public void onNetworkConnected();

    public void onNetworkConnectionFailed();

    public void onNetworkDisconnected();
}
