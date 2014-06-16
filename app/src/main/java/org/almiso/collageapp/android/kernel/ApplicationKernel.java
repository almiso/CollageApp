package org.almiso.collageapp.android.kernel;

import android.os.SystemClock;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.log.Logger;

/**
 * Created by almiso on 07.06.2014.
 */
public class ApplicationKernel {

    private static final String TAG = "Kernel";

    private volatile CollageApplication application;

	/* --- Kernels --- */

    private volatile AuthKernel authKernel;

    private volatile UiKernel uiKernel;

    private volatile DataSourceKernel dataSourceKernel;

    private volatile ApiKernel apiKernel;

	/* --- Kernels --- */

    public ApplicationKernel(CollageApplication application) {
        this.application = application;
        initLogging();
        Logger.d(TAG, "--------------- Kernel Created ------------------");
    }


    public void initAuthKernel() {
        long start = SystemClock.uptimeMillis();
        authKernel = new AuthKernel(this);
        Logger.d(TAG, "AuthKernel init in "
                + (SystemClock.uptimeMillis() - start) + " ms");
    }

    public void initApiKernel() {
        long start = SystemClock.uptimeMillis();
        apiKernel = new ApiKernel(this);
        Logger.d(TAG, "ApiKernel init in "
                + (SystemClock.uptimeMillis() - start) + " ms");
    }

    public void initUiKernel() {
        long start = SystemClock.uptimeMillis();
        uiKernel = new UiKernel(this);
        Logger.d(TAG, "UiKernel init in " + (SystemClock.uptimeMillis() - start) + " ms");
    }

    public void initSourcesKernel() {
        long start = SystemClock.uptimeMillis();
        dataSourceKernel = new DataSourceKernel(this);
        Logger.d(TAG, "DataSourceKernel init in " + (SystemClock.uptimeMillis() - start) + " ms");
    }

    public void runKernels() {
        long kernelsStart = SystemClock.uptimeMillis();

        long start = SystemClock.uptimeMillis();
        apiKernel.runKernel();
        Logger.d(TAG, "ApiKernel run in "
                + (SystemClock.uptimeMillis() - start) + " ms");


        Logger.d(TAG, "Kernels started in "
                + (SystemClock.uptimeMillis() - kernelsStart) + " ms");
    }

    public ApiKernel getApiKernel() {
        return apiKernel;
    }

    private void initLogging() {
        Logger.init(application);
    }

    public CollageApplication getApplication() {
        return application;
    }

    public AuthKernel getAuthKernel() {
        return authKernel;
    }

    public UiKernel getUiKernel() {
        return uiKernel;
    }

    public DataSourceKernel getDataSourceKernel() {
        return dataSourceKernel;
    }



    public void logOut() {
        long start = SystemClock.uptimeMillis();
        authKernel.logOut();
        Logger.d(TAG, "LogOut: atuhKernel in "
                + (SystemClock.uptimeMillis() - start) + " ms");
    }

}
