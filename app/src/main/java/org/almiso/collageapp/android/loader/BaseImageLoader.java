package org.almiso.collageapp.android.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.preview.queue.QueueProcessor;
import org.almiso.collageapp.android.preview.queue.QueueWorker;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Alexandr Sosorev on 22.07.2014.
 */
public abstract class BaseImageLoader<T extends QueueProcessor.BaseTask> {

    private static final String TAG = "BaseImageLoader";

    protected CollageApplication application;
    protected QueueWorker<T>[] workers;
    protected QueueProcessor<T> processor;

    private Handler handler = new Handler(Looper.getMainLooper());
    private CopyOnWriteArrayList<ReceiverHolder> receivers = new CopyOnWriteArrayList<ReceiverHolder>();

    public BaseImageLoader(CollageApplication application) {
        this.application = application;
        this.processor = new QueueProcessor<T>();
        this.workers = createWorkers();

        for (QueueWorker worker : workers) {
            worker.start();
        }
    }

    protected abstract QueueWorker<T>[] createWorkers();

    protected void requestTask(T task, ImageReceiver receiver) {
        checkUiThread();

        String key = task.getKey();

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

    protected void checkUiThread() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalAccessError("Might be called on UI thread");
        }
    }

    protected void notifyMediaLoaded(final QueueProcessor.BaseTask task, final Bitmap bitmap) {
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
                            receiver.onImageReceived(bitmap, true);
                        }
                    }
                }
//                imageCache.decReference(task.getKey(), BaseLoader.this);
            }
        });
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
