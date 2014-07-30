package org.almiso.collageapp.android.activity;

import android.os.Bundle;
import android.os.SystemClock;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageActivity;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.fragments.screens.FragmentScreenController;
import org.almiso.collageapp.android.fragments.screens.RootController;
import org.almiso.collageapp.android.fragments.screens.RootControllerHolder;

public class StartActivity extends CollageActivity implements RootControllerHolder {

    private static final String TAG = "StartActivity";

    protected FragmentScreenController controller;

    @Override
    public RootController getRootController() {
        return controller;
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            long start = SystemClock.uptimeMillis();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);

        Bundle savedState = null;
        if (savedInstanceState != null && savedInstanceState.containsKey("screen_controller")) {
            savedState = savedInstanceState.getBundle("screen_controller");
        }
        controller = new FragmentScreenController(this, application, savedState);
        if (savedInstanceState != null) {
        } else {
            doInitApp(true);
        }

        Logger.d(TAG, "Kernel: Activity loaded in " + (SystemClock.uptimeMillis() - start) + " ms");
    }

    public void doInitApp(boolean firstAttempt) {

        if (!application.isLoggedIn()) {
            getRootController().openFragmentLaunch();
            return;
        }
        getRootController().openApp();
        onNewIntent(getIntent());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("screen_controller", controller.saveState());
    }


    @Override
    public void onBackPressed() {
        if (controller != null) {
            if (!controller.doSystemBack()) {
                finish();
            }
        } else {
            finish();
        }
    }
}
