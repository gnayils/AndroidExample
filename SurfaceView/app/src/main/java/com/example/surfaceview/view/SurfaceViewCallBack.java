package com.example.surfaceview.view;

import android.view.SurfaceHolder;

import com.example.surfaceview.network.NetworkListener;

import java.util.concurrent.BlockingQueue;

/**
 * Created by lf489159 on 12/8/15.
 */
public class SurfaceViewCallBack implements SurfaceHolder.Callback, NetworkListener {

    private SurfaceViewRenderer mRenderer;
    private BlockingQueue<byte[]> mFrameDataQueue;
    private boolean displaying;

    private SurfaceHolder mSurfaceHolder;
    private int mFormat;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public SurfaceViewCallBack(BlockingQueue<byte[]> frameDataQueue) {
        mFrameDataQueue = frameDataQueue;
    }

    private void start() {
        mRenderer = new SurfaceViewRenderer(mSurfaceHolder, mFrameDataQueue, mSurfaceWidth, mSurfaceHeight);
        mRenderer.start();
        displaying = true;
    }

    private void stop() {
        if (mRenderer != null) {
            mRenderer.stopRender();
        }
        mFrameDataQueue.clear();
        displaying = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        mSurfaceHolder = surfaceHolder;
        mFormat = format;
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        System.out.println("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //stop();
    }

    @Override
    public void onNetworkConnected() {
        start();
    }

    @Override
    public void onNetworkConnectionFailed() {

    }

    @Override
    public void onNetworkDisconnected() {
        stop();
    }
}
