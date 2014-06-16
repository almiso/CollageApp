package org.almiso.collageapp.android.tasks;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by almiso on 07.06.2014.
 */
public class CollageException extends IOException {
    private static final Pattern REGEXP_PATTERN = Pattern.compile("[A-Z_0-9]+");

    private int errorCode;
    private String errorTag;

    private static String getErrorTag(String srcMessage) {
        if (srcMessage == null) {
            return "UNKNOWN";
        }
        Matcher matcher = REGEXP_PATTERN.matcher(srcMessage);
        if (matcher.find()) {
            return matcher.group();
        }
        return "UNKNOWN";
    }

    private static String getErrorMessage(String srcMessage) {
        if (srcMessage == null) {
            return "Unknown error";
        }
        int index = srcMessage.indexOf(":");
        if (index > 0) {
            return srcMessage.substring(index);
        } else {
            return srcMessage;
        }
    }

    public CollageException(int errorCode, String message) {
        super(getErrorMessage(message));
        this.errorCode = errorCode;
        this.errorTag = getErrorTag(message);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorTag() {
        return errorTag;
    }
}
