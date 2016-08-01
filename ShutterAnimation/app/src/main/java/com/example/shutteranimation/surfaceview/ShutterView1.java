package com.example.shutteranimation.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.shutteranimation.R;

/**
 * Created by Administrator on 2015/11/19.
 */
public class ShutterView1 extends SurfaceView implements SurfaceHolder.Callback{

    private Renderer mRenderer;

    public ShutterView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        getHolder().setFormat(0x00000004);
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

        private Bitmap mShutterBlade;
        private Paint mBladePaint;

        private float scale = 0.2f;

        private int mBladeWidth;
        private int mBladeHeight;
        private int mMaxBladeDegree;
        private int mMinBladeDegree;
        private int mMaxBladeTranslateX;
        private int mMinBladeTranslateX;
        private int mMaxBladeTranslateY;
        private int mMinBladeTranslateY;

        private int mCurBladeDegree;
        private int mCurBladeTranslateX;
        private int mCurBladeTranslateY;

        private int mDegreeIncrement;
        private int mTranslateXIncrement;
        private int mTranslateYIncrement;

        private Matrix mMatrix = new Matrix();

        public Renderer(SurfaceHolder holder, int width, int height) {
            mHolder = holder;
            mWidth = width;
            mHeight = height;
            mShutterBlade = BitmapFactory.decodeResource(ShutterView1.this.getResources(), R.mipmap.iris_shutter);
            mBladeWidth = (int)(mShutterBlade.getWidth() * scale);
            mBladeHeight = (int)(mShutterBlade.getHeight() * scale);
            mMaxBladeDegree = 42;
            mMinBladeDegree = 2;
            mMaxBladeTranslateX = 0;
            mMinBladeTranslateX = mMaxBladeTranslateX - mBladeWidth / 2;
            mMaxBladeTranslateY = mBladeHeight / 2;
            mMinBladeTranslateY = 0;

            mCurBladeDegree = mMinBladeDegree;
            mCurBladeTranslateX = mMinBladeTranslateX;
            mCurBladeTranslateY = mMinBladeTranslateY;

            mDegreeIncrement = 2;
            mTranslateXIncrement = (mMaxBladeTranslateX - mMinBladeTranslateX) / (mMaxBladeDegree - mMinBladeDegree);
            mTranslateYIncrement = (mMaxBladeTranslateY - mMinBladeTranslateY) / (mMaxBladeDegree - mMinBladeDegree);
            mBladePaint = new Paint();

        }

        @Override
        public void run() {
            rendering = true;
            Canvas canvas = null;
            while (rendering) {
                /*Canvas canvas = null;
                try {
                    canvas = mHolder.lockCanvas();
                    draw(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }*/
                canvas = mHolder.lockCanvas();
                draw(canvas);
                mHolder.unlockCanvasAndPost(canvas);
            }
        }

        private void draw(Canvas canvas) {
            long time = SystemClock.uptimeMillis();
            if(mCurBladeDegree > mMaxBladeDegree) {
                mDegreeIncrement = 0 - mDegreeIncrement ;
                mTranslateXIncrement = 0 - mTranslateXIncrement;
                mTranslateYIncrement = 0 - mTranslateYIncrement;
            } else if(mCurBladeDegree < mMinBladeDegree) {
                mDegreeIncrement = 0 - mDegreeIncrement;
                mTranslateXIncrement = 0 - mTranslateXIncrement;
                mTranslateYIncrement = 0 - mTranslateYIncrement;
            }

            canvas.drawColor(Color.WHITE);
            canvas.translate(mWidth / 2, mHeight / 2);
            for(int i=0; i<12; i++) {
                canvas.rotate(-30);
                mMatrix.setScale(scale, scale);
                mMatrix.postRotate(mCurBladeDegree, mBladeWidth, mBladeHeight);
                mMatrix.postTranslate(mCurBladeTranslateX, mCurBladeTranslateY);
                canvas.drawBitmap(mShutterBlade, mMatrix, null);
            }
            mCurBladeDegree += mDegreeIncrement;
            mCurBladeTranslateX += mTranslateXIncrement;
            mCurBladeTranslateY += mTranslateYIncrement;
            System.out.println("frame speed: " + (SystemClock.uptimeMillis() - time));
        }

        public void stopRender() {
            rendering = false;
        }

    }
}
