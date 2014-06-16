package org.almiso.collageapp.android.tasks;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageApplication;

/**
 * Created by almiso on 07.06.2014.
 */
public class AsyncException extends Exception {

    public static enum ExceptionType {
        CONNECTION_ERROR, UNKNOWN_ERROR, NO_USER_FOUND, LOAD_ERROR
    }

    private static CollageApplication application;
    private ExceptionType type;
    private boolean repeatable;

    public AsyncException(CollageException ex) {
        this(getErrorMessage(ex));
        this.repeatable = true;
        if (ex.getErrorCode() == 0) {
            this.type = ExceptionType.CONNECTION_ERROR;
        } else {
            if (getMessage() == null) {
                this.type = ExceptionType.UNKNOWN_ERROR;
            }
        }
    }

    public static void initLocalisation(CollageApplication application) {
        AsyncException.application = application;
    }

    public AsyncException(String message) {
        super(message);
        this.repeatable = true;
    }

    public AsyncException(String message, Throwable t) {
        super(message, t);
        this.repeatable = true;
    }

    public AsyncException(String message, boolean repeatable) {
        super(message);
        this.repeatable = repeatable;
    }

    public AsyncException(String message, Throwable t, boolean repeatable) {
        super(message, t);
        this.repeatable = repeatable;
    }

    public AsyncException(ExceptionType type) {
        this.type = type;
        this.repeatable = true;
    }

    public AsyncException(ExceptionType type, Throwable t) {
        super(t);
        this.type = type;
        this.repeatable = true;
    }

    public AsyncException(ExceptionType type, boolean repeatable) {
        this.type = type;
        this.repeatable = repeatable;
    }

    public AsyncException(ExceptionType type, Throwable t, boolean repeatable) {
        super(t);
        this.type = type;
        this.repeatable = repeatable;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public ExceptionType getType() {
        return type;
    }

    private static String getErrorMessage(CollageException ex) {
        if (application != null) {
            return application.getString(R.string.st_error_title);
        } else {
            return "Error";
        }
    }

}
