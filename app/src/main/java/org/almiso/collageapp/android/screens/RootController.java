package org.almiso.collageapp.android.screens;

import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.core.model.InstaUser;

import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by almiso on 07.06.2014.
 */
public interface RootController {

    public void openFragmentLaunch();

    public void openFragmentAuthorize();

    public void openFragmentMain();

    public void openFragmentSettings();

    public void openFragmentSearchUserByNick();

    public void openFragmentSearch(int action, InstaUser user);

    public void openCollagePreview(LinkedHashMap<Integer, InstaSearchResult> photos);

	/* system */

    public void openApp();

    public void onLogout();

    public void clearBackStack();

    public void popFragment(int count);

    void openPreview(InstaSearchResult result);
}
