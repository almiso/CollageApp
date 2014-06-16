package org.almiso.collageapp.android.kernel;

import android.os.SystemClock;

import org.almiso.collageapp.android.core.Account;
import org.almiso.collageapp.android.log.Logger;

/**
 * Created by almiso on 13.06.2014.
 */
public class AuthKernel {

    private static final String TAG = "AuthKernel";
    private ApplicationKernel kernel;
    private Account account;

    public AuthKernel(ApplicationKernel kernel) {
        this.kernel = kernel;
        if (account == null)
            account = new Account(kernel.getApplication());

        long start = SystemClock.uptimeMillis();

        Logger.d(TAG, "General loading in "
                + (SystemClock.uptimeMillis() - start) + " ms");
    }

    public synchronized Account getAccount() {
        return account;
    }

    public void logIn(long id, String username, String full_name, String profile_picture, String access_token, String request_token) {
        account.logIn(id, username, full_name, profile_picture, access_token, request_token);
    }

    public boolean isLoggedIn() {
        return account.isLoggedIn();
    }

    public void logOut() {
        account.logOut();
    }
}
