package org.almiso.collageapp.android.preview;

import android.graphics.Bitmap;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.media.Optimizer;
import org.almiso.collageapp.android.preview.media.BaseTask;
import org.almiso.collageapp.android.preview.media.SearchInstaTask;
import org.almiso.collageapp.android.preview.queue.QueueWorker;
import org.almiso.collageapp.android.util.ApiUtils;
import org.almiso.collageapp.android.util.IOUtils;

/**
 * Created by almiso on 10.06.2014.
 */

public class InstaMediaLoader extends BaseLoader<BaseTask> {

    protected static final String TAG = "InstaMediaLoader";

    private static final int SIZE_SMALL_PREVIEW = 4;

    private Bitmap fullImageCached = null;
    private final Object fullImageCachedLock = new Object();
    private static final int cacheSize = 4 * 1024 * 1024; // 4MiB

    public InstaMediaLoader(CollageApplication application) {
        super("previews", cacheSize, application);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected QueueWorker<BaseTask>[] createWorkers() {
        return new QueueWorker[]{new MapWorker()};
    }

    public void requestSearchInsta(InstaSearchResult result, ImageReceiver receiver) {
        requestTask(new SearchInstaTask(result), receiver);
    }

    public Bitmap tryLoadInstaPreview(InstaSearchResult result) {
        return checkCachePreview(result.getThumbnailUrl());
    }

    public void cancelRequest(ImageReceiver receiver) {
        super.cancelRequest(receiver);
    }

    private Bitmap fetchMediaPreview() {
        Bitmap res = imageCache.findFree(SIZE_SMALL_PREVIEW);
        if (res == null) {
            res = Bitmap
                    .createBitmap(PreviewConfig.MEDIA_PREVIEW, PreviewConfig.MEDIA_PREVIEW, Bitmap.Config.ARGB_8888);
        } else {
            Optimizer.clearBitmap(res);
        }
        return res;
    }

    private class MapWorker extends QueueWorker<BaseTask> {

        private MapWorker() {
            super(processor);
        }

        @Override
        protected boolean needRepeatOnError() {
            return true;
        }

        protected void processWeb(SearchInstaTask thumbTask) throws Exception {
            synchronized (fullImageCachedLock) {
                if (fullImageCached == null) {
                    fullImageCached = Bitmap.createBitmap(ApiUtils.MAX_SIZE / 2, ApiUtils.MAX_SIZE / 2,
                            Bitmap.Config.ARGB_8888);
                }
                Optimizer.BitmapInfo info = tryToLoadFromCache(thumbTask.getStorageKey(), fullImageCached);
                if (info != null) {
                    Bitmap res = fetchMediaPreview();
                    Optimizer.scaleToFill(fullImageCached, info.getWidth(), info.getHeight(), res);
                    onImageLoaded(res, res.getWidth(), res.getHeight(), thumbTask, SIZE_SMALL_PREVIEW);
                    return;
                }
            }

            byte[] data = IOUtils.downloadFile(thumbTask.getResult().getThumbnailUrl());
            synchronized (fullImageCachedLock) {
                fullImageCached = Bitmap.createBitmap(ApiUtils.MAX_SIZE / 2, ApiUtils.MAX_SIZE / 2,
                        Bitmap.Config.ARGB_8888);

                final Bitmap newRes = Optimizer.optimize(data);
                onImageLoaded(newRes, newRes.getWidth(), newRes.getHeight(), thumbTask, SIZE_SMALL_PREVIEW);
            }

        }

        @Override
        protected boolean processTask(BaseTask task) throws Exception {

            if (task instanceof SearchInstaTask) {
                processWeb((SearchInstaTask) task);
            }
            return true;
        }

        @Override
        public boolean isAccepted(BaseTask task) {
            return task instanceof SearchInstaTask;
        }
    }


}
