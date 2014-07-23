package org.almiso.collageapp.android.loader;

import org.almiso.collageapp.android.preview.media.BaseTask;

/**
 * Created by Alexandr Sosorev on 22.07.2014.
 */
public class ImageTask extends BaseTask {

    private Object data;

    public ImageTask(Object data) {
        super();
        this.data = data;
    }

    @Override
    protected String getStorageKey() {
        return String.valueOf(data);
    }

//    @Override
//    public String getKey() {
//        return String.valueOf(data);
//    }
}
