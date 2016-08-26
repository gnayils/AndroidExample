package com.example.surfaceview.network;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by lf489159 on 12/8/15.
 */
public class Networker extends Thread {

    public final String TAG = this.getClass().getSimpleName();

    private String mIp;
    private int mPort;
    private Socket mSocket;
    private InputStream mFrameDataInputStream;
    private BlockingQueue<byte[]> mFrameDataQueue;

    private boolean mNetworking;
    public static final int HEADER_LENGTH = 4;
    public static final int BUFFER_LENGTH = 1024;
    private byte[] mHeaderData = new byte[HEADER_LENGTH];
    private byte[] mDataBuffer = new byte[BUFFER_LENGTH];
    private ByteBuffer mHeaderDataParser = ByteBuffer.allocate(HEADER_LENGTH);
    private Set<NetworkListener> mListeners;

    public Networker(String ip, int port, BlockingQueue<byte[]> frameDataQueue, Set<NetworkListener> listeners) {
        mIp = ip;
        mPort = port;
        mFrameDataQueue = frameDataQueue;
        mListeners = listeners;
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket(mIp, mPort);
            mFrameDataInputStream = mSocket.getInputStream();
        } catch (IOException e) {
            notifyNetworkStatus(NetworkListener.STATUS_CONNECTION_FAILED);
            Log.e(TAG, "Connect to the server failed", e);
            return;
        }
        mNetworking = true;
        notifyNetworkStatus(NetworkListener.STATUS_CONNECTED);
        while (!isInterrupted() && mNetworking) {
            try {
                mFrameDataQueue.offer(readFrameData());
            } catch (Exception e) {
                stopNetwork();
                notifyNetworkStatus(NetworkListener.STATUS_CONNECTION_FAILED);
                Log.e(TAG, "Read frame data failed", e);
                break;
            }
        }
        Log.i(TAG, "networking finished");
    }

    private byte[] readFrameData() throws IOException {
        readFrom(mFrameDataInputStream, mHeaderData);
        mHeaderDataParser.clear();
        mHeaderDataParser.put(mHeaderData);
        mHeaderDataParser.flip();
        int frameDataLength = mHeaderDataParser.getInt();

        ByteBuffer frameDataBuffer = ByteBuffer.allocate(frameDataLength);
        for (int i = 0; i < frameDataLength / BUFFER_LENGTH; i++) {
            readFrom(mFrameDataInputStream, mDataBuffer);
            frameDataBuffer.put(mDataBuffer);
        }
        byte[] remainData = new byte[frameDataLength % BUFFER_LENGTH];
        readFrom(mFrameDataInputStream, remainData);
        frameDataBuffer.put(remainData);
        return frameDataBuffer.array();
    }

    private void readFrom(InputStream is, byte[] buffer) throws IOException {
        int readLength = 0;
        while (readLength < buffer.length) {
            readLength += is.read(buffer, readLength, buffer.length - readLength);
        }
    }

    public boolean isNetworking() {
        return mNetworking;
    }

    public void stopNetwork() {
        mNetworking = false;
        try {
            join(100);
        } catch (InterruptedException e) {
        }
        if (isAlive()) {
            interrupt();
        }
        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notifyNetworkStatus(NetworkListener.STATUS_DISCONNECTED);
    }

    private void notifyNetworkStatus(int status) {
        for (NetworkListener listener : mListeners) {
            try {
                switch(status) {
                    case NetworkListener.STATUS_DISCONNECTED:
                        listener.onNetworkDisconnected();
                        break;
                    case NetworkListener.STATUS_CONNECTED:
                        listener.onNetworkConnected();
                        break;
                    case NetworkListener.STATUS_CONNECTION_FAILED:
                        listener.onNetworkConnectionFailed();
                        break;
                }
            } catch (Exception e) {
            }
        }
    }
}
