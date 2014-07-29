package org.almiso.collageapp.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.activity.ActivityAvatarPreview;
import org.almiso.collageapp.android.base.CollageImageFragment;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.core.model.InstaUserDependence;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.preview.user.dependence.UserDependenceReceiver;

/**
 * Created by almiso on 21.06.2014.
 */
public class FragmentUserProfile extends CollageImageFragment implements View.OnClickListener, UserDependenceReceiver {


    protected static String TAG = "FragmentUserProfile";

    private ProgressBar progress;
    private LinearLayout layoutData;
    private LinearLayout layoutError;


    private InstaUser user;
    private InstaUserDependence userDependence;


    //Controls
    private TextView mediaCount;
    private TextView followsCount;
    private TextView followedByCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            user = (InstaUser) savedInstanceState.getSerializable("user");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userDependence == null) {
            loadData();
        }
        updateDataLayout();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            user = (InstaUser) savedInstanceState.getSerializable("user");
        }
        if (getArguments() != null) {
            user = (InstaUser) getArguments().getSerializable("user");
        } else {
            user = null;
        }
        View view = inflater.inflate(R.layout.fragment_user_profile, null);
        if (user == null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    activity.onBackPressed();
                }
            });
        } else {
            setUpView(view);
        }
        return view;
    }

    private void setUpView(View view) {
        mediaCount = (TextView) view.findViewById(R.id.mediaCount);
        followsCount = (TextView) view.findViewById(R.id.followsCount);
        followedByCount = (TextView) view.findViewById(R.id.followedByCount);

        view.findViewById(R.id.buttonMedia).setOnClickListener(this);
        view.findViewById(R.id.buttonFollows).setOnClickListener(this);
        view.findViewById(R.id.buttonFollowedBy).setOnClickListener(this);

        layoutData = (LinearLayout) view.findViewById(R.id.layoutData);
        layoutError = (LinearLayout) view.findViewById(R.id.layoutError);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        goneView(layoutError);
        goneView(progress);


        view.findViewById(R.id.avatarTouchLayer).setOnClickListener(this);

        if (user != null) {
            ((TextView) view.findViewById(R.id.name)).setText(user.getDisplayName().toUpperCase());

            ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
            mImageFetcher.setImageSize(100);
            mImageFetcher.setShape(ImageShape.SHAPE_CIRCLE);
            mImageFetcher.setImageFadeIn(false);
            mImageFetcher.loadImage(user.getProfile_picture_url(), avatar);
        }
    }

    private void updateDataLayout() {
        if (userDependence == null) {
            goneView(layoutData);
            goneView(layoutError);
            showView(progress);
            return;
        }
        if (userDependence.isPrivate()) {
            goneView(layoutData);
            goneView(progress);
            showView(layoutError);
        } else {
            goneView(layoutError);
            goneView(progress);
            showView(layoutData);

            mediaCount.setText(String.valueOf(userDependence.getMediaCount()));
            followsCount.setText(String.valueOf(userDependence.getFollowsCount()));
            followedByCount.setText(String.valueOf(userDependence.getFollowedByCount()));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMedia:
                getRootController().openFragmentImageGrid(FragmentImageGrid.ACTION_SEARCH_USER_PHOTOS, user, false);
                break;
            case R.id.buttonFollows:
                getRootController().openFragmentFriendList(FragmentUserList.ACTION_FOLLOWS, user);
                break;
            case R.id.buttonFollowedBy:
                getRootController().openFragmentFriendList(FragmentUserList.ACTION_FOLLOWED_BY, user);
                break;
            case R.id.avatarTouchLayer:
                Intent intent = new Intent(application, ActivityAvatarPreview.class);
                intent.putExtra("EXTRA_USER", user);
                startActivity(intent);
                break;
        }

    }

    private void loadData() {
        InstaUserDependence dep = new InstaUserDependence(user.getId(), 0, 0, 0, true);
        application.getUiKernel().getInstaUserDependenceLoader().requestSearchUser(dep, this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.st_user_profile);
        activity.getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUserDependenceReceived(InstaUserDependence dependence) {
        userDependence = dependence;
        updateDataLayout();
    }
}
