package org.almiso.collageapp.android.core;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by almiso on 16.06.2014.
 */
public class ExceptionSource {
    protected static final String TAG = "NotificationSource";
    private final CopyOnWriteArrayList<ExceptionSourceListener> listeners
            = new CopyOnWriteArrayList<ExceptionSourceListener>();

    private Handler handler = new Handler(Looper.getMainLooper());

    public ExceptionSource() {
    }

    public void registerListener(ExceptionSourceListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(ExceptionSourceListener listener) {
        listeners.remove(listener);
    }

    public void notifyException(final String error) {
        if (error != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (ExceptionSourceListener listener : listeners) {
                        listener.onException(error);
                    }
                }
            });
        }
    }
}
