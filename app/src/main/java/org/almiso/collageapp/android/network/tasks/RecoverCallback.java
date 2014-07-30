package org.almiso.collageapp.android.network.tasks;

/**
 * Created by almiso on 07.06.2014.
 */
public interface RecoverCallback {
    public void onError(AsyncException e, Runnable onRepeat, Runnable onCancel);
}
