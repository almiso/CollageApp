package org.almiso.collageapp.android.loader;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.media.Optimizer;
import org.almiso.collageapp.android.preview.queue.QueueWorker;
import org.almiso.collageapp.android.util.IOUtils;

/**
 * Created by Alexandr Sosorev on 22.07.2014.
 */
public class ImageLoader extends BaseImageLoader<ImageTask> {
    protected static final String TAG = "ImageLoader";

    private CollageApplication application;
    private ImageCache mImageCache;

    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

    public ImageLoader(CollageApplication application) {
        super(application);
        this.application = application;
    }

    @Override
    protected QueueWorker<ImageTask>[] createWorkers() {
        return new QueueWorker[]{new ImageWorker()};
    }

    public void addImageCache(FragmentManager fragmentManager, float memCacheSizePercent) {
        mImageCache = ImageCache.getInstance(fragmentManager, memCacheSizePercent);
    }


    public void request(Object data, ImageReceiver receiver) {
        requestTask(new ImageTask(data), receiver);
    }


    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }

    //    <----------------------------------------------------->
    //    <----------------------------------------------------->
    //    <----------------------------------------------------->
    private class ImageWorker extends QueueWorker<ImageTask> {

        private ImageWorker() {
            super(processor);
        }

        @Override
        protected boolean processTask(ImageTask task) throws Exception {
            if (task instanceof ImageTask) {
                processWeb(task);
            }
            return true;
        }

        @Override
        protected boolean needRepeatOnError() {
            return true;
        }


        @Override
        public boolean isAccepted(ImageTask task) {
            return task instanceof ImageTask;
        }
    }

    protected void processWeb(ImageTask task) throws Exception {

        synchronized (mPauseWorkLock) {
            while (mPauseWork) {
                try {
                    mPauseWorkLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }

        Bitmap bitmap = null;
//        synchronized (mPauseWorkLock) {
            if (mImageCache != null && !mExitTasksEarly) {
                bitmap = mImageCache.getBitmapFromMemCache(String.valueOf(task.getStorageKey()));
            }
            if (bitmap != null && !mExitTasksEarly) {
                notifyMediaLoaded(task, bitmap);
            } else {
                final Bitmap newRes = loadImage(task);
                mImageCache.addBitmapToCache(task.getStorageKey(), newRes);
                notifyMediaLoaded(task, newRes);
//            }
        }
    }


    private Bitmap loadImage(ImageTask task) throws Exception {

        byte[] data = IOUtils.downloadFile(task.getStorageKey());
        final Bitmap bitmap = Optimizer.optimize(data);
        return bitmap;
    }

}
