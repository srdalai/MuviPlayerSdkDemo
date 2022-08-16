package com.muvi.tvplayer.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ui.DefaultTimeBar;

public class CustomTimeBar extends DefaultTimeBar {

    public CustomTimeBar(Context context) {
        super(context);
    }

    public CustomTimeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTimeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomTimeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, @Nullable AttributeSet timebarAttrs) {
        super(context, attrs, defStyleAttr, timebarAttrs);
    }

    public CustomTimeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, @Nullable AttributeSet timebarAttrs, int defStyleRes) {
        super(context, attrs, defStyleAttr, timebarAttrs, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
