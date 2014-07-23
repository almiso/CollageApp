package org.almiso.collageapp.android.loader;

import android.graphics.Bitmap;

/**
 * Created by Alexandr Sosorev on 22.07.2014.
 */
public interface ImageReceiver {
    public void onImageReceived(Bitmap bitmap, boolean intermediate);
}
