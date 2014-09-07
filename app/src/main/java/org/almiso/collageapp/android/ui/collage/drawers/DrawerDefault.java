package org.almiso.collageapp.android.ui.collage.drawers;

import android.graphics.Color;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.ui.collage.BaseDrawer;
import org.almiso.collageapp.android.ui.collage.CollageObject;

/**
 * Created by Alexandr Sosorev on 01.08.2014.
 */
public class DrawerDefault extends BaseDrawer {

    public static final int FRAMES_COUNT = 1;

    public DrawerDefault(CollageObject object) {
        super(object);
    }

    public void draw() {
        String text = context.getResources().getString(R.string.st_not_supported_layout_count);
        TextPaint mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(50);
        StaticLayout mTextLayout = new StaticLayout(text, mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        canvas.translate(canvas.getWidth() / 10, canvas.getWidth() / 4);
        mTextLayout.draw(canvas);
        canvas.restore();
    }
}