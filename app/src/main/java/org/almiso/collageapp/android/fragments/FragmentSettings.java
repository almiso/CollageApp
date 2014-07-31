package org.almiso.collageapp.android.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageImageFragment;

/**
 * Created by almiso on 13.06.2014.
 */
public class FragmentSettings extends CollageImageFragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        ((TextView) view.findViewById(R.id.tvVersion)).setText(getVersionString());
        view.findViewById(R.id.buttonExit).setOnClickListener(this);
        view.findViewById(R.id.buttonClearCache).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonExit:
                onLogOut();
                break;
            case R.id.buttonClearCache:
                mImageFetcher.clearCache();
                Toast.makeText(getActivity(), R.string.st_clear_cache_complete, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String getVersionString() {
        String versionName;

        PackageManager manager = application.getPackageManager();
        try {
            versionName = manager.getPackageInfo(application.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "1.0.0";
            e.printStackTrace();
        }
        return versionName;
    }

    private void onLogOut() {
        final Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.st_exit);
        builder.setMessage(R.string.st_exit_question);
        builder.setPositiveButton(R.string.st_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getRootController().onLogout();
            }
        });
        builder.setNegativeButton(R.string.st_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
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
