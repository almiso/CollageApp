package org.almiso.collageapp.android.preview;

import android.graphics.Bitmap;

import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.preview.cache.BitmapHolder;
import org.almiso.collageapp.android.preview.cache.ImageCache;

/**
 * Created by almiso on 10.06.2014.
 */
public class ImageHolder {
    private static final String TAG = "ImageHolder";
    private BitmapHolder bitmap;
    private ImageCache cache;
    private boolean isReleased = false;

    public ImageHolder(BitmapHolder bitmap, ImageCache cache) {
        this.bitmap = bitmap;
        this.cache = cache;
        cache.incReference(bitmap.getKey(), this);
    }

    public int getW() {
        if (isReleased) {
            throw new UnsupportedOperationException();
        }
        return bitmap.getRealW();
    }

    public int getH() {
        if (isReleased) {
            throw new UnsupportedOperationException();
        }
        return bitmap.getRealH();
    }

    public Bitmap getBitmap() {
        if (isReleased) {
            throw new UnsupportedOperationException();
        }
        return bitmap.getBitmap();
    }

    public void release() {
        if (isReleased) {
            throw new UnsupportedOperationException();
        }
        isReleased = true;
        cache.decReference(bitmap.getKey(), this);
        bitmap = null;
    }
}
