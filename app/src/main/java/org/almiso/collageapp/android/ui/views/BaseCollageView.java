package org.almiso.collageapp.android.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.dialogs.util.BackgroundChooserListener;
import org.almiso.collageapp.android.dialogs.util.SizeChooserListener;
import org.almiso.collageapp.android.media.util.ImageFetcher;
import org.almiso.collageapp.android.media.util.ImageReceiver;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.ui.collage.CollageBaseDrawer;
import org.almiso.collageapp.android.ui.collage.CollageObject;

import java.util.ArrayList;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public class BaseCollageView extends BaseView {

    protected static final String TAG = "BaseCollageView";

    private static final int FRAME_DEFAULT = 0;

    //common
    private ArrayList<InstaSearchResult> photos;
    private Bitmap[] images;
    private ImageFetcher mImageFetcher;
    private Context context;

    //Drawer of photos
    private CollageBaseDrawer drawer;

    // Background
    private RectF rectF = new RectF();
    private static Paint backgroundPaint;
    private int colorBgId;

    // shape angle
    private float strokeWidth = 0f;
    private int strokeColorId;

    // Content
    private int shape;
    private int frameId;
    protected RectF contentArea = new RectF();
    private float indentContentArea;
    private float indentContentAreaFactor;
    private float indentPhoto;

    //Interfaces
    BackgroundChooserListener backgroundChooserListener;
    SizeChooserListener sizeChooserListener;


    public BaseCollageView(Context context) {
        super(context);
        init(context);
    }

    public BaseCollageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseCollageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);

        strokeWidth = 0f;
        strokeColorId = Color.BLACK;

        shape = ImageShape.SHAPE_RECTANGLE;
        indentContentAreaFactor = 1;
        indentPhoto = 0f;

        updateIndentContentArea();

        contentArea = new RectF(indentContentArea, indentContentArea, metrics.widthPixels - indentContentArea,
                metrics.widthPixels - indentContentArea);
        frameId = FRAME_DEFAULT;


        colorBgId = Color.TRANSPARENT;
        this.context = context;

        drawer = new CollageBaseDrawer(context);
        photos = new ArrayList<>();
        backgroundChooserListener = new BackgroundChooserListener() {

            @Override
            public void onShapeSelected(int shape) {
                setShape(shape);
            }

            @Override
            public void onContentColorSelected(int colorId) {
                setBackgroundColor(colorId);
            }

            @Override
            public void onStrokeWidthSelected(int width) {
                setStrokeWidth(width);
            }

            @Override
            public void onStrokeColorSelected(int colorId) {
                setStrokeColor(colorId);
            }

            @Override
            public void onFrameSelected(int frameId) {
                setFrameId(frameId);
            }
        };
        sizeChooserListener = new SizeChooserListener() {
            @Override
            public void onSizeChanged(int progress) {
                setIndentContentArea(progress);
            }

            @Override
            public void onSizePhotoChanged(int progress) {
                setIndentPhoto(progress);
            }
        };
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawPhotos(canvas);
    }

    private void drawBackground(Canvas canvas) {
        // Background pattern
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(colorBgId);
        // draw stroke
        Paint paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(strokeColorId);
        paintStroke.setStrokeWidth(strokeWidth);

        if (shape == ImageShape.SHAPE_RECTANGLE) {
            // draw rect
            rectF.set(0, 0, metrics.widthPixels, metrics.widthPixels);
            canvas.drawRoundRect(rectF, 0f, 0f, backgroundPaint);
            canvas.drawRoundRect(rectF, 0f, 0f, paintStroke);
        } else {
            // draw circle
            canvas.drawCircle(metrics.widthPixels / 2, metrics.widthPixels / 2, metrics.widthPixels / 2,
                    backgroundPaint);
            canvas.drawCircle(metrics.widthPixels / 2, metrics.widthPixels / 2, metrics.widthPixels / 2 - strokeWidth / 2, paintStroke);
        }
    }

    private void drawPhotos(Canvas canvas) {
        if (photos.isEmpty() || photos == null)
            return;
        drawer.draw(new CollageObject(canvas, context, contentArea, indentPhoto, images, frameId));
    }

    private void updateIndentContentArea() {
        float tmp = (strokeWidth == 0) ? 1 : strokeWidth;
        indentContentArea = tmp * indentContentAreaFactor;

        if (shape == ImageShape.SHAPE_RECTANGLE) {
            contentArea.set(indentContentArea, indentContentArea, metrics.widthPixels - indentContentArea,
                    metrics.widthPixels - indentContentArea);
        } else {
            float A = metrics.widthPixels;
            contentArea.set(indentContentArea + 0.15f * A, indentContentArea + 0.15f * A,
                    0.85f * A - indentContentArea, 0.85f * A - indentContentArea);
        }

    }

    private void loadImages() {
        for (int i = 0; i < photos.size(); i++) {
            final int k = i;
            mImageFetcher.loadImage(photos.get(i).getThumbnailUrl(), new ImageReceiver() {
                @Override
                public void onImageReceived(Bitmap bitmap) {
                    images[k] = bitmap;
                    invalidate();
                }
            });
        }
    }

    //Public methods
    public void setPhotos(ArrayList<InstaSearchResult> photos) {
        this.photos = photos;
        images = new Bitmap[photos.size()];
        loadImages();
        invalidate();
    }

    public ArrayList<InstaSearchResult> getPhotos() {
        return photos;
    }

    public BackgroundChooserListener getBackgroundChooserListener() {
        return backgroundChooserListener;
    }

    public void setImageFetcher(ImageFetcher mImageFetcher) {
        this.mImageFetcher = mImageFetcher;
    }

    public SizeChooserListener getSizeChooserListener() {
        return sizeChooserListener;
    }

    public float getIndentContentAreaFactor() {
        return indentContentAreaFactor;
    }

    public void setIndentContentArea(int indentContentArea) {
        this.indentContentAreaFactor = 1 + indentContentArea / 40.01f;
        updateIndentContentArea();
        invalidate();
    }

    public float getIndentPhoto() {
        return indentPhoto;
    }

    public void setIndentPhoto(int size) {
        this.indentPhoto = size;
        invalidate();
    }

    public void setShape(int shape) {
        this.shape = shape;
        updateIndentContentArea();
        invalidate();
    }

    public int getShape() {
        return shape;
    }

    public void setBackgroundColor(int colorId) {
        this.colorBgId = colorId;
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        updateIndentContentArea();
        invalidate();
    }

    public int getStrokeWidth() {
        return (int) strokeWidth;
    }

    public void setStrokeColor(int strokeColorId) {
        this.strokeColorId = strokeColorId;
        invalidate();
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
        invalidate();
    }

    public CharSequence[] getFramesTitles() {
        return drawer.getFramesTitles(photos.size());
    }


}
