package org.almiso.collageapp.android.network.tasks;

/**
 * Created by almiso on 07.06.2014.
 */
public abstract class AsyncAction {

    public void beforeExecute() {
    }

    public void afterExecute() {

    }

    public abstract void execute() throws AsyncException;

    public void onException(AsyncException e) {

    }

    public void onCanceled() {

    }

    public boolean repeatable() {
        return false;
    }
}
