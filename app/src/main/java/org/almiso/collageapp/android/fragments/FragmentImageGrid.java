package org.almiso.collageapp.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;

/**
 * Created by Alexandr Sosorev on 24.07.2014.
 */
public class FragmentImageGrid extends CollageFragment implements AdapterView.OnItemClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_loader, container, false);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        
    }
}
