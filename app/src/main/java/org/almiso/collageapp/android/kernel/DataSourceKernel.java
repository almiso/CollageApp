package org.almiso.collageapp.android.kernel;

import org.almiso.collageapp.android.core.ExceptionSource;
import org.almiso.collageapp.android.core.InstaSearchSource;
import org.almiso.collageapp.android.core.InstaUserSource;

import java.util.HashMap;

/**
 * Created by almiso on 10.06.2014.
 */
public class DataSourceKernel {
    protected static final String TAG = "DataSourceKernel";

    private ApplicationKernel kernel;
    private ExceptionSource exceptionSource;

    private volatile HashMap<Long, InstaSearchSource> searchSource;
    private volatile HashMap<Long, InstaUserSource> userSource;


    public DataSourceKernel(ApplicationKernel kernel) {
        this.kernel = kernel;
        init();
    }

    private void init() {
        exceptionSource = new ExceptionSource();
        userSource = new HashMap<>();
        searchSource = new HashMap<>();
    }

    public InstaSearchSource getInstaSearchSource(long index) {
        if (userSource.containsKey(index)) {
            return searchSource.get(index);
        } else {
            InstaSearchSource source = new InstaSearchSource(kernel.getApplication());
            searchSource.put(index, source);
            return source;
        }
    }

    public InstaUserSource getInstaUserSource(long index) {
        if (userSource.containsKey(index)) {
            return userSource.get(index);
        } else {
            InstaUserSource source = new InstaUserSource(kernel.getApplication());
            userSource.put(index, source);
            return source;
        }
    }

    public ExceptionSource getExceptionSource() {
        return exceptionSource;
    }

}
