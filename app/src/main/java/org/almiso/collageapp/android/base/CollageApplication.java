package org.almiso.collageapp.android.base;

import android.app.Application;

import org.almiso.collageapp.android.core.Account;
import org.almiso.collageapp.android.kernel.ApplicationKernel;
import org.almiso.collageapp.android.kernel.AuthKernel;
import org.almiso.collageapp.android.kernel.DataSourceKernel;
import org.almiso.collageapp.android.kernel.KernelsLoader;
import org.almiso.collageapp.android.kernel.UiKernel;
import org.almiso.collageapp.android.network.Api;

/**
 * Created by almiso on 07.06.2014.
 */
public class CollageApplication extends Application {
    protected static final String TAG = "IntroApplication";

    private ApplicationKernel kernel;
    private KernelsLoader kernelsLoader;

    @Override
    public void onCreate() {
        if (kernel != null) {
            super.onCreate();
            return;
        }

        kernel = new ApplicationKernel(this);
        super.onCreate();

        kernelsLoader = new KernelsLoader();
        kernelsLoader.stagedLoad(kernel);
    }

    public ApplicationKernel getKernel() {
        return kernel;
    }

    public Api getApi() {
        return kernel.getApiKernel().getApi();
    }

    public UiKernel getUiKernel() {
        return kernel.getUiKernel();
    }

    public DataSourceKernel getDataSourceKernel() {
        return kernel.getDataSourceKernel();
    }

    public AuthKernel getAuthKernel() {
        return kernel.getAuthKernel();
    }

    public Account getAccount() {
        return kernel.getAuthKernel().getAccount();
    }

    public boolean isLoggedIn() {
        return kernel.getAuthKernel().isLoggedIn();
    }
}
