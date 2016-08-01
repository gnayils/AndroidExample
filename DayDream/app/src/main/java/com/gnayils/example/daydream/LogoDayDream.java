package com.gnayils.example.daydream;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.service.dreams.DreamService;
import android.widget.ImageView;

public class LogoDayDream extends DreamService {

    private AnimatorSet animatorSet;
    private ImageView logoDecoratorImageView;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFullscreen(true);
        setContentView(R.layout.day_dream_log);
        animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.spin);
        logoDecoratorImageView = (ImageView) findViewById(R.id.image_view_logo_decorator);
        animatorSet.setTarget(logoDecoratorImageView);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        animatorSet.start();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        animatorSet.cancel();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
