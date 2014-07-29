package org.almiso.collageapp.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageImageFragment;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.dialogs.DialogFactory;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.ui.views.BaseCollageView;

import java.util.ArrayList;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public class FragmentCollage extends CollageImageFragment implements View.OnClickListener {

    private ArrayList<InstaSearchResult> mSelectedPhotos;
    private BaseCollageView collageView;

    @Override
    public void onResume() {
        super.onResume();
        collageView.invalidate();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSelectedPhotos = (ArrayList<InstaSearchResult>) savedInstanceState.getSerializable("mSelectedPhotos");
        }
        if (getArguments() != null) {
            mSelectedPhotos = (ArrayList<InstaSearchResult>) getArguments().getSerializable("mSelectedPhotos");
        }
        View view = inflater.inflate(R.layout.fragment_collage, container, false);
        if (mSelectedPhotos == null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    Logger.d(TAG, "Sucks :( ");
                    activity.onBackPressed();
                }
            });
        } else {
            setUpView(view);
        }
        return view;
    }

    private void setUpView(View view) {
        Logger.d(TAG, "setUpView");
        collageView = (BaseCollageView) view.findViewById(R.id.collageView);

        mImageFetcher.setImageSize(100);
        mImageFetcher.setShape(ImageShape.SHAPE_RECTANGLE);
        mImageFetcher.setImageFadeIn(false);

        collageView.setImageFetcher(mImageFetcher);
        collageView.setPhotos(mSelectedPhotos);

        view.findViewById(R.id.buttonBackground).setOnClickListener(this);
        view.findViewById(R.id.buttonSize).setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_collage, menu);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.st_collage);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBackground:
                requestBackgroundChooser();
                break;
            case R.id.buttonSize:
                requestSizeChooser();
                break;
        }

    }

    private void requestBackgroundChooser() {
        final Context context = getActivity();
        DialogFactory factory = new DialogFactory(context);
        factory.requestBackgroundChooser(collageView);
    }


    private void requestSizeChooser() {
        final Context context = getActivity();
        DialogFactory factory = new DialogFactory(context);
        factory.requestSizeChooser(collageView);
    }
}