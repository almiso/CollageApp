package org.almiso.collageapp.android.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import org.almiso.collageapp.android.base.CollageApplication;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public class BaseView extends View {
    protected final float density;
    protected final float dDensity;
    protected final DisplayMetrics metrics;
    protected final CollageApplication application;

    public BaseView(Context context) {
        super(context);
        application = (CollageApplication) context.getApplicationContext();
        metrics = context.getResources().getDisplayMetrics();
        density = metrics.density;
        dDensity = metrics.scaledDensity;
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        application = (CollageApplication) context.getApplicationContext();
        metrics = context.getResources().getDisplayMetrics();
        density = metrics.density;
        dDensity = metrics.scaledDensity;
    }

    public BaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        application = (CollageApplication) context.getApplicationContext();
        metrics = context.getResources().getDisplayMetrics();
        density = metrics.density;
        dDensity = metrics.scaledDensity;
    }

    protected int getPx(float dp) {
		/*
		 * return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		 * dp, metrics);
		 */
        return (int) (dp * density + 0.5f);
    }

    protected int getSp(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    protected void bounds(Drawable d, int x, int y, float scale) {
        int w = d.getIntrinsicWidth() >> 1;
        int h = d.getIntrinsicHeight() >> 1;
        int centerX = w + x;
        int centerY = h + y;
        bounds(d, (int) (centerX - w * scale), (int) (centerY - h * scale), (int) (2 * w * scale),
                (int) (2 * h * scale));
    }

    protected void bounds(Drawable d, int x, int y) {
        bounds(d, x, y, d.getIntrinsicWidth(), d.getIntrinsicHeight());
    }

    protected void bounds(Drawable d, int x, int y, int w, int h) {
        d.setBounds(x, y, x + w, y + h);
    }

//    protected static int px(float dp) {
//        return (int) (dp * UiMeasure.DENSITY + 0.5f);
//    }
//
//    protected static int sp(float sp) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, UiMeasure.METRICS);
//    }

    @SuppressLint("NewApi")
    protected void inavalidateForAnimation() {
        if (Build.VERSION.SDK_INT >= 16) {
            postInvalidateOnAnimation();
        } else {
            postInvalidateDelayed(16);
        }
    }
}
