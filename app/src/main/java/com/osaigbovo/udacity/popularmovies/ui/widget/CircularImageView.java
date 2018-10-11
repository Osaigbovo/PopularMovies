package com.osaigbovo.udacity.popularmovies.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.osaigbovo.udacity.popularmovies.util.ViewsUtils;


/**
 * https://github.com/nickbutcher/plaid
 * 
 * An extension to image view that has a circular outline.
 */
public class CircularImageView extends ForegroundImageView {

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOutlineProvider(ViewsUtils.CIRCULAR_OUTLINE);
        setClipToOutline(true);
    }
}
