package org.almiso.collageapp.android.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import org.almiso.collageapp.android.log.Logger;

/**
 * Created by almiso on 10.06.2014.
 */
public class UiResponsibility {
    private static final String TAG = "UiResponsibility";
    private static final long DEFAULT_DELAY = 250;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Logger.d(TAG, "notify");
            onResumed();
        }
    };

    private long minimumTime = 0;

    private volatile boolean isPaused = false;

    private Object locker = new Object();

    public void doPause() {
        doPause(DEFAULT_DELAY);
    }

    public void doPause(long delta) {
        minimumTime = Math.max(SystemClock.uptimeMillis() + delta, minimumTime);
        handler.removeMessages(0);
        handler.sendEmptyMessageAtTime(0, minimumTime);
        isPaused = true;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void waitForResume() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Logger.w(TAG, "Ignoring waitForResume");
            return;
        }
        if (!isPaused())
            return;
        synchronized (locker) {
            try {
                locker.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void onResumed() {
        isPaused = false;
        synchronized (locker) {
            locker.notifyAll();
        }
    }
}
