package org.almiso.collageapp.android.util;

import org.almiso.collageapp.android.base.CollageApplication;

/**
 * Created by almiso on 10.06.2014.
 */
public class ApiUtils {

    private static CollageApplication application;

    //Base URLs. Need to be change.
    public static String PATTERN_CLIENT_ID = "PLEASE USE YOUR OWN CLIENT_ID";
    public static String PATTERN_CLIENT_SECRET = "PLEASE USE YOUR OWN CLIENT_SECRET";
    public static String CALLBACK_URL = "PLEASE USE YOUR OWN CALLBACK_URL";


    //Private Instagram URLs
    private static final String PATTERN_API = "https://api.instagram.com/v1/";

    //Public Instagram URLs
    public static final String PATTERN_AUTH = "https://api.instagram.com/oauth/authorize/" +
            "?client_id=" + PATTERN_CLIENT_ID +
            "&redirect_uri=" + CALLBACK_URL +
            "&response_type=code&display=touch&scope=basic";

    public static final String PATTERN_AUTH_LOGIN = "https://api.instagram.com/oauth/access_token/" +
            "?client_id=" + PATTERN_CLIENT_ID +
            "&client_secret=" + PATTERN_CLIENT_SECRET +
            "&grant_type=authorization_code" +
            "&redirect_uri=" + CALLBACK_URL
            + "&code=";

    public static String PATTERN_MY_BEST_PHOTOS = PATTERN_API + "users/self/media/liked?";
    public static String PATTERN_USER = PATTERN_API + "users/";
    public static String PATTERN_LOCATION = PATTERN_API + "locations/";
    public static int MAX_SIZE;


    public static void init(CollageApplication _application, int screenSize) {
        application = _application;
        if (screenSize <= 320) {
            MAX_SIZE = 800;
        } else {
            MAX_SIZE = 1280;
        }
    }
}
