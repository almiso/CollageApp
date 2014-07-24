package org.almiso.collageapp.android.loader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import org.almiso.collageapp.android.base.CollageApplication;

/**
 * Created by Alexandr Sosorev on 22.07.2014.
 */
public class ImageFetcherOld {

//    protected static final String TAG = "ImageFetcherm";

    private Resources mResources;
    private Bitmap mLoadingBitmap;

    private final Object mPauseWorkLock = new Object();
    protected boolean mPauseWork = false;

    private ImageLoader loader;

    public ImageFetcherOld(Context context, int imageSize) {
//        super(context, imageSize);
        init(context);
    }

    private void init(Context context) {
        mResources = context.getResources();
        loader = new ImageLoader((CollageApplication) context.getApplicationContext());
    }

    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
    }

    public void addImageCache(FragmentManager fragmentManager, float memCacheSizePercent) {
        loader.addImageCache(fragmentManager, memCacheSizePercent);
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        loader.setExitTasksEarly(exitTasksEarly);
        setPauseWork(false);
    }

    public void loadImage(final Object data, final ImageView imageView) {

        imageView.setImageBitmap(mLoadingBitmap);

        if (data == null) {
            return;
        }

        loader.request(data, new ImageReceiver() {
            @Override
            public void onImageReceived(Bitmap bitmap, boolean intermediate) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    public void setPauseWork(boolean pauseWork) {
        loader.setPauseWork(pauseWork);
    }

}
