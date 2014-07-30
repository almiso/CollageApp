package org.almiso.collageapp.android.kernel;

import android.os.SystemClock;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.network.dependence.InstaUserDependenceLoader;
import org.almiso.collageapp.android.network.util.ApiUtils;

/**
 * Created by almiso on 10.06.2014.
 */
public class UiKernel {
    protected static final String TAG = "UiKernel";

    protected ApplicationKernel kernel;
    protected CollageApplication application;

//    private ImageStorage webImageStorage;
    private InstaUserDependenceLoader userLoader;

    public UiKernel(ApplicationKernel kernel) {
        this.kernel = kernel;
        this.application = kernel.getApplication();

        Logger.d(TAG, "Creating ui kernel");

        long start = System.currentTimeMillis();
        int screenSize = Math.min(application.getResources().getDisplayMetrics().widthPixels, application
                .getResources().getDisplayMetrics().heightPixels);
        ApiUtils.init(application, screenSize);
        Logger.d(TAG, "ApiUtils loaded in " + (SystemClock.uptimeMillis() - start) + " ms");

        start = SystemClock.uptimeMillis();
        userLoader = new InstaUserDependenceLoader(application);
        Logger.d(TAG, "MediaLoader loaded in " + (SystemClock.uptimeMillis() - start) + " ms");


    }


    public InstaUserDependenceLoader getInstaUserDependenceLoader() {
        return userLoader;
    }

}
