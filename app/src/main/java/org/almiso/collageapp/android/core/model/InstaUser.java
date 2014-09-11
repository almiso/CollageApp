package org.almiso.collageapp.android.core.model;

import java.io.Serializable;

/**
 * Created by almiso on 08.06.2014.
 */
public class InstaUser implements Serializable {

    private long id;
    private String username;
    private String bio;
    private String website;
    private String profile_picture_url;
    private String full_name;


    public InstaUser() {
    }

    @Override
    public String toString() {
        return "User: " +
                "id: " + id +
                ", username: " + username +
                ", full_name: " + full_name +
                ", bio: " + bio +
                ", website: " + website +
                ", profile_picture_url: " + profile_picture_url;
    }

    public String getDisplayName() {
        if (full_name != null && !full_name.equals(""))
            return full_name;
        else if (username != null && !username.equals(""))
            return username;
        else return "User";
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
