package org.almiso.collageapp.android.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;

/**
 * Created by almiso on 13.06.2014.
 */
public class FragmentSettings extends CollageFragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        view.findViewById(R.id.buttonExit).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tvVersion)).setText(getVersionString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonExit:
                getRootController().onLogout();
                break;
        }
    }

    private String getVersionString() {
        String result = "";
        String versionName = "";
        int versionCode = 1;

        PackageManager manager = application.getPackageManager();
        PackageInfo info = null;


        try {
            info = manager.getPackageInfo(application.getPackageName(), 0);
            versionName = info.versionName;
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "1.0.0";
            versionCode = 1;
            e.printStackTrace();
        }
//        result = getString(R.string.st_version_name) + " " + versionName + " (" + getString(R.string.st_vercion_code)
//                + " " + versionCode + ")";
        result = versionName;
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.st_settings);
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
}
