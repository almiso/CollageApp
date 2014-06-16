package org.almiso.collageapp.android.preview;

import android.content.Context;
import android.util.AttributeSet;

import org.almiso.collageapp.android.core.model.InstaSearchResult;

/**
 * Created by almiso on 10.06.2014.
 */
public class InstaPreviewView extends BaseView<InstaMediaLoader> {

    protected static final String TAG = "SmallPreviewView";
    private InstaSearchResult searchResult;

    public InstaPreviewView(Context context) {
        super(context);
    }

    public InstaPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstaPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected InstaMediaLoader bindLoader() {
        return application.getUiKernel().getInstaMediaLoader();
    }

    public void requestSearch(InstaSearchResult searchResult) {
        this.searchResult = searchResult;
        requestBind();
    }

    public void requestAvatar() {
        String avatarUrl = application.getAccount().getProfilePictureUrl();
        InstaSearchResult searchResult = new InstaSearchResult("", 0, "", 0, avatarUrl, avatarUrl, avatarUrl);
        this.searchResult = searchResult;
        requestBind();
    }

    @Override
    protected void bind() {
        if (searchResult != null) {
            getLoader().requestSearchInsta(searchResult, this);
        } else {
            getLoader().cancelRequest(this);
        }

    }
}