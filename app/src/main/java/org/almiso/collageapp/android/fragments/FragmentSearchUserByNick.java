package org.almiso.collageapp.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.tasks.AsyncAction;
import org.almiso.collageapp.android.tasks.AsyncException;
import org.almiso.collageapp.android.tasks.CollageException;


/**
 * Created by almiso on 07.06.2014.
 */
public class FragmentSearchUserByNick extends CollageFragment implements View.OnClickListener {

    protected static String TAG = "FragmentSearchUserByNick";
    private EditText edNickname;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serch_user_by_nick, null);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        view.findViewById(R.id.buttonSearchPhotos).setOnClickListener(this);
        edNickname = (EditText) view.findViewById(R.id.editTextNick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSearchPhotos:
                String nickname = edNickname.getText().toString();
                if (nickname.equals("")) {
                    Toast.makeText(application, R.string.st_null_query, Toast.LENGTH_SHORT).show();
                } else {
                    doSearch(nickname);
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getSherlockActivity().getSupportActionBar().setTitle(R.string.st_search_user);
        getSherlockActivity().getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    ******************************************************
    *  Network functions
    * ***************************************************
    */
    private void doSearch(final String query) {

        runUiTask(new AsyncAction() {
            private InstaUser user;

            @Override
            public void execute() throws AsyncException {
                try {
                    user = application.getApi().getUser(query);
                } catch (CollageException ex) {
                    if (ex.getErrorCode() == 2) {
                        throw new AsyncException(
                                AsyncException.ExceptionType.NO_USER_FOUND, false);
                    } else if (ex.getErrorCode() == 3) {
                        throw new AsyncException(
                                AsyncException.ExceptionType.LOAD_ERROR, true);
                    } else {
                        throw new AsyncException(
                                AsyncException.ExceptionType.CONNECTION_ERROR, true);
                    }
                }
            }

            @Override
            public void afterExecute() {
                Logger.d(TAG, "user = " + user.toString());
                if (user == null || user.getId() == 0 || user.getId() == -1) {
                    try {
                        throw new AsyncException(
                                AsyncException.ExceptionType.LOAD_ERROR, false);
                    } catch (AsyncException e) {
                        e.printStackTrace();
                    }
                } else {
                    getRootController().openFragmentSearch(FragmentPhotoGrid.ACTION_SEARCH_USER_PHOTOS, user);
                }

            }
        });
    }
}
