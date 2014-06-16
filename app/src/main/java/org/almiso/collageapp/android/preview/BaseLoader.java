package org.almiso.collageapp.android.preview;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.mdeia.Optimizer;
import org.almiso.collageapp.android.preview.cache.BitmapHolder;
import org.almiso.collageapp.android.preview.cache.ImageCache;
import org.almiso.collageapp.android.preview.cache.ImageStorage;
import org.almiso.collageapp.android.preview.queue.QueueProcessor;
import org.almiso.collageapp.android.preview.queue.QueueWorker;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by almiso on 10.06.2014.
 */

public abstract class BaseLoader<T extends QueueProcessor.BaseTask> {

    private static final String TAG = "BaseLoader";

    protected CollageApplication application;
    protected QueueProcessor<T> processor;
    protected ImageCache imageCache;
    protected ImageStorage imageStorage;
    protected QueueWorker<T>[] workers;

    private Handler handler = new Handler(Looper.getMainLooper());
    private CopyOnWriteArrayList<ReceiverHolder> receivers = new CopyOnWriteArrayList<ReceiverHolder>();

    public BaseLoader(String name, int size, CollageApplication application) {
        this.application = application;
        this.processor = new QueueProcessor<T>();
        this.imageCache = new ImageCache(size);
        this.imageStorage = new ImageStorage(application, name);
        this.workers = createWorkers();

        for (QueueWorker worker : workers) {
            worker.start();
        }
    }

    protected abstract QueueWorker<T>[] createWorkers();

    protected void checkUiThread() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalAccessError("Might be called on UI thread");
        }
    }

    protected boolean checkCache(String key, ImageReceiver receiver) {

        BitmapHolder cached = imageCache.getFromCache(key);

        if (cached != null) {
            receiver.onImageReceived(new ImageHolder(cached, imageCache), true);
            return true;
        }
        return false;
    }


    protected Bitmap checkCachePreview(String key) {
        BitmapHolder cached = imageCache.getFromCache(key);

        if (cached != null) {
            return cached.getBitmap();
        }
        return null;
    }

    protected void requestTask(T task, ImageReceiver receiver) {
        checkUiThread();

        String key = task.getKey();

        if (checkCache(key, receiver)) {
            return;
        }

        for (ReceiverHolder holder : receivers) {
            if (holder.getReceiverReference().get() == null) {
                receivers.remove(holder);
                continue;
            }
            if (holder.getReceiverReference().get() == receiver) {
                receivers.remove(holder);
                break;
            }
        }
        receivers.add(new ReceiverHolder(key, receiver));
        processor.requestTask(task);
    }


    protected Optimizer.BitmapInfo tryToLoadFromCache(String key, Bitmap dest) {
        return imageStorage.tryLoadFile(key, dest);
    }

    private void notifyMediaLoaded(final QueueProcessor.BaseTask task, final BitmapHolder bitmap) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (ReceiverHolder holder : receivers) {
                    if (holder.getReceiverReference().get() == null) {
                        receivers.remove(holder);
                        continue;
                    }

                    if (holder.getKey().equals(task.getKey())) {
                        receivers.remove(holder);
                        ImageReceiver receiver = holder.getReceiverReference().get();
                        if (receiver != null) {
                            receiver.onImageReceived(new ImageHolder(bitmap, imageCache), true);
                        }
                    }
                }
                imageCache.decReference(task.getKey(), BaseLoader.this);
            }
        });
    }

    protected void onImageLoaded(Bitmap res, QueueProcessor.BaseTask task, int size) {
        BitmapHolder holder = new BitmapHolder(res, task.getKey());
        imageCache.putToCache(size, holder, BaseLoader.this);
        notifyMediaLoaded(task, holder);
    }

    protected void onImageLoaded(Bitmap res, int w, int h, QueueProcessor.BaseTask task, int size) {
        BitmapHolder holder = new BitmapHolder(res, task.getKey(), w, h);
        imageCache.putToCache(size, holder, BaseLoader.this);
        notifyMediaLoaded(task, holder);
    }

    public void cancelRequest(ImageReceiver receiver) {
        checkUiThread();

        HashSet<String> removedKey = new HashSet<String>();
        for (ReceiverHolder holder : receivers) {
            if (holder.getReceiverReference().get() == null) {
                receivers.remove(holder);
                removedKey.add(holder.getKey());
                continue;
            }
            if (holder.getReceiverReference().get() == receiver) {
                receivers.remove(holder);
                removedKey.add(holder.getKey());
                break;
            }
        }

        for (ReceiverHolder holder : receivers) {
            if (removedKey.contains(holder.getKey())) {
                removedKey.remove(holder.getKey());
            }
        }

        for (String s : removedKey) {
            processor.removeTask(s);
        }
    }

    private class ReceiverHolder {
        private String key;
        private WeakReference<ImageReceiver> receiverReference;

        private ReceiverHolder(String key, ImageReceiver receiverReference) {
            this.key = key;
            this.receiverReference = new WeakReference<ImageReceiver>(receiverReference);
        }

        public String getKey() {
            return key;
        }

        public WeakReference<ImageReceiver> getReceiverReference() {
            return receiverReference;
        }
    }
}