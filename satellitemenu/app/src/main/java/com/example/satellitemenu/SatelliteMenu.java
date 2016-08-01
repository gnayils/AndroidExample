package com.example.satellitemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/3.
 */
public class SatelliteMenu extends FrameLayout {

    private static final String TAG = SatelliteMenu.class.getSimpleName();

    private int defaultExpandDistance = 500;
    private int defaultExpandDuration = 400;
    private float defaultExpandDegree = 90f;

    private boolean isExpanded = false;

    private ImageView mainButton;
    private Animation mainButtonExpandAnim;
    private Animation mainButtonCollapseAnim;
    private Drawable mainButtonDrawable;
    private List<MenuItem> miList = new ArrayList<MenuItem>();

    public SatelliteMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SatelliteMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mainButton = new ImageView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        mainButton.setLayoutParams(lp);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SatelliteMenu, defStyle, 0);
            mainButtonDrawable = ta.getDrawable(R.styleable.SatelliteMenu_mainButtonImage);
            mainButton.setImageDrawable(mainButtonDrawable);
            ta.recycle();
        }
        mainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    collapse();
                } else {
                    expand();
                }
            }
        });
        mainButtonExpandAnim = AnimationUtils.loadAnimation(context, R.anim.sat_main_button_rotate_left);
        mainButtonExpandAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isExpanded = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mainButtonCollapseAnim = AnimationUtils.loadAnimation(context, R.anim.sat_main_button_rotate_right);
        mainButtonCollapseAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isExpanded = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void addMenuItem(List<MenuItem> miList) {
        for (MenuItem mi : this.miList) {
            this.removeView(mi.imageView);
        }
        this.miList.clear();

        this.miList.addAll(miList);
        float[] degrees = createDegrees(miList.size(), defaultExpandDegree);
        for (int i = 0; i < this.miList.size(); i++) {
            MenuItem mi = this.miList.get(i);
            float degree = degrees[i];
            int finalX = getTranslateX(degree, defaultExpandDistance);
            int finalY = getTranslateY(degree, defaultExpandDistance);
            mi.itemExpandAnimation = createMenuItemExpandAnimation(i, finalX, finalY, defaultExpandDuration);
            mi.itemCollapseAnimation = createMenuItemCollapseAnimation(i, finalX, finalY, defaultExpandDuration);
            mi.itemExpandAnimation.setAnimationListener(new MenuItemAnimationListener(mi, true));
            mi.itemCollapseAnimation.setAnimationListener(new MenuItemAnimationListener(mi, false));
            mi.imageView = new ImageView(getContext());
            mi.imageView.setLayoutParams(mainButton.getLayoutParams());
            mi.imageView.setImageResource(mi.resId);
            mi.imageView.setVisibility(View.INVISIBLE);
            this.addView(mi.imageView);

            mi.clonedImageView = new ImageView(getContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
            lp.bottomMargin = Math.abs(finalY);
            lp.leftMargin = Math.abs(finalX);
            mi.clonedImageView.setLayoutParams(lp);
            mi.clonedImageView.setImageResource(mi.resId);
            mi.clonedImageView.setVisibility(View.INVISIBLE);
            mi.clonedImageView.setOnClickListener(new MenuItemOnClickListener(mi));

            mi.itemClickAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.sat_menu_item_click);
            mi.itemClickAnimation.setAnimationListener(new MenuItemClickAnimationListener(this));

            this.addView(mi.clonedImageView);
        }
        this.addView(mainButton);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int itemWidth = 0, itemHeight = 0;
        if (!miList.isEmpty()) {
            itemWidth = miList.get(0).imageView.getWidth();
            itemHeight = miList.get(0).imageView.getHeight();
        }
        int totalWidth = mainButton.getWidth() + defaultExpandDistance + itemWidth;
        int totalHeight = mainButton.getHeight() + defaultExpandDistance + itemHeight;
        setMeasuredDimension(totalWidth, totalHeight);
    }

    private void expand() {
        mainButton.startAnimation(mainButtonExpandAnim);
        for (MenuItem mi : miList) {
            mi.imageView.startAnimation(mi.itemExpandAnimation);
        }
    }

    private void collapse() {
        mainButton.startAnimation(mainButtonCollapseAnim);
        for (MenuItem mi : miList) {
            mi.imageView.startAnimation(mi.itemCollapseAnimation);
        }
    }

    private Animation createMenuItemExpandAnimation(int index, int x, int y, int duration) {
        Animation rotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(duration);
        rotateAnim.setInterpolator(getContext(), R.anim.sat_menu_item_rotate_interpolator);

        Animation translateAnim = new TranslateAnimation(0, x, 0, y);
        translateAnim.setDuration(duration);
        translateAnim.setInterpolator(getContext(), R.anim.sat_menu_item_overshoot_interpolator);

        AnimationSet animSet = new AnimationSet(false);
        animSet.setFillAfter(false);
        animSet.setFillBefore(true);
        animSet.setFillEnabled(true);
        animSet.addAnimation(rotateAnim);
        animSet.addAnimation(translateAnim);
        animSet.setStartOffset(index * 30);

        return animSet;
    }

    private Animation createMenuItemCollapseAnimation(int index, int x, int y, int duration) {
        Animation rotateAnim = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(duration);
        rotateAnim.setInterpolator(getContext(), R.anim.sat_menu_item_rotate_interpolator);

        Animation translateAnim = new TranslateAnimation(x, 0, y, 0);
        translateAnim.setDuration(duration);
        translateAnim.setInterpolator(getContext(), R.anim.sat_menu_item_anticipate_interpolator);

        AnimationSet animSet = new AnimationSet(false);
        animSet.setFillBefore(true);
        animSet.setFillAfter(false);
        animSet.setFillEnabled(true);
        animSet.addAnimation(rotateAnim);
        animSet.addAnimation(translateAnim);
        animSet.setStartOffset(index * 30);

        return animSet;
    }

    public static int getTranslateX(float degree, int distance) {
        return Double.valueOf(distance * Math.cos(Math.toRadians(degree))).intValue();
    }

    public static int getTranslateY(float degree, int distance) {
        return Double.valueOf(-1 * distance * Math.sin(Math.toRadians(degree))).intValue();
    }

    public static float[] createDegrees(int count, float totalDegree) {
        float[] degrees = new float[count];
        int tmpCount = count;
        if (count < 3) {
            tmpCount = count + 1;
        } else {
            tmpCount = count - 1;
        }
        float averageDegree = totalDegree / tmpCount;
        for (int i = 0; i < degrees.length; i++) {
            if (count < 3) {
                degrees[i] = (i + 1) * averageDegree;
            } else {
                degrees[i] = i * averageDegree;
            }
        }
        return degrees;
    }

    private static class MenuItemAnimationListener implements Animation.AnimationListener {

        private MenuItem mi;
        private boolean isExpandAnimation;

        public MenuItemAnimationListener(MenuItem mi, boolean isExpandAnimation) {
            this.mi = mi;
            this.isExpandAnimation = isExpandAnimation;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if(!isExpandAnimation) {
                mi.imageView.setVisibility(View.VISIBLE);
                mi.clonedImageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isExpandAnimation) {
                mi.imageView.setVisibility(View.INVISIBLE);
                mi.clonedImageView.setVisibility(View.VISIBLE);
            } else {
                mi.imageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public static class MenuItemOnClickListener implements OnClickListener {

        private MenuItem mi;

        public MenuItemOnClickListener(MenuItem mi) {
            this.mi = mi;
        }

        @Override
        public void onClick(View v) {
            v.startAnimation(mi.itemClickAnimation);
        }
    }

    public static class MenuItemClickAnimationListener implements Animation.AnimationListener {


        public SatelliteMenu satMenu;

        public MenuItemClickAnimationListener(SatelliteMenu satMenu) {
            this.satMenu = satMenu;
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            satMenu.collapse();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    public static class MenuItem {

        public ImageView imageView;
        public ImageView clonedImageView;
        public int resId;
        public Animation itemExpandAnimation;
        public Animation itemCollapseAnimation;
        public Animation itemClickAnimation;

        public MenuItem(int resId) {
            this.resId = resId;
        }
    }
}
