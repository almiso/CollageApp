package org.almiso.collageapp.android.network.util;

import org.almiso.collageapp.android.base.CollageApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by almiso on 10.06.2014.
 */
public class ApiUtils {

    private static CollageApplication application;

    //Base URLs. Need to be change.
    public static String PATTERN_CLIENT_ID = "7c3fa5e4f18b41f4aac621e8c0f35f3f";
    public static String PATTERN_CLIENT_SECRET = "7b618023458f4378b7ee51ab42d9792a";
    public static String CALLBACK_URL = "https://github.com/almiso/CollageApp";

    //Ad utils
    public static String AD_TEST_DEVICE = "PLEASE_USE_YOUR_OWN_AD_UTILS";
    public static String AD_UNIT_ID_MAIN = "PLEASE_USE_YOUR_OWN_AD_UTILS";
    public static String AD_UNIT_ID_SEARCH_USER = "PLEASE_USE_YOUR_OWN_AD_UTILS";
    public static String AD_UNIT_ID_USER_PROFILE = "PLEASE_USE_YOUR_OWN_AD_UTILS";


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

    public static String streamToString(InputStream is) throws IOException {
        String string = "";
        if (is != null) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
            } finally {
                is.close();
            }
            string = stringBuilder.toString();
        }
        return string;
    }
}
