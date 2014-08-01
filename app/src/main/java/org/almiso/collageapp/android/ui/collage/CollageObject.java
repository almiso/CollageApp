package org.almiso.collageapp.android.ui.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Alexandr Sosorev on 01.08.2014.
 */
public class CollageObject {

    private Canvas canvas;
    private Context context;
    private RectF contentArea;
    private float indentPhoto;
    private Bitmap[] images;
    private int frameId;

    public CollageObject(Canvas canvas, Context context, RectF contentArea, float indentPhoto, Bitmap[] images, int frameId) {
        this.canvas = canvas;
        this.context = context;
        this.contentArea = contentArea;
        this.indentPhoto = indentPhoto;
        this.images = images;
        this.frameId = frameId;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Context getContext() {
        return context;
    }

    public RectF getContentArea() {
        return contentArea;
    }

    public float getIndentPhoto() {
        return indentPhoto;
    }

    public Bitmap[] getImages() {
        return images;
    }

    public int getFrameId() {
        return frameId;
    }

}
