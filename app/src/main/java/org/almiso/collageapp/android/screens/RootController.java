package org.almiso.collageapp.android.screens;

import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.core.model.InstaUser;

import java.util.ArrayList;
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

    public void openFragmentImageGrid(int action, InstaUser user, boolean canOpenProf);

    public void openFragmentFriendList(int action, InstaUser user);

    public void openCollagePreview(LinkedHashMap<Integer, InstaSearchResult> photos);

    public void openFragmentCollage(ArrayList<InstaSearchResult> photos);

    public void openFragmentUserProfile(InstaUser user);

	/* system */

    public void openApp();

    public void onLogout();

    public void clearBackStack();

    public void popFragment(int count);

}
