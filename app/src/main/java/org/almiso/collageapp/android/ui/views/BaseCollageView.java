package org.almiso.collageapp.android.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.dialogs.util.BackgroundChooserListener;
import org.almiso.collageapp.android.dialogs.util.SizeChooserListener;
import org.almiso.collageapp.android.media.util.ImageFetcher;
import org.almiso.collageapp.android.media.util.ImageReceiver;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.ui.collage.CollageBaseDrawer;

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

    // Background
    private Rect rect = new Rect();
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

    // Help fields
    private boolean isLoaded = false;

    //Interfaces
    BackgroundChooserListener backgroundChooserListener;
    SizeChooserListener sizeChooserListener;

    //Drawer of photos
    private CollageBaseDrawer drawer;


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

    private void checkResources(Context context) {
        if (!isLoaded) {
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
            isLoaded = true;
        }
    }

    private void init(Context context) {
        checkResources(context);
        colorBgId = Color.TRANSPARENT;

        drawer = new CollageBaseDrawer();
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


    public void setPhotos(ArrayList<InstaSearchResult> photos) {
        this.photos = photos;
        images = new Bitmap[photos.size()];
        loadImages();
        invalidate();
    }

    public ArrayList<InstaSearchResult> getPhotos() {
        return photos;
    }

    public void setImageFetcher(ImageFetcher mImageFetcher) {
        this.mImageFetcher = mImageFetcher;
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

        switch (photos.size()) {
            case 1:
                draw1v1(canvas);
                break;
            case 2:
                if (frameId == FRAME_DEFAULT) {
                    draw2v1(canvas);
                } else if (frameId == 1) {
                    draw2v2(canvas);
                } else if (frameId == 2) {
                    draw2v3(canvas);
                } else if (frameId == 3) {
                    draw2v4(canvas);
                }
                break;
            case 3:
                draw3v1(canvas);
                break;
            case 4:
                draw4v1(canvas);
                break;
            case 5:
                draw5v1(canvas);
                break;
            case 6:
                draw6v1(canvas);
                break;
            case 7:
                if (frameId == FRAME_DEFAULT) {
                    draw7v1(canvas);
                } else if (frameId == 1) {
                    draw7v2(canvas);
                }
                break;
//
            default:
                draw4v1(canvas);
                break;

        }
    }

    private void updateIndentContentArea() {
//        indentContentArea = (getPx(8) + strokeWidth) * indentContentAreaFactor;

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

    //Public methods
    public BackgroundChooserListener getBackgroundChooserListener() {
        return backgroundChooserListener;
    }

    public SizeChooserListener getSizeChooserListener() {
        return sizeChooserListener;
    }


    /*
     * Getters and Setters for interfaces
     */

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

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /*
     * Drawing different variants
     */
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    // DRAW PHOTO -------------     1
    // ---------------------------------------------------------------------------------------------
    private void draw1v1(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto, contentArea.top + contentArea.height()
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }
    }

    //----------------------------------------------------------------------------------------------
    // DRAW PHOTO -------------     2
    // ---------------------------------------------------------------------------------------------
    private void draw2v1(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }

        rectF1.set(contentArea.left + contentArea.width() / 2 + indentPhoto, contentArea.top + contentArea.height() / 2
                        + indentPhoto, contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }
    }

    private void draw2v2(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(contentArea.left + contentArea.width() / 2 + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height()
                        - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }
    }

    private void draw2v3(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height()
                        - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }
    }

    private void draw2v4(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(contentArea.left + contentArea.width() / 2 + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }

        rectF1.set(contentArea.left + contentArea.width() / 2 + indentPhoto, contentArea.top + contentArea.height() / 2
                        + indentPhoto, contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }
    }

    //----------------------------------------------------------------------------------------------
    // DRAW PHOTO -------------     3
    // ---------------------------------------------------------------------------------------------
    private void draw3v1(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }


        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);

        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

    }

    //----------------------------------------------------------------------------------------------
    // DRAW PHOTO -------------     4
    // ---------------------------------------------------------------------------------------------
    private void draw4v1(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }


        rectF1.set(contentArea.left + contentArea.width() / 2 + indentPhoto, contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(contentArea.left + indentPhoto, contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height()
                        - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

        rectF1.set(contentArea.left + contentArea.width() / 2 + indentPhoto, contentArea.top + contentArea.height() / 2
                        + indentPhoto, contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[3] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[3], null, rectF1, photoPaint);
        }
    }

    //----------------------------------------------------------------------------------------------
    // DRAW PHOTO -------------     5
    // ---------------------------------------------------------------------------------------------
    private void draw5v1(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }


        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[3] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[3], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() - contentArea.height() / 4 - indentPhoto,
                contentArea.top + contentArea.height() - contentArea.height() / 4 - indentPhoto);
        if (images[4] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[4], null, rectF1, photoPaint);
        }
    }

    //----------------------------------------------------------------------------------------------
    // DRAW PHOTO -------------     6
    // ---------------------------------------------------------------------------------------------
    private void draw6v1(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }


        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[3] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[3], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() / 4 - indentPhoto);
        if (images[4] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[4], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[5] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[5], null, rectF1, photoPaint);
        }
    }

    //----------------------------------------------------------------------------------------------
    // DRAW PHOTO -------------     7
    // ---------------------------------------------------------------------------------------------
    private void draw7v1(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto, contentArea.top + contentArea.height() / 2
                        - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }


        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);

        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 - indentPhoto);
        if (images[3] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[3], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[4] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[4], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 - indentPhoto);
        if (images[5] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[5], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[6] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[6], null, rectF1, photoPaint);
        }

    }

    private void draw7v2(Canvas canvas) {
        Paint photoPaint = new Paint();
        RectF rectF1 = new RectF();
        Bitmap emptyBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture_dark)).getBitmap();

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[0] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[0], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() / 2 - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[1] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[1], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 2 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() - indentPhoto);
        if (images[2] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[2], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() / 4 - indentPhoto);
        if (images[3] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[3], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 + indentPhoto,
                contentArea.top + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 4 - indentPhoto);
        if (images[4] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[4], null, rectF1, photoPaint);
        }

        rectF1.set(
                contentArea.left + contentArea.width() / 2 + indentPhoto,
                contentArea.top + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[5] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[5], null, rectF1, photoPaint);
        }
        rectF1.set(
                contentArea.left + contentArea.width() / 2 + contentArea.width() / 4 + indentPhoto,
                contentArea.top + contentArea.height() / 4 + indentPhoto,
                contentArea.left + contentArea.width() - indentPhoto,
                contentArea.top + contentArea.height() / 2 - indentPhoto);
        if (images[6] == null) {
            canvas.drawBitmap(emptyBitmap, null, rectF1, photoPaint);
        } else {
            canvas.drawBitmap(images[6], null, rectF1, photoPaint);
        }

    }

}
