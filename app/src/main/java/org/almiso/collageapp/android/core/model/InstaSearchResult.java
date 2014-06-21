package org.almiso.collageapp.android.core.model;

import java.io.Serializable;

/**
 * Created by almiso on 10.06.2014.
 */
public class InstaSearchResult implements Serializable {

    private int index;
    private String id; // "id":"738028311313126121_3"
    private String type; // "type":"image"
    private long likes; // "likes":"count":15977,
    private String lowResolutionUrl; // "images": "low_resolution":
    private String thumbnailUrl; // "images": "thumbnail":
    private String standardResolutionUrl; // "images": "standard_resolution":
    private InstaUser author;

    private boolean isChecked;

    public InstaSearchResult(String id, int index, String type, long likes, String lowResolutionUrl,
                             String thumbnailUrl, String standardResolutionUrl, InstaUser author) {
        this.id = id;
        this.index = index;
        this.type = type;
        this.likes = likes;
        this.lowResolutionUrl = lowResolutionUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.standardResolutionUrl = standardResolutionUrl;
        this.author = author;
    }

    public int getIndex() {
        return index;
    }

    public String geThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getLikesCount() {
        return String.valueOf(likes);
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getStandardResolutionUrl() {
        return standardResolutionUrl;
    }

    public String getLowResolutionUrl() {
        return lowResolutionUrl;
    }

    public InstaUser getAuthor() {
        return author;
    }


}
