package org.almiso.collageapp.android.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageActivity;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.core.InstaSearchSource;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.fragments.FragmentPhotoPreview;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.media.util.ImageCache;
import org.almiso.collageapp.android.media.util.ImageFetcher;
import org.almiso.collageapp.android.media.util.VersionUtils;
import org.almiso.collageapp.android.ui.source.ViewSourceListener;

import java.util.ArrayList;

//import android.widget.ShareActionProvider;

/**
 * Created by Alexandr Sosorev on 24.07.2014.
 */
public class ActivityPhotoPreview extends CollageActivity implements View.OnClickListener, ViewSourceListener {

    private static final String TAG = "ActivityPhotoPreview";

    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String EXTRA_USER = "extra_user";
    public static final String EXTRA_ACTION = "extra_action";

    private ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private ViewPager mPager;

    private InstaUser user;
    private int ACTION;
    private int currentId;
    private ArrayList<InstaSearchResult> searchResults = new ArrayList<InstaSearchResult>();
    private InstaSearchSource instaSearchSource;

    private Uri uri;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        instaSearchSource.setListener(this);
        onSourceStateChanged();
        onSourceDataChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
        instaSearchSource.setListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_USER, user);
        outState.putInt(EXTRA_ACTION, ACTION);
        outState.putInt(EXTRA_IMAGE, currentId);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (InstaUser) extras.getSerializable(EXTRA_USER);
            ACTION = extras.getInt(EXTRA_ACTION);
            currentId = extras.getInt(EXTRA_IMAGE);
            setContentView(R.layout.activity_photo_preview);
            setUpView();
        } else {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                    return;
                }
            });
        }
    }

    private void setUpView() {
        instaSearchSource = application.getDataSourceKernel().getInstaSearchSource(user.getId() + ACTION);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        mAdapter = new ImagePagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Logger.d(TAG, "onPageSelected = " + position);
                uri = application.getDataSourceKernel().getTempPhoto(position);
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(currentId, false);
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (VersionUtils.hasHoneycomb()) {
            final ActionBar actionBar = getActionBar();

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

            mPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int vis) {
                    if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                        actionBar.hide();
                    } else {
                        actionBar.show();
                    }
                }
            });
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            actionBar.hide();
        }


    }


    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (instaSearchSource.getViewSource() != null) {
                return searchResults.size();
            } else {
                return 0;
            }

        }


        @Override
        public CollageFragment getItem(int position) {
            if (instaSearchSource.getViewSource() != null) {
                instaSearchSource.getViewSource().onItemsShown(position);
                InstaSearchResult searchResult = searchResults.get(position);
                return FragmentPhotoPreview.newInstance(searchResult.getStandardResolutionUrl(), position);
            } else {
                return FragmentPhotoPreview.newInstance("", 0);
            }

        }
    }

    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    @Override
    public void onSourceStateChanged() {
    }

    @Override
    public void onSourceDataChanged() {
        if (instaSearchSource.getViewSource() != null) {
            searchResults = instaSearchSource.getViewSource().getCurrentWorkingSet();
        } else {
            searchResults.clear();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_preview, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/jpeg");
            mShareActionProvider.setShareIntent(shareIntent);
        }
        return true;

    }
}
