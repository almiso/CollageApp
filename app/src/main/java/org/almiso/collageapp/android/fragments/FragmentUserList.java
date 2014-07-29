package org.almiso.collageapp.android.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageImageFragment;
import org.almiso.collageapp.android.core.ExceptionSourceListener;
import org.almiso.collageapp.android.core.InstaUserSource;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.ui.source.ViewSourceListener;
import org.almiso.collageapp.android.ui.source.ViewSourceState;

import java.util.ArrayList;

/**
 * Created by almiso on 21.06.2014.
 */
public class FragmentUserList extends CollageImageFragment implements ViewSourceListener, ExceptionSourceListener {

    public static final int ACTION_FOLLOWS = 1;
    public static final int ACTION_FOLLOWED_BY = 2;

    private int ACTION;

    private ProgressBar progress;
    private ListView list;
    private LinearLayout layoutError;


    private ArrayList<InstaUser> friends = new ArrayList<InstaUser>();
    private BaseAdapter adapter;
    private InstaUserSource instaUserSource;

    private InstaUser user;

    private long start;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            user = (InstaUser) savedInstanceState.getSerializable("user");
            ACTION = savedInstanceState.getInt("action");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
        outState.putInt("action", ACTION);
    }

    @Override
    public void onResume() {
        super.onResume();
        instaUserSource.setListener(this);
        application.getDataSourceKernel().getExceptionSource().registerListener(this);
        onSourceStateChanged();
        onSourceDataChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        instaUserSource.setListener(null);
        application.getDataSourceKernel().getExceptionSource().unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instaUserSource.cancelSearch();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        start = SystemClock.uptimeMillis();
        if (savedInstanceState != null) {
            user = (InstaUser) savedInstanceState.getSerializable("user");
            ACTION = savedInstanceState.getInt("action");
        }
        if (getArguments() != null) {
            user = (InstaUser) getArguments().getSerializable("user");
            ACTION = getArguments().getInt("action");
        } else {
            user = null;
            ACTION = ACTION_FOLLOWS;
        }
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        if (user == null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    activity.onBackPressed();
                }
            });
        } else {
            setUpView(view);
            if (friends.size() < 1) {
                secureCallback(new Runnable() {
                    @Override
                    public void run() {
                        doSearch(user.getId());
                    }
                });
            }
        }
        return view;
    }


    private void setUpView(View view) {
        instaUserSource = application.getDataSourceKernel().getInstaUserSource(user.getId() + ACTION);
        list = (ListView) view.findViewById(R.id.friendList);
        layoutError = (LinearLayout) view.findViewById(R.id.layoutError);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        goneView(layoutError);
        goneView(progress);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InstaUser mUser = (InstaUser) parent.getItemAtPosition(position);
                if (mUser.getId() != application.getMyId()) {
                    getRootController().openFragmentUserProfile(mUser);
                }
            }
        });

        final Context context = getActivity();
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return friends.size() + 1;
            }

            @Override
            public InstaUser getItem(int position) {
                return friends.get(position);
            }

            @Override
            public int getItemViewType(int position) {
                if (position < friends.size()) {
                    return 0;
                } else {
                    return 1;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return position < friends.size();
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (position < friends.size()) {
                    instaUserSource.getViewSource().onItemsShown(position);
                    if (convertView == null) {

                        FrameLayout res = new FrameLayout(context);
                        AbsListView.LayoutParams params =
                                new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getPx(56));
                        res.setLayoutParams(params);

                        ImageView previewView = new ImageView(context);

                        FrameLayout.LayoutParams photoParams = new FrameLayout.LayoutParams(getPx(48), getPx(48));
                        photoParams.setMargins(getPx(4), getPx(4), getPx(4), getPx(4));
                        previewView.setLayoutParams(photoParams);
                        previewView.setPadding(getPx(4), getPx(4), getPx(4), getPx(4));
                        res.addView(previewView);

                        TextView size = new TextView(context);
                        size.setBackgroundColor(Color.TRANSPARENT);
                        size.setTextColor(Color.BLACK);
                        size.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                        size.setTextSize(22);

                        FrameLayout.LayoutParams sizeParams = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER | Gravity.LEFT);

                        size.setLayoutParams(sizeParams);
                        size.setPadding(getPx(60), getSp(4), getSp(4), getSp(4));
                        res.addView(size);

                        convertView = res;
                    }

                    InstaUser user = getItem(position);


                    ImageView previewView = (ImageView) ((ViewGroup) convertView).getChildAt(0);

                    mImageFetcher.setImageSize(100);
                    mImageFetcher.setShape(ImageShape.SHAPE_CIRCLE);
                    mImageFetcher.setImageFadeIn(false);
                    mImageFetcher.loadImage(user.getProfile_picture_url(), previewView);


                    TextView size = (TextView) ((ViewGroup) convertView).getChildAt(1);
                    size.setText(user.getDisplayName());

                    return convertView;
                } else {

                    ProgressBar res = new ProgressBar(context);
                    AbsListView.LayoutParams progressParams = new AbsListView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER);

                    res.setBackgroundColor(Color.TRANSPARENT);
                    res.setLayoutParams(progressParams);
                    res.setPadding(getPx(4), getPx(4), getPx(4), getPx(4));


                    res.setVisibility(View.INVISIBLE);
                    if (instaUserSource.getViewSource() != null) {
                        if (instaUserSource.getViewSource().getState() == ViewSourceState.IN_PROGRESS) {
                            res.setVisibility(View.VISIBLE);
                        }
                    }
                    return res;
                }

            }
        };
        list.setAdapter(adapter);

    }


    private void doSearch(long userId) {
        instaUserSource.newSearch(ACTION, userId);
        onSourceDataChanged();
        onSourceStateChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        if (ACTION == ACTION_FOLLOWED_BY) {
            activity.getSupportActionBar().setTitle(R.string.st_followed_by);
        } else {
            activity.getSupportActionBar().setTitle(R.string.st_follows);
        }
        activity.getSupportActionBar().setSubtitle(null);
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


    @Override
    public void onException(String error) {

    }

    @Override
    public void onSourceStateChanged() {
        if (instaUserSource.getViewSource() != null) {

            if (instaUserSource.getViewSource().getItemsCount() == 0) {
                if (instaUserSource.getViewSource().getState() == ViewSourceState.IN_PROGRESS) {
                    goneView(list);
                    goneView(layoutError);
                    showView(progress);
                } else {
                    showView(layoutError);
                    goneView(list);
                    goneView(progress);
                }
            } else {
                goneView(layoutError);
                showView(list);
                goneView(progress);
            }
        } else {
            if ((SystemClock.uptimeMillis() - start) < 200) {
                goneView(list);
                goneView(layoutError);
                showView(progress);
            } else {
                goneView(list);
                goneView(progress);
                showView(layoutError);
            }
        }


    }

    @Override
    public void onSourceDataChanged() {
        if (instaUserSource.getViewSource() != null) {
            friends = instaUserSource.getViewSource().getCurrentWorkingSet();
        } else {
            friends.clear();
        }
        adapter.notifyDataSetChanged();
    }
}
