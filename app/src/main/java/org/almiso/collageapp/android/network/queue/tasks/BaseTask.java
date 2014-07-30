package org.almiso.collageapp.android.network.queue.tasks;

import org.almiso.collageapp.android.network.queue.QueueProcessor;

/**
 * Created by almiso on 10.06.2014.
 */
public abstract class BaseTask extends QueueProcessor.BaseTask {

    protected BaseTask() {
    }

    protected abstract String getStorageKey();

    @Override
    public final String getKey() {
        return getStorageKey();
    }
}