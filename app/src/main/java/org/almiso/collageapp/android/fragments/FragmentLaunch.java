package org.almiso.collageapp.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;

/**
 * Created by almiso on 13.06.2014.
 */
public class FragmentLaunch extends CollageFragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launch, null);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        view.findViewById(R.id.buttonAuthorize).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAuthorize:
                getRootController().openFragmentAuthorize();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(false);
        getSherlockActivity().getSupportActionBar().setTitle(R.string.st_app_name);
        getSherlockActivity().getSupportActionBar().setSubtitle(null);
    }
}