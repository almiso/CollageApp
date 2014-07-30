package org.almiso.collageapp.android.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageActivity;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.media.util.Constants;
import org.almiso.collageapp.android.media.util.ImageCache;
import org.almiso.collageapp.android.media.util.ImageFetcher;
import org.almiso.collageapp.android.media.util.ImageReceiver;
import org.almiso.collageapp.android.media.util.RecyclingImageView;

import java.io.File;

/**
 * Created by almiso on 09.07.2014.
 */
public class ActivityAvatarPreview extends CollageActivity implements View.OnClickListener {

    private InstaUser user;
    private ImageFetcher mImageFetcher;
    private RecyclingImageView imageView;

    private ShareActionProvider mShareActionProvider;
    private Bitmap bitmapToSave = null;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_photo_preview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (InstaUser) extras.getSerializable("EXTRA_USER");
        }

        imageView = (RecyclingImageView) findViewById(R.id.imageView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);


        imageView.setOnClickListener(this);


        if (user != null) {
            mImageFetcher.loadImage(user.getProfile_picture_url(), imageView, new ImageReceiver() {
                @Override
                public void onImageReceived(Bitmap bitmap) {
                    bitmapToSave = bitmap;
                }
            });
        } else {
            Toast.makeText(application, R.string.st_error_load_avatar, Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.root).setOnClickListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int vis) {
                if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                    actionBar.hide();
                } else {
                    actionBar.show();
                }
            }
        });

        imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        actionBar.hide();
    }

    @Override
    public void onClick(View view) {
        final int vis = imageView.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            imageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_preview, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            Uri uri = Uri.fromFile(new File(""));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/jpeg");
            mShareActionProvider.setShareIntent(shareIntent);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.ic_save:
                if (bitmapToSave != null) {
                    application.getDataSourceKernel().saveToGallery(bitmapToSave);
                } else {
                    Toast.makeText(application, R.string.st_error_photo_loading, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
