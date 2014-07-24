package org.almiso.collageapp.android.screens;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.activity.StartActivity;
import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.fragments.FragmentAuthorize;
import org.almiso.collageapp.android.fragments.FragmentImageGrid;
import org.almiso.collageapp.android.fragments.FragmentInstaCollage;
import org.almiso.collageapp.android.fragments.FragmentLaunch;
import org.almiso.collageapp.android.fragments.FragmentMain;
import org.almiso.collageapp.android.fragments.FragmentPreviewPhoto;
import org.almiso.collageapp.android.fragments.FragmentSearchUserByNick;
import org.almiso.collageapp.android.fragments.FragmentSettings;
import org.almiso.collageapp.android.fragments.FragmentUserList;
import org.almiso.collageapp.android.fragments.FragmentUserProfile;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by almiso on 07.06.2014.
 */
public class FragmentScreenController implements RootController {

    protected static final String TAG = "FragmentScreenController";

    private ActionBarActivity activity;
    private CollageApplication application;


    private ArrayList<CollageFragment> backStack = new ArrayList<CollageFragment>();

    public FragmentScreenController(ActionBarActivity activity, CollageApplication application, Bundle savedState) {
        this.activity = activity;
        this.application = application;

        if (savedState != null && savedState.containsKey("backstack")) {
            String[] keys = savedState.getStringArray("backstack");
            for (int i = 0; i < keys.length; i++) {
                backStack.add((CollageFragment) activity.getSupportFragmentManager().findFragmentByTag(keys[i]));
            }
        }
    }


    protected void openScreen(CollageFragment fragment) {
        FragmentTransaction transaction = prepareTransaction();
        if (backStack.size() > 0) {
            CollageFragment backFragment = backStack.get(backStack.size() - 1);
            if (((Object) fragment).getClass() != ((Object) backFragment).getClass()) {
                if (backFragment.isSaveInStack()) {
                    transaction.detach(backFragment);
                } else {
                    transaction.remove(backFragment);
                    backStack.remove(backStack.size() - 1);
                }

                transaction.replace(R.id.place_of_fragments, fragment, "backstack#" + backStack.size());
                backStack.add(fragment);
            }
        } else {
            transaction.replace(R.id.place_of_fragments, fragment, "backstack#" + backStack.size());
            backStack.add(fragment);
        }
        transaction.commit();
    }

    private FragmentTransaction prepareTransaction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            return activity.getSupportFragmentManager().beginTransaction();
        } else {
            return activity.getSupportFragmentManager().beginTransaction();
        }
    }

    private FragmentTransaction prepareBackTransaction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.getSupportFragmentManager().beginTransaction();
        } else {
            return activity.getSupportFragmentManager().beginTransaction();
        }
    }

    public boolean doSystemBack() {
        if (backStack.size() > 1) {
            CollageFragment currentFragment = backStack.get(backStack.size() - 1);
            if (!currentFragment.onBackPressed()) {
                CollageFragment prevFragment = backStack.get(backStack.size() - 2);
                backStack.remove(backStack.size() - 1);
                activity.getSupportFragmentManager().beginTransaction().remove(currentFragment).attach(prevFragment)
                        .commit();
                return true;
            } else {
                if (backStack.size() > 2) {
                    CollageFragment prevFragmentOne = backStack.get(backStack.size() - 2);
                    CollageFragment prevFragmentTwo = backStack.get(backStack.size() - 3);
                    backStack.remove(backStack.size() - 2);
                    activity.getSupportFragmentManager().beginTransaction().remove(currentFragment)
                            .remove(prevFragmentOne).attach(prevFragmentTwo).commit();
                    return true;
                } else {
                    return false;
                }
            }

        } else {
            return false;
        }
    }

    public Bundle saveState() {
        Bundle bundle = new Bundle();
        String[] keys = new String[backStack.size()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = backStack.get(i).getTag();
        }
        bundle.putStringArray("backstack", keys);
        return bundle;
    }

    @Override
    public void clearBackStack() {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        for (CollageFragment fragment : backStack) {
            transaction.remove(fragment);
        }
        transaction.commit();
        backStack.clear();
    }


    @Override
    public void popFragment(int count) {
        for (int i = 0; i < count - 1; i++) {
            backStack.remove(backStack.size() - 2);
        }

        if (backStack.size() > 1) {
            CollageFragment currentFragment = backStack.get(backStack.size() - 1);
            CollageFragment prevFragment = backStack.get(backStack.size() - 2);
            prevFragment.setHasOptionsMenu(true);
            backStack.remove(backStack.size() - 1);
            prepareBackTransaction().remove(currentFragment).attach(prevFragment).commit();
        }
    }


    /**
     * ***************************************************************************
     */
    @Override
    public void openApp() {
        openFragmentMain();
    }

    @Override
    public void onLogout() {
        application.getKernel().logOut();
        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction();
        for (CollageFragment fragment : backStack) {
            transaction.remove(fragment);
        }
        backStack.clear();
        ((StartActivity) activity).doInitApp(true);
    }

    @Override
    public void openFragmentLaunch() {
        openScreen(new FragmentLaunch());
    }

    @Override
    public void openFragmentAuthorize() {
        openScreen(new FragmentAuthorize());
    }


    @Override
    public void openFragmentMain() {
        openScreen(new FragmentMain());
    }

    @Override
    public void openFragmentSettings() {
        openScreen(new FragmentSettings());
    }

    @Override
    public void openFragmentSearchUserByNick() {
        openScreen(new FragmentSearchUserByNick());
    }

    @Override
    public void openFragmentSearch(int action, InstaUser user, boolean canOpenProf) {

        //TODO change back to normal. Now is testing
        Bundle args = new Bundle();
        args.putInt("action", action);
        args.putBoolean("canOpenProf", canOpenProf);
        args.putSerializable("user", user);
        FragmentImageGrid frag = new FragmentImageGrid();
        frag.setArguments(args);
        openScreen(frag);

//        openScreen(new FragmentImageGrid());

    }

    @Override
    public void openPreview(InstaSearchResult result, boolean canOpenProf) {
        Bundle args = new Bundle();
        args.putBoolean("canOpenProf", canOpenProf);
        args.putSerializable("result", result);
        FragmentPreviewPhoto frag = new FragmentPreviewPhoto();
        frag.setArguments(args);
        openScreen(frag);
    }


    @Override
    public void openCollagePreview(LinkedHashMap<Integer, InstaSearchResult> photos) {
        Bundle args = new Bundle();
        args.putSerializable("photos", photos);
        FragmentInstaCollage frag = new FragmentInstaCollage();
        frag.setArguments(args);
        openScreen(frag);
    }

    @Override
    public void openFragmentUserProfile(InstaUser user) {
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        FragmentUserProfile frag = new FragmentUserProfile();
        frag.setArguments(args);
        openScreen(frag);
    }

    @Override
    public void openFragmentFriendList(int action, InstaUser user) {
        Bundle args = new Bundle();
        args.putInt("action", action);
        args.putSerializable("user", user);
        FragmentUserList frag = new FragmentUserList();
        frag.setArguments(args);
        openScreen(frag);
    }
}
