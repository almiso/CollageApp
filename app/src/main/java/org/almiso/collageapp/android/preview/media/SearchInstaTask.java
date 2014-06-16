package org.almiso.collageapp.android.preview.media;

import org.almiso.collageapp.android.core.model.InstaSearchResult;

/**
 * Created by almiso on 10.06.2014.
 */
public class SearchInstaTask extends BaseTask {
    private InstaSearchResult result;

    public SearchInstaTask(InstaSearchResult result) {
        super();
        this.result = result;
    }

    public InstaSearchResult getResult() {
        return result;
    }

    @Override
    public String getStorageKey() {
        return result.geThumbnailUrl();
    }
}