package org.almiso.collageapp.android.log;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by almiso on 07.06.2014.
 */
public class Logger {
    private static class LogRecord {
        private long time;
        private String tag;
        private String thread;
        private String message;

        private LogRecord(long time, String tag, String thread, String message) {
            this.time = time;
            this.tag = tag;
            this.thread = thread;
            this.message = message;
        }
    }

    private static final boolean ENABLED = true;
    private static final boolean LOG_THREAD = false;

    private static ArrayList<LogRecord> cachedRecords = new ArrayList<LogRecord>();

    private static String logPath;

    private static boolean isInitied = false;
    private static boolean isEnabled = false;

    private static boolean enqueued = false;
    private static final Object locker = new Object();

    private static BufferedWriter writer;
    private static Thread writerThread;
    private static Handler writerHandler;
    private static final Runnable dropCacheRunnable = new Runnable() {
        @Override
        public void run() {
            dropCache();
        }
    };

    public static void enableDiskLog() {
        isEnabled = true;
    }

    public static void disableDiskLog() {
        isEnabled = false;
    }

    public static synchronized void init(Context context) {
        logPath = context.getFilesDir().getAbsolutePath() + "/log.txt";
        isInitied = true;
        try {
            writer = new BufferedWriter(new FileWriter(logPath, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
                writerHandler = new Handler();
                Looper.loop();
            }
        });
        writerThread.start();
    }

    private static void addLogRecord(LogRecord record) {
        if (isEnabled && isInitied) {
            synchronized (cachedRecords) {
                cachedRecords.add(record);
                if (!enqueued && writerHandler != null) {
                    enqueued = true;
                    writerHandler.postDelayed(dropCacheRunnable, 1000);
                }
            }
        }
    }

    private static synchronized void dropCache() {
        if (ENABLED && isInitied && writer != null) {
            synchronized (locker) {
                LogRecord[] records = null;
                synchronized (cachedRecords) {
                    if (cachedRecords.size() > 0) {
                        records = cachedRecords.toArray(new LogRecord[cachedRecords.size()]);
                        cachedRecords.clear();
                        enqueued = false;
                    }
                }
                if (records != null) {
                    for (LogRecord record : records) {
                        try {
                            writer.write(record.time + "|" + record.tag + "|" + record.thread + "|" + record.message);
                            writer.newLine();
                        } catch (IOException e) {
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    try {
                        writer.flush();
                    } catch (IOException e) {
                        return;
                    }
                }
            }
        }
    }

    public static void d(String TAG, String message) {
        if (!ENABLED) {
            return;
        }
        if (LOG_THREAD) {
            Log.d("CollageApp|" + TAG, Thread.currentThread().getName() + "| " + message);
        } else {
            Log.d("CollageApp|" + TAG, message);
        }
        if (isEnabled && isInitied) {
            addLogRecord(new LogRecord(System.currentTimeMillis(), TAG, Thread.currentThread().getName(), message));
        }
    }

    public static void e(String TAG, String message) {
        if (!ENABLED) {
            return;
        }
        Log.e("CollageApp|" + TAG, message);
        if (isEnabled && isInitied) {
            addLogRecord(new LogRecord(System.currentTimeMillis(), TAG, Thread.currentThread().getName(), message));
        }
    }

    public static void e(String TAG, String message, Exception e) {
        if (!ENABLED) {
            return;
        }
        Log.e("CollageApp|" + TAG, message, e);
        if (isEnabled && isInitied) {
            addLogRecord(new LogRecord(System.currentTimeMillis(), TAG, Thread.currentThread().getName(), message
                    + "\n" + android.util.Log.getStackTraceString(e)));
        }
    }

    public static void w(String TAG, String message) {
        if (!ENABLED) {
            return;
        }
        if (LOG_THREAD) {
            Log.w("CollageApp|" + TAG, Thread.currentThread().getName() + "| " + message);
        } else {
            Log.w("CollageApp|" + TAG, message);
        }
        if (isEnabled && isInitied) {
            addLogRecord(new LogRecord(System.currentTimeMillis(), TAG, Thread.currentThread().getName(), message));
        }
    }

}
