package org.almiso.collageapp.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.preview.InstaPreviewView;

/**
 * Created by almiso on 13.06.2014.
 */
public class FragmentMain extends CollageFragment implements View.OnClickListener {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        view.findViewById(R.id.buttonMyBestPhotos).setOnClickListener(this);
        view.findViewById(R.id.buttonMyPhotos).setOnClickListener(this);
        view.findViewById(R.id.buttonSearchUsersPhotos).setOnClickListener(this);
        view.findViewById(R.id.buttonSearchFeed).setOnClickListener(this);
        view.findViewById(R.id.buttonSearchNear).setOnClickListener(this);
        goneView(view.findViewById(R.id.buttonSearchNear));
        view.findViewById(R.id.avatarTouchLayer).setOnClickListener(this);

        InstaPreviewView previewView = (InstaPreviewView) view.findViewById(R.id.avatar);
        previewView.setEmptyDrawable(R.drawable.ic_action_person);
        previewView.requestAvatar();

        ((TextView) view.findViewById(R.id.name)).setText(application.getAccount().getUsername().toUpperCase());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMyPhotos:
                getRootController().openFragmentSearch(FragmentPhotoGrid.ACTION_SEARCH_MY_PHOTOS,
                        application.getAccount().getMe());
                break;
            case R.id.buttonMyBestPhotos:
                getRootController().openFragmentSearch(FragmentPhotoGrid.ACTION_SEARCH_MY_BEST_PHOTOS,
                        application.getAccount().getMe());
                break;
            case R.id.buttonSearchUsersPhotos:
                getRootController().openFragmentSearchUserByNick();
                break;
            case R.id.buttonSearchFeed:
                getRootController().openFragmentSearch(FragmentPhotoGrid.ACTION_SEARCH_FEED,
                        application.getAccount().getMe());
                break;
            case R.id.buttonSearchNear:
                getRootController().openFragmentSearch(FragmentPhotoGrid.ACTION_SEARCH_NEAR,
                        application.getAccount().getMe());
                break;
//

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_main, menu);

        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(false);
        getSherlockActivity().getSupportActionBar().setTitle(R.string.st_app_name);
        getSherlockActivity().getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_settings:
                getRootController().openFragmentSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
