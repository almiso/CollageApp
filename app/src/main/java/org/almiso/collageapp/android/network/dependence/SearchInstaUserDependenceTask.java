package org.almiso.collageapp.android.network.dependence;

import org.almiso.collageapp.android.core.model.InstaUserDependence;
import org.almiso.collageapp.android.network.queue.tasks.BaseTask;

/**
 * Created by almiso on 22.06.2014.
 */
public class SearchInstaUserDependenceTask extends BaseTask {
    private InstaUserDependence result;

    public SearchInstaUserDependenceTask(InstaUserDependence result) {
        super();
        this.result = result;
    }

    public InstaUserDependence getResult() {
        return result;
    }

    @Override
    public String getStorageKey() {
        return String.valueOf(result.getUserId());
    }
}