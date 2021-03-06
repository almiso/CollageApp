package org.almiso.collageapp.android.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import java.io.IOException;

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
        view.findViewById(R.id.layoutGooglePlay).setOnClickListener(this);
        view.findViewById(R.id.layoutShare).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonExit:
                onLogOut();
                break;
            case R.id.buttonClearCache:
                onClearCache();
                break;
            case R.id.layoutGooglePlay:
                rateApp();
                break;
            case R.id.layoutShare:
                shareApp();
                break;
            default:
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

    private void onClearCache() {
        mImageFetcher.clearCache();
        try {
            application.getDataSourceKernel().clearTmp();
            Toast.makeText(getActivity(), R.string.st_clear_cache_complete, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.st_error_clear_tmp, Toast.LENGTH_SHORT).show();
        }
    }

    private void rateApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + application.getPackageName()));
        if (!mStartActivity(intent)) {
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + application.getPackageName()));
            if (!mStartActivity(intent)) {
                Toast.makeText(application, getString(R.string.st_error_do_action), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean mStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void shareApp() {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.st_share_app_text));
        shareIntent.setType("text/plain");
        Intent chooser = Intent.createChooser(shareIntent, getResources().getString(R.string.st_share_using));
        if (chooser.resolveActivity(activity.getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            Toast.makeText(application, getString(R.string.st_error_do_action), Toast.LENGTH_SHORT).show();
        }
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
