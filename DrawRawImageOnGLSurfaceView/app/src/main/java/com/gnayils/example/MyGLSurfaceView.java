package com.gnayils.example;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                mRenderer.setAngle(mRenderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
