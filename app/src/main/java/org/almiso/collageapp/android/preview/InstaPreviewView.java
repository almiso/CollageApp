package org.almiso.collageapp.android.preview;

import android.content.Context;
import android.util.AttributeSet;

import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.core.model.InstaUser;

/**
 * Created by almiso on 10.06.2014.
 */
public class InstaPreviewView extends BaseView<InstaMediaLoader> {

    protected static final String TAG = "SmallPreviewView";
    private InstaSearchResult searchResult;

    public InstaPreviewView(Context context) {
        super(context);
        init(context);
    }

    public InstaPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InstaPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (this.isInEditMode()) {
        }
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
        InstaSearchResult searchResult = new InstaSearchResult("", 0, "image", 0,
                avatarUrl, avatarUrl, avatarUrl, application.getAccount().getMe());
        this.searchResult = searchResult;
        requestBind();
    }

    public void requestUserAvatar(InstaUser user) {
        InstaSearchResult searchResult = new InstaSearchResult("", 0, "image", 0,
                user.getProfile_picture_url(),
                user.getProfile_picture_url(),
                user.getProfile_picture_url(), user);
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