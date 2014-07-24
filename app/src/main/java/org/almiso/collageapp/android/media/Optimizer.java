package org.almiso.collageapp.android.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by almiso on 10.06.2014.
 */

public class Optimizer {

    //
    private static final String TAG = "Optimizer";
    //
    private static final int MAX_PIXELS = 1200 * 1200;
//
    private static ThreadLocal<byte[]> bitmapTmp = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[16 * 1024];
        }
    };

    //
//    // Public methods
//
    public static void clearBitmap(Bitmap bitmap) {
        bitmap.eraseColor(Color.TRANSPARENT);
        new Canvas(bitmap).drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    public static Bitmap optimize(byte[] data) throws IOException {
        return optimize(new ByteSource(data));
    }

    public static Bitmap load(String fileName) throws IOException {
        return load(new FileSource(fileName));
    }

    public static BitmapInfo loadTo(String fileName, Bitmap dest) throws IOException {
        return loadTo(new FileSource(fileName), dest);
    }

    public static void scaleToFill(Bitmap src, int sourceW, int sourceH, Bitmap dest) {

        float ratio = Math.max(dest.getWidth() / (float) sourceW, dest.getHeight() / (float) sourceH);

        clearBitmap(dest);
        Canvas canvas = new Canvas(dest);
        int paddingTop = -(dest.getHeight() - (int) (sourceH * ratio)) / 2;
        int paddingLeft = -(dest.getWidth() - (int) (sourceW * ratio)) / 2;
        canvas.drawBitmap(src, new Rect(1, 1, sourceW - 1, sourceH - 1), new Rect(-paddingLeft, -paddingTop,
                (int) (sourceW * ratio) - paddingLeft, (int) (sourceH * ratio) - paddingTop), new Paint(
                Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
    }

    @SuppressLint("NewApi")
    private static Bitmap load(Source source) throws IOException {
        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inSampleSize = 1;
        o.inScaled = false;
        o.inTempStorage = bitmapTmp.get();

        if (Build.VERSION.SDK_INT >= 10) {
            o.inPreferQualityOverSpeed = true;
        }

        if (Build.VERSION.SDK_INT >= 11) {
            o.inMutable = true;
        }

        InputStream stream = createStream(source);
        try {

            return BitmapFactory.decodeStream(stream, null, o);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    //
    private static int getScale(Source source) throws IOException {
        InputStream stream = createStream(source);
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, o);

            int scale = 1;
            int scaledW = o.outWidth;
            int scaledH = o.outHeight;
            while (scaledW * scaledH > MAX_PIXELS) {
                scale *= 2;
                scaledH /= 2;
                scaledW /= 2;
            }

//            Logger.d(TAG, "Image Scale = " + scale + ", width: " + o.outWidth + ", height: " + o.outHeight);

            return scale;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Ignore this
                }
            }
        }
    }

    private static BitmapInfo getInfo(Source source, boolean ignoreOrientation) throws IOException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        o.inTempStorage = bitmapTmp.get();

        InputStream fis = createStream(source);
        try {
            BitmapFactory.decodeStream(fis, null, o);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // Ignore this
                }
            }
        }

        int w = o.outWidth;
        int h = o.outHeight;

        if (!ignoreOrientation) {
            if (source instanceof FileSource || source instanceof UriSource) {
                if (!isVerticalImage(source)) {
                    w = o.outHeight;
                    h = o.outWidth;
                }
            }
        }

        return new BitmapInfo(w, h, o.outMimeType);
    }

    //
    private static Bitmap buildOptimized(Source source, int scale) throws IOException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        if (scale > 1) {
            o.inSampleSize = scale;
        }

        o.inPreferredConfig = Bitmap.Config.ARGB_8888;
        o.inDither = false;
        o.inScaled = false;
        o.inTempStorage = bitmapTmp.get();

        InputStream stream = createStream(source);
        try {
            return BitmapFactory.decodeStream(stream, null, o);
        } finally {
            if (stream != null) {
                try {

                    stream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private static Bitmap optimize(Source source) throws IOException {
//        boolean isAnimated = detectGif(source);

//        if (isAnimated) {
//            return load(source);
//        }

        int scale = getScale(source);
        Bitmap res = buildOptimized(source, scale);
//        if (!(source instanceof ByteSource)) {
//            res = fixRotation(res, source);
//        }
        return res;
    }

    //
    private static BitmapInfo loadTo(Source source, Bitmap dest) throws IOException {
        BitmapInfo res = getInfo(source, true);
        clearBitmap(dest);
        decodeReuse(source, res, dest);
        return res;
    }

    //
    private static void decodeReuse(Source source, BitmapInfo info, Bitmap dest) throws IOException {
    }

    //
//    //TODO Check this function
    private static InputStream createStream(Source source) throws IOException {
        if (source instanceof ByteSource) {
            return new ByteArrayInputStream(((ByteSource) source).getData());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static String getOrientationTag(String fileName) throws IOException {
        ExifInterface exif = new ExifInterface(fileName);
        return exif.getAttribute(ExifInterface.TAG_ORIENTATION);
    }

    //
    private static boolean isVerticalImage(Source source) throws IOException {
        if (source instanceof FileSource) {
            return isVerticalImage(((FileSource) source).getFileName());
        } else if (source instanceof UriSource) {
            return isVerticalImage(((UriSource) source).getUri(), ((UriSource) source).getContext());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static boolean isVerticalImage(String fileName) throws IOException {
        String exifOrientation = getOrientationTag(fileName);
        return (exifOrientation.equals("0") || exifOrientation.equals("1") || exifOrientation.equals("2")
                || exifOrientation.equals("3") || exifOrientation.equals("4"));
    }

    private static boolean isVerticalImage(Uri uri, Context context) {
        int angle = getContentRotation(uri, context);
        return angle == 0 || angle == 180;
    }

    private static int getContentRotation(Uri uri, Context context) {
        try {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static abstract class Source {

    }

    //
    private static class FileSource extends Source {
        private String fileName;

        private FileSource(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    private static class UriSource extends Source {
        private Uri uri;
        private Context context;

        private UriSource(Uri uri, Context context) {
            this.uri = uri;
            this.context = context;
        }

        public Uri getUri() {
            return uri;
        }

        public Context getContext() {
            return context;
        }
    }

    private static class ByteSource extends Source {
        private byte[] data;

        private ByteSource(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }
    }

    //
    public static class BitmapInfo {
        private int width;
        private int height;
        private String mimeType;

        public BitmapInfo(int width, int height, String mimeType) {
            this.width = width;
            this.height = height;
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean isJpeg() {
            return mimeType != null && mimeType.equals("image/jpeg");
        }
    }
}
