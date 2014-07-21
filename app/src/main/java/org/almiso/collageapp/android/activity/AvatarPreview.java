package org.almiso.collageapp.android.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageActivity;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.preview.BaseView;
import org.almiso.collageapp.android.preview.InstaPreviewView;

/**
 * Created by almiso on 09.07.2014.
 */
public class AvatarPreview extends CollageActivity implements View.OnClickListener {

    private InstaPreviewView image;
    private ShareActionProvider mShareActionProvider;
    private InstaUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_preview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (InstaUser) extras.getSerializable("EXTRA_USER");
        }


        image = (InstaPreviewView) findViewById(R.id.image);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
        layoutParams.height = width;
        layoutParams.width = width;
        image.setLayoutParams(layoutParams);
        image.setEmptyDrawable(R.drawable.ic_action_picture);
        image.setBgColor(getResources().getColor(R.color.st_transparent));
        image.setShape(BaseView.SHAPE.SHAPE_RECTANGLE);
        image.setOnClickListener(this);

        if (user != null) {
            image.requestUserAvatar(user);
        } else {
            Toast.makeText(application, R.string.st_error_load_avatar, Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.root).setOnClickListener(this);
        // Set up activity to go full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final ActionBar actionBar = getActionBar();

        // Hide title text and set home as up
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Hide and show the ActionBar as the visibility changes
        image.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int vis) {
                if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                    actionBar.hide();
                } else {
                    actionBar.show();
                }
            }
        });

        // Start low profile mode and hide ActionBar
        image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        actionBar.hide();
    }

    @Override
    public void onClick(View view) {
        final int vis = image.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            image.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_avatar_preview, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getDefaultIntent());
        return true;
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.ic_save:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
