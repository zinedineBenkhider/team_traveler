package com.example.teamtraveler.presentation.trip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class NonSwipableViewPager extends ViewPager {

    private boolean enabled;
    public NonSwipableViewPager(Context context) {
        super(context);
        this.enabled = false;
    }
    public NonSwipableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }
}
