package com.example.surfaceview.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.BlockingQueue;

/**
 * Created by lf489159 on 12/8/15.
 */
public class SurfaceViewRenderer extends Thread {

    public final String TAG = this.getClass().getName();

    private SurfaceHolder mSurfaceHolder;
    private BlockingQueue<byte[]> mFrameDataQueue;
    private float mSurfaceWidth;
    private float mSurfaceHeight;
    private float translateX;
    private float translateY;
    private float textSize = 48;

    private boolean mRendering;
    private Matrix mMatrix;
    private Paint mFPSPaint = new Paint();

    public SurfaceViewRenderer(SurfaceHolder surfaceHolder, BlockingQueue<byte[]> queue, int width, int height) {
        mSurfaceHolder = surfaceHolder;
        mFrameDataQueue = queue;
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mFPSPaint.setColor(Color.YELLOW);
        mFPSPaint.setTextSize(textSize);
    }

    @Override
    public void run() {
        int fpsCounter = 0;
        int fps = 0;
        long startTime = SystemClock.uptimeMillis();
        mRendering = true;
        while (!isInterrupted() && mRendering) {
            Bitmap bitmap = null;
            Canvas canvas = null;
            try {
                byte[] frameData = mFrameDataQueue.take();
                bitmap = BitmapFactory.decodeByteArray(frameData, 0, frameData.length);
                canvas = mSurfaceHolder.lockCanvas();
                if(mMatrix == null) {
                    mMatrix = new Matrix();
                    float surfaceViewRatio = mSurfaceWidth / mSurfaceHeight;
                    float bitmapRatio = bitmap.getWidth() / bitmap.getHeight();
                    float scale = 0;
                    if (surfaceViewRatio < bitmapRatio) {
                        scale = mSurfaceWidth / bitmap.getWidth();
                        translateY = (mSurfaceHeight - bitmap.getHeight() * scale) / 2;
                    } else {
                        scale = mSurfaceHeight / bitmap.getHeight();
                        translateX = (mSurfaceWidth - bitmap.getWidth() * scale) / 2;
                    }
                    mMatrix.setScale(scale, scale);
                    mMatrix.postTranslate(translateX, translateY);
                }
                if(canvas != null) {
                    canvas.drawBitmap(bitmap, mMatrix, null);
                    canvas.drawText("FPS: " + fps, translateX, translateY + textSize, mFPSPaint);
                }
            } catch (Exception e) {
                Log.e(TAG, "Drawing canvas failed", e);
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            if (SystemClock.uptimeMillis() - startTime >= 500) {
                fps = fpsCounter * 2;
                fpsCounter = -1;
                startTime = SystemClock.uptimeMillis();
            }
            fpsCounter++;
        }
    }


    public void stopRender() {
        mRendering = false;
        try {
            join(100);
        } catch (InterruptedException e) {
        }
        if (isAlive()) {
            interrupt();
        }
    }
}

