package org.almiso.collageapp.android.ui.collage.drawers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import org.almiso.collageapp.android.R;

/**
 * Created by Alexandr Sosorev on 31.07.2014.
 */
public class DrawerOne {

    private static Canvas canvas;
    private static Context context;
    private static RectF contentArea;
    private static float indentPhoto;
    private static Bitmap[] images;

    public static void set(Canvas canvas, Context context, RectF contentArea, float indentPhoto, Bitmap[] images) {
        DrawerOne.canvas = canvas;
        DrawerOne.context = context;
        DrawerOne.contentArea = contentArea;
        DrawerOne.indentPhoto = indentPhoto;
        DrawerOne.images = images;
    }

    public static void draw1v1() {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto, contentArea.top + contentArea.height()
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }
    }
}
