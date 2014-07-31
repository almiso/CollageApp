package org.almiso.collageapp.android.util;

import org.almiso.collageapp.android.log.Logger;

/**
 * Created by Alexandr Sosorev on 31.07.2014.
 */
public class TextUtils {

    public static String getMediaCount(long inputValue) {
        if (inputValue < 10000) {
            return String.valueOf(inputValue);
        } else if (inputValue < 1000000) {
            long tmp = inputValue / 1000L;
            Logger.d("TextUtils", "tmp = " + String.valueOf(tmp));
            return String.valueOf(tmp) + "k";
        } else {
            long tmp = inputValue / 1000000L;
            Logger.d("TextUtils", "tmp = " + String.valueOf(tmp));
            return String.valueOf(tmp) + "M";
        }

    }
}
