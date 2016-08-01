package com.example.shutteranimation.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.example.shutteranimation.R;

/**
 * Created by Administrator on 2015/11/19.
 */
public class ShutterView extends GLSurfaceView implements SurfaceHolder.Callback{

    private Renderer mRenderer;

    public ShutterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

       }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mRenderer = new Renderer(holder, width, height);
        mRenderer.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mRenderer.stopRender();
    }


    private class Renderer extends Thread {

        private boolean rendering;
        private SurfaceHolder mHolder;
        private int mWidth;
        private int mHeight;

        private Bitmap mIrisShutterBitmap;
        private Paint mIrisShutterPaint;

        private float scale = 0.8f;

        private int mIrisShutterWidth;
        private int mIrisShutterHeight;
        private int mIrisShutterMaxAngleDegree;
        private int mIrisShutterMinAngleDegree;

        private int mCurrentIrisShutterAngleDegree;

        private int mAngleDegreeIncrement;

        private Matrix mMatrix = new Matrix();

        public Renderer(SurfaceHolder holder, int width, int height) {
            mHolder = holder;
            mWidth = width;
            mHeight = height;
            mIrisShutterBitmap = BitmapFactory.decodeResource(ShutterView.this.getResources(), R.mipmap.iris_shutter);
            mIrisShutterWidth = (int)(mIrisShutterBitmap.getWidth() * scale);
            mIrisShutterHeight = (int)(mIrisShutterBitmap.getHeight() * scale);
            mIrisShutterMaxAngleDegree = 42;
            mIrisShutterMinAngleDegree = -22;

            mCurrentIrisShutterAngleDegree = mIrisShutterMinAngleDegree;

            mAngleDegreeIncrement = 1;
            mIrisShutterPaint = new Paint();

        }

        @Override
        public void run() {
            rendering = true;
            while (rendering) {
                Canvas canvas = null;
                try {
                    canvas = mHolder.lockCanvas();
                    draw(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void draw(Canvas canvas) {
            long time = SystemClock.uptimeMillis();
            if(mCurrentIrisShutterAngleDegree > mIrisShutterMaxAngleDegree) {
                mAngleDegreeIncrement = 0 - mAngleDegreeIncrement;
            } else if(mCurrentIrisShutterAngleDegree < mIrisShutterMinAngleDegree) {
                mAngleDegreeIncrement = 0 - mAngleDegreeIncrement;
            }
            canvas.drawColor(Color.BLACK);
            canvas.translate(mWidth / 2, mHeight / 2);
            for(int i=0; i<10; i++) {
                canvas.rotate(-36);
                mMatrix.setScale(scale, scale);
                mMatrix.postRotate(mCurrentIrisShutterAngleDegree, 0, mIrisShutterHeight);
                mMatrix.postTranslate(0, -3);
                canvas.drawBitmap(mIrisShutterBitmap, mMatrix, null);
            }
            mCurrentIrisShutterAngleDegree += mAngleDegreeIncrement;
            System.out.println("elapsed time : " + (SystemClock.uptimeMillis() - time));
        }

        public void stopRender() {
            rendering = false;
        }
    }
}
