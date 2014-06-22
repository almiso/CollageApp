package org.almiso.collageapp.android.core.model;

import java.io.Serializable;

/**
 * Created by almiso on 21.06.2014.
 */
public class InstaUserDependence implements Serializable {

    private long userId;
    private long mediaCount;
    private long followsCount;
    private long followedByCount;

    private boolean isPrivate;

    public InstaUserDependence(long userId, long mediaCount, long followsCount, long followedByCount, boolean isPrivate) {
        this.userId = userId;
        this.mediaCount = mediaCount;
        this.followsCount = followsCount;
        this.followedByCount = followedByCount;
        this.isPrivate = isPrivate;
    }

    public long getUserId() {
        return userId;
    }

    public long getMediaCount() {
        return mediaCount;
    }

    public long getFollowsCount() {
        return followsCount;
    }

    public long getFollowedByCount() {
        return followedByCount;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public String toString() {
        return "InstaUserDependence: userId = " + userId + "; " +
                "mediaCount = " + mediaCount +
                "; followsCount = " + followsCount +
                "; followedByCount = " + followedByCount+
                "; isPrivate = " + isPrivate;


    }
}