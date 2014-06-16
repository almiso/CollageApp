package org.almiso.collageapp.android.kernel;

import org.almiso.collageapp.android.core.ExceptionSource;
import org.almiso.collageapp.android.core.InstaSearchSource;
import org.almiso.collageapp.android.log.Logger;

/**
 * Created by almiso on 10.06.2014.
 */
public class DataSourceKernel {
    protected static final String TAG = "DataSourceKernel";

    private ApplicationKernel kernel;
    private ExceptionSource exceptionSource;

    private volatile InstaSearchSource instaSearchSource;


    public DataSourceKernel(ApplicationKernel kernel) {
        this.kernel = kernel;
        init();
    }

    private void init() {
        instaSearchSource = new InstaSearchSource(kernel.getApplication());
        exceptionSource = new ExceptionSource();
    }

    public InstaSearchSource getInstaSearchSource() {
        return instaSearchSource;
    }

    public ExceptionSource getExceptionSource() {
        return exceptionSource;
    }

}
