package org.almiso.collageapp.android.preview.cache;

import android.graphics.Bitmap;

/**
 * Created by almiso on 10.06.2014.
 */
public class BitmapHolder {
    private Bitmap bitmap;
    private String key;
    private int realW;
    private int realH;

    public BitmapHolder(Bitmap bitmap, String key, int realW, int realH) {
        this.bitmap = bitmap;
        this.key = key;
        this.realW = realW;
        this.realH = realH;
    }

    public BitmapHolder(Bitmap bitmap, String key) {
        this.bitmap = bitmap;
        this.key = key;
        this.realW = bitmap.getWidth();
        this.realH = bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getKey() {
        return key;
    }

    public int getRealW() {
        return realW;
    }

    public int getRealH() {
        return realH;
    }
}

