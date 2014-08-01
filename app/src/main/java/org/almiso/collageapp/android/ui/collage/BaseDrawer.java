package org.almiso.collageapp.android.ui.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import org.almiso.collageapp.android.R;

/**
 * Created by Alexandr Sosorev on 01.08.2014.
 */
public class BaseDrawer {

    protected Canvas canvas;
    protected Context context;
    protected RectF contentArea;
    protected float indentPhoto;
    protected Bitmap[] images;
    protected int frameId;
    protected Bitmap emptyBitmap;
    protected Paint photoPaint;
    protected RectF rectF1;

    public BaseDrawer(CollageObject object) {
        this.canvas = object.getCanvas();
        this.context = object.getContext();
        this.contentArea = object.getContentArea();
        this.indentPhoto = object.getIndentPhoto();
        this.images = object.getImages();
        this.frameId = object.getFrameId();

        emptyBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();
        photoPaint = new Paint();
        rectF1 = new RectF();
    }

    protected Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, null, targetRect, null);

        return bitmap;
    }

}
