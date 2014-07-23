package org.almiso.collageapp.android.loader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.LruCache;

import org.almiso.collageapp.android.BuildConfig;

/**
 * Created by Alexandr Sosorev on 22.07.2014.
 */
public class ImageCache {
    private static final String TAG = "ImageCache";
    private LruCache<String, Bitmap> mMemoryCache;

    private ImageCache(float memCacheSizePercent) {
        init(memCacheSizePercent);
    }

    public static ImageCache getInstance(FragmentManager fragmentManager, float memCacheSizePercent) {

        // Search for, or create an instance of the non-UI RetainFragment
        final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);

        // See if we already have an ImageCache stored in RetainFragment
        ImageCache imageCache = (ImageCache) mRetainFragment.getObject();

        // No existing ImageCache, create one and store it in RetainFragment
        if (imageCache == null) {
            imageCache = new ImageCache(memCacheSizePercent);
            mRetainFragment.setObject(imageCache);
        }

        return imageCache;
    }

    public static int calculateMemCacheSize(float percent) {
        if (percent < 0.05f || percent > 0.8f) {
            throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                    + "between 0.05 and 0.8 (inclusive)");
        }
        return Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
    }

    private void init(float memCacheSizePercent) {
        int memCacheSize = calculateMemCacheSize(memCacheSizePercent);

        // Set up memory cache
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Memory cache created (size = " + memCacheSize + ")");
        }
        mMemoryCache = new LruCache<String, Bitmap>(memCacheSize) {

            /**
             * Measure item size in kilobytes rather than units which is more
             * practical for a bitmap cache
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                final int bitmapSize = getBitmapSize(bitmap) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
    }

    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getByteCount();
    }

    public void addBitmapToCache(String data, Bitmap bitmap) {
        if (data == null || bitmap == null) {
            return;
        }

        // Add to memory cache
        if (mMemoryCache != null && mMemoryCache.get(data) == null) {
            mMemoryCache.put(data, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String data) {
        if (mMemoryCache != null) {
            final Bitmap memBitmap = mMemoryCache.get(data);
            if (memBitmap != null) {
//                Logger.d(TAG, "Memory cache hit");
                return memBitmap;
            }
        }
        return null;
    }


    public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        // Check to see if we have retained the worker fragment.
        RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

        // If not retained (or first time running), we need to create and add
        // it.
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            fm.beginTransaction().add(mRetainFragment, TAG).commitAllowingStateLoss();
        }

        return mRetainFragment;
    }

    public static class RetainFragment extends Fragment {
        private Object mObject;

        public RetainFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Make sure this Fragment is retained over a configuration change
            setRetainInstance(true);
        }

        public void setObject(Object object) {
            mObject = object;
        }

        public Object getObject() {
            return mObject;
        }
    }
}
