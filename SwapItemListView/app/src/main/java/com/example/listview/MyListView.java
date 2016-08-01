package com.example.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/3/19.
 */
public class MyListView extends ListView {

    private WindowManager windowManager;
    private int offsetYInDragHandle;
    private int offsetYInScreen;
    private ImageView dragImageView;
    private WindowManager.LayoutParams layoutParams;

    private boolean isDragging = false;

    private int xInScreen;
    private int yInScreen;

    private int moveX;
    private int moveY;

    private ViewGroup fromItem;
    private int fromPosition;
    private int toPosition;

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.windowAnimations = 0;
        //layoutParams.windowAnimations = android.R.style.Animation_Toast;

        dragImageView = new ImageView(getContext());
        dragImageView.setBackgroundColor(Color.TRANSPARENT);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] locationInWindow = new int[2];
                MyListView.this.getLocationInWindow(locationInWindow);
                xInScreen = locationInWindow[0];
                yInScreen = locationInWindow[1];
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                fromPosition = pointToPosition(x, y);
                if (fromPosition == AdapterView.INVALID_POSITION) {
                    break;
                }
                fromItem = (ViewGroup) getChildAt(fromPosition - getFirstVisiblePosition());
                View dragHandle = fromItem.findViewById(R.id.icon);
                if (dragHandle.getLeft() < x && x < dragHandle.getRight()) {
                    startDragging(ev);
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isDragging) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    duringDragging();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopDragging();
                    break;
            }
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    private void startDragging(MotionEvent ev) {
        if(!isDragging) {
            offsetYInDragHandle = (int) (ev.getY() - fromItem.getTop());
            offsetYInScreen = (int) (ev.getRawY() - ev.getY());
            fromItem.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(fromItem.getDrawingCache());
            layoutParams.x = fromItem.getLeft();
            layoutParams.y = (int) (ev.getY() + offsetYInScreen - offsetYInDragHandle);
            dragImageView.setImageBitmap(bitmap);
            windowManager.addView(dragImageView, layoutParams);
            fromItem.setVisibility(View.INVISIBLE);
            isDragging = true;
        }
    }

    private void duringDragging() {
        if(isDragging) {
            layoutParams.y = moveY + offsetYInScreen - offsetYInDragHandle;
            if(yInScreen < layoutParams.y && layoutParams.y < yInScreen + this.getHeight()) {
                windowManager.updateViewLayout(dragImageView, layoutParams);
                post(scrollRunnable);
            }
        }
    }

    private void onSwapItem() {
        toPosition = pointToPosition(moveX, moveY);
        if(toPosition != AdapterView.INVALID_POSITION && toPosition != fromPosition) {
            int childIndex = toPosition - getFirstVisiblePosition();
            ViewGroup child = (ViewGroup) getChildAt(childIndex);
            ((MyListViewAdapter)getAdapter()).dataChanged(fromPosition, toPosition);
            getChildAt(fromPosition - getFirstVisiblePosition()).setVisibility(View.VISIBLE);
            getChildAt(toPosition - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);
            fromPosition = toPosition;
        }
    }

    private  Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollY;
            if(moveY < MyListView.this.getHeight() / 5) {
                scrollY = -20;
                MyListView.this.postDelayed(scrollRunnable, 25);
            } else if(moveY >  MyListView.this.getHeight() * 4 / 5) {
                scrollY = 20;
                MyListView.this.postDelayed(scrollRunnable, 25);
            } else{
                scrollY = 0;
                MyListView.this.removeCallbacks(scrollRunnable);
            }
            onSwapItem();
            smoothScrollBy(scrollY, 10);
        }
    };

    private void stopDragging() {
        if(isDragging) {
            if (dragImageView.getDrawable() != null) {
                windowManager.removeView(dragImageView);
                ((BitmapDrawable) dragImageView.getDrawable()).getBitmap().recycle();
                dragImageView.setImageDrawable(null);
            }

            removeCallbacks(scrollRunnable);
            View view = getChildAt(fromPosition - getFirstVisiblePosition());
            if(view != null) {
                view.setVisibility(View.VISIBLE);
            }
            isDragging = false;
        }
    }

    public static abstract class MyListViewAdapter extends BaseAdapter {

        public abstract void onItemChange(int from , int to);

        public void dataChanged(int from, int to) {
            onItemChange(from, to);
            notifyDataSetChanged();
        }
    }
}
