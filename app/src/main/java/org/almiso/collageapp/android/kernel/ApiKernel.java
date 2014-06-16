package org.almiso.collageapp.android.kernel;

import org.almiso.collageapp.android.network.Api;

/**
 * Created by almiso on 08.06.2014.
 */
public class ApiKernel {

    protected static final String TAG = "ApiKernel";
    private ApplicationKernel kernel;
    private Api api;

    public ApiKernel(ApplicationKernel kernel) {
        this.kernel = kernel;
    }

    public void runKernel() {
        api = new Api(kernel.getApplication());
    }

    public ApplicationKernel getKernel() {
        return kernel;
    }

    public Api getApi() {
        return api;
    }
}
