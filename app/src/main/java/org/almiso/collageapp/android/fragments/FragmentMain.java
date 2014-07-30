package org.almiso.collageapp.android.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.activity.ActivityAvatarPreview;
import org.almiso.collageapp.android.base.CollageImageFragment;
import org.almiso.collageapp.android.core.model.InstaUserDependence;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.media.util.VersionUtils;
import org.almiso.collageapp.android.preview.user.dependence.UserDependenceReceiver;

/**
 * Created by almiso on 13.06.2014.
 */
public class FragmentMain extends CollageImageFragment implements View.OnClickListener, UserDependenceReceiver {

    private InstaUserDependence mDependence;

    //Controls
    private TextView mediaCount;
    private TextView followsCount;
    private TextView followedByCount;

    @Override
    public void onResume() {
        super.onResume();
        if (mDependence == null) {
            loadDependence();
        }
        updateDataLayout();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        mediaCount = (TextView) view.findViewById(R.id.mediaCount);
        followsCount = (TextView) view.findViewById(R.id.followsCount);
        followedByCount = (TextView) view.findViewById(R.id.followedByCount);

        view.findViewById(R.id.buttonMyPhotos).setOnClickListener(this);
        view.findViewById(R.id.buttonMyFollows).setOnClickListener(this);
        view.findViewById(R.id.buttonMyFollowedBy).setOnClickListener(this);
        view.findViewById(R.id.buttonSearchFeed).setOnClickListener(this);
        view.findViewById(R.id.avatarTouchLayer).setOnClickListener(this);

        initFields(view);
    }


    private void initFields(View view) {
        ((TextView) view.findViewById(R.id.name)).setText(application.getAccount().getUsername().toUpperCase());

        ImageView avatarImage = (ImageView) view.findViewById(R.id.avatar);
        mImageFetcher.setImageSize(100);
        mImageFetcher.setShape(ImageShape.SHAPE_CIRCLE);
        mImageFetcher.setImageFadeIn(false);
        mImageFetcher.loadImage(application.getAccount().getMe().getProfile_picture_url(), avatarImage);
    }

    private void updateDataLayout() {
        if (mDependence == null) {
            return;
        }
        mediaCount.setText(String.valueOf(mDependence.getMediaCount()));
        followsCount.setText(String.valueOf(mDependence.getFollowsCount()));
        followedByCount.setText(String.valueOf(mDependence.getFollowedByCount()));
    }

    private void loadDependence() {
        InstaUserDependence dep = new InstaUserDependence(application.getMyId(), 0, 0, 0, true);
        application.getUiKernel().getInstaUserDependenceLoader().requestSearchUser(dep, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMyPhotos:
                getRootController().openFragmentImageGrid(FragmentImageGrid.ACTION_SEARCH_MY_PHOTOS,
                        application.getAccount().getMe(), false);
                break;
            case R.id.buttonMyFollows:
                getRootController().openFragmentFriendList(FragmentUserList.ACTION_FOLLOWS,
                        application.getAccount().getMe());
                break;
            case R.id.buttonMyFollowedBy:
                getRootController().openFragmentFriendList(FragmentUserList.ACTION_FOLLOWED_BY,
                        application.getAccount().getMe());
                break;
            case R.id.buttonSearchFeed:
                getRootController().openFragmentImageGrid(FragmentImageGrid.ACTION_SEARCH_FEED,
                        application.getAccount().getMe(), false);
                break;
            case R.id.avatarTouchLayer:
                Intent intent = new Intent(application, ActivityAvatarPreview.class);
                intent.putExtra("EXTRA_USER", application.getAccount().getMe());

                if (VersionUtils.hasJellyBean()) {
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
                    getActivity().startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
        }

    }

    @Override
    public void onUserDependenceReceived(InstaUserDependence dependence) {
        mDependence = dependence;
        updateDataLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_main, menu);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(false);


        activity.getSupportActionBar().setTitle(R.string.st_app_name);
        activity.getSupportActionBar().setSubtitle(R.string.st_main_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_settings:
                getRootController().openFragmentSettings();
                return true;
            case R.id.ic_search_user:
                getRootController().openFragmentSearchUserByNick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}