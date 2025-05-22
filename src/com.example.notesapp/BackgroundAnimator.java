package com.example.notesapp;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;

public class BackgroundAnimator {

    private ValueAnimator colorAnimator;

    public void startBackgroundAnimation(View targetView) {
        int colorStart = Color.parseColor("#6a11cb"); // Purple-blue start
        int colorEnd = Color.parseColor("#2575fc");   // Blue end

        colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorStart, colorEnd);
        colorAnimator.setDuration(5000); // 5 seconds
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);

        colorAnimator.addUpdateListener(animator -> {
            targetView.setBackgroundColor((int) animator.getAnimatedValue());
        });

        colorAnimator.start();
    }

    public void stopBackgroundAnimation() {
        if (colorAnimator != null && colorAnimator.isRunning()) {
            colorAnimator.cancel();
        }
    }
}
