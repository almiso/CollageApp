package org.almiso.collageapp.android.core;

import android.content.Context;
import android.content.SharedPreferences;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.core.model.InstaUser;

/**
 * Created by almiso on 13.06.2014.
 */
public class Account {

    private static final String PREFERENCE_NAME = "org.almiso.collageapp.android.Account.pref";
    private SharedPreferences preferences;

//    private long id;
//    private String username;
//    private String full_name;
//    private String profile_picture;
//    private String access_token;
//    private String request_token;

    private CollageApplication application;

    public Account(CollageApplication application) {
        this.application = application;
        this.preferences = application.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return !preferences.getString("access_token", "").equals("");
    }

    public void logIn(long id, String username, String full_name, String profile_picture, String access_token, String request_token) {
        preferences.edit().putLong("id", id).
                putString("username", username).
                putString("full_name", full_name).
                putString("profile_picture", profile_picture).
                putString("access_token", access_token).
                putString("request_token", request_token).commit();
    }

    public void logOut() {
        clearSettings();
    }

    private void clearSettings() {
        String[] keys = preferences.getAll().keySet().toArray(new String[0]);
        SharedPreferences.Editor editor = preferences.edit();
        for (String k : keys) {
            editor.remove(k);
        }
        editor.commit();
    }

    public String getAccessToken() {
        return preferences.getString("access_token", "");
    }

    public String getFullName() {
        return preferences.getString("full_name", "");
    }

    public String getUsername() {
        return preferences.getString("username", "");
    }

    public String getProfilePictureUrl() {
        return preferences.getString("profile_picture", "");
    }

    public long getId() {
        return preferences.getLong("id", -1);
    }

    public InstaUser getMe() {
        InstaUser user = new InstaUser();
        user.setId(getId());
        user.setUsername(getUsername());
        user.setBio("");
        user.setWebsite("");
        user.setProfile_picture_url(getProfilePictureUrl());
        user.setFull_name(getFullName());
        return user;
    }
}
