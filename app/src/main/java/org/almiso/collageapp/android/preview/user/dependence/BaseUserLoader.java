package org.almiso.collageapp.android.preview.user.dependence;

import android.os.Handler;
import android.os.Looper;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.core.model.InstaUserDependence;
import org.almiso.collageapp.android.preview.queue.QueueProcessor;
import org.almiso.collageapp.android.preview.queue.QueueWorker;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by almiso on 22.06.2014.
 */
public abstract class BaseUserLoader<T extends QueueProcessor.BaseTask> {

    private static final String TAG = "BaseUserLoader";

    protected CollageApplication application;
    protected QueueProcessor<T> processor;
    protected QueueWorker<T>[] workers;

    private Handler handler = new Handler(Looper.getMainLooper());
    private CopyOnWriteArrayList<ReceiverHolder> receivers = new CopyOnWriteArrayList<ReceiverHolder>();

    public BaseUserLoader(String name, CollageApplication application) {
        this.application = application;
        this.processor = new QueueProcessor<T>();
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

    protected void requestTask(T task, UserDependenceReceiver receiver) {
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


    protected void onDependenceLoaded(InstaUserDependence res, QueueProcessor.BaseTask task) {
        notifyDependenceLoaded(task, res);
    }

    private void notifyDependenceLoaded(final QueueProcessor.BaseTask task, final InstaUserDependence res) {
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
                        UserDependenceReceiver receiver = holder.getReceiverReference().get();
                        if (receiver != null) {
                            receiver.onUserDependenceReceived(res);
                        }
                    }
                }
            }
        });
    }

    private class ReceiverHolder {
        private String key;
        private WeakReference<UserDependenceReceiver> receiverReference;

        private ReceiverHolder(String key, UserDependenceReceiver receiverReference) {
            this.key = key;
            this.receiverReference = new WeakReference<UserDependenceReceiver>(receiverReference);
        }

        public String getKey() {
            return key;
        }

        public WeakReference<UserDependenceReceiver> getReceiverReference() {
            return receiverReference;
        }
    }


}
