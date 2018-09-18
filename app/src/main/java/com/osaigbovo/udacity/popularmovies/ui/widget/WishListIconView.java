package com.osaigbovo.udacity.popularmovies.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

public class WishListIconView extends LottieAnimationView {

    public WishListIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WishListIconView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final void toggleWishlisted() {
        this.setActivated(!this.isActivated());
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        this.setSpeed(activated ? 1.0F : -2.0F);
        this.setProgress(0.0F);
        this.playAnimation();
    }
}
