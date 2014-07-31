package org.almiso.collageapp.android.base;

import android.os.Bundle;

import org.almiso.collageapp.android.media.util.Constants;
import org.almiso.collageapp.android.media.util.ImageCache;
import org.almiso.collageapp.android.media.util.ImageFetcher;

/**
 * Created by Alexandr Sosorev on 25.07.2014.
 */
public class CollageImageFragment extends CollageFragment {

    protected ImageFetcher mImageFetcher;

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(application, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(application, 100);
        mImageFetcher.addImageCache(activity.getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
    }


}
