package org.almiso.collageapp.android.kernel;

import android.os.SystemClock;

import org.almiso.collageapp.android.log.Logger;


/**
 * Created by almiso on 07.06.2014.
 */
public class KernelsLoader {

    private static final String TAG = "KernelsLoader";

    public boolean stagedLoad(final ApplicationKernel kernel) {
        long initStart = SystemClock.uptimeMillis();

        kernel.initApiKernel();
        kernel.initUiKernel();
        kernel.initSourcesKernel();
        kernel.initAuthKernel();

        Logger.d(TAG, "Kernels created in " + (SystemClock.uptimeMillis() - initStart) + " ms");

        kernel.runKernels();

        Logger.d(TAG, "Kernels loaded in " + (SystemClock.uptimeMillis() - initStart) + " ms");
        return true;
    }
}
