package com.gnayils.example;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

/**
 * Created by iUser on 11/9/16.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private static final String TAG = MyGLSurfaceView.class.getName();

    private final MyGLRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    private double previousAngle = -1;
    private double radius;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer(context);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                radius = getWidth() / 2d;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(e.getPointerCount() > 1) {
                    previousAngle = getAngle(e);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(e.getPointerCount() > 1) {
                    float currentAngle = getAngle(e);
                    renderer.setAngle((float) (renderer.getAngle() + (currentAngle - previousAngle)));
                    requestRender();
                    previousAngle = currentAngle;
                    for (int i = 0; i < e.getPointerCount(); i++) {
                        System.out.print("event" + i + ": " + String.format("%03d", Math.round(e.getX(i))) + ", " + String.format("%03d", Math.round(e.getY(i))) + "\t");
                    }
                    System.out.println();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private float getAngle(MotionEvent event) {
        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);
        float dx = x0 - x1;
        float dy = y0 - y1;
        double radian = Math.atan2(dy, dx);
        double angle = Math.toDegrees(radian);
        return (float) ((360 + angle) % 360);
    }

    /**
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousAngle = getAngle(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                float currentAngle = getAngle(x, y);
                renderer.setAngle((float) (renderer.getAngle() + (currentAngle - previousAngle)));
                requestRender();
                previousAngle = currentAngle;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private float getAngle(float x, float y) {
        double radian = Math.atan2(x - radius, radius - y);
        double angle = Math.toDegrees(radian);
        return (float) ((360 + angle) % 360);
    }
    */

    /**
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                double a = Math.abs(y - radius);
                double b = Math.abs(x - radius);
                double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
                double radian = Math.asin(a / c);
                double angle = Math.toDegrees(radian);
                Log.d(TAG, "angle: " + angle);
                break;
        }
        previousX = x;
        previousY = y;
        return true;
    }
    */
    /**
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                renderer.setAngle(renderer.getAngle() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
                break;
        }
        previousX = x;
        previousY = y;
        return true;
    }
    */
}
