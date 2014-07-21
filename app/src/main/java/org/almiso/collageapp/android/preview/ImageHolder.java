package org.almiso.collageapp.android.preview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

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


    public Bitmap getRoundedCornerBitmap() {
        if (isReleased) {
            throw new UnsupportedOperationException();
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getBitmap().getWidth(), bitmap.getBitmap()
                .getHeight(), Bitmap.Config.ARGB_8888);
        float pixels = 100f;
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap.getBitmap(), rect, rect, paint);

        return output;
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
