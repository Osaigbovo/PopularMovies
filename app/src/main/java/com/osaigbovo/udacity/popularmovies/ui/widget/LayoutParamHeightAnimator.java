package com.osaigbovo.udacity.popularmovies.ui.widget;

import android.animation.ValueAnimator;
import android.view.View;

/*
 * https://messedcode.com/dave/android-java-swipe-collapse-animation
 * */
public class LayoutParamHeightAnimator extends ValueAnimator {

    public LayoutParamHeightAnimator(final View target, int... values) {
        setIntValues(values);

        addUpdateListener(valueAnimator -> {
            int value = (int) valueAnimator.getAnimatedValue();
            target.getLayoutParams().height = value;
            target.requestLayout();
        });
    }

    public static LayoutParamHeightAnimator collapse(View target) {
        return new LayoutParamHeightAnimator(target, target.getHeight(), 0);
    }

}