package org.almiso.collageapp.android.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.core.ExceptionSourceListener;
import org.almiso.collageapp.android.core.InstaSearchSource;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.preview.InstaPreviewView;
import org.almiso.collageapp.android.preview.PreviewConfig;
import org.almiso.collageapp.android.ui.source.ViewSourceListener;
import org.almiso.collageapp.android.ui.source.ViewSourceState;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by almiso on 07.06.2014.
 */
public class FragmentPhotoGrid extends CollageFragment implements View.OnClickListener,
        ViewSourceListener, ExceptionSourceListener {

    public static final int ACTION_SEARCH_MY_PHOTOS = 1;
    public static final int ACTION_SEARCH_MY_LIKED_PHOTOS = 2;
    public static final int ACTION_SEARCH_USER_PHOTOS = 3;
    public static final int ACTION_SEARCH_FEED = 4;
    public static final int ACTION_SEARCH_NEAR = 5;

    /*
        Fields
     */
    private InstaUser user;
    private int ACTION;
    private boolean canOpenProf;
    private LinkedHashMap<Integer, InstaSearchResult> selectedPhotos;
    private ArrayList<InstaSearchResult> searchResults = new ArrayList<InstaSearchResult>();
    private InstaSearchSource instaSearchSource;

    /*
        Controls
     */
    private ImageView image_count;
    private Button buttonMakeCollage;
    private RelativeLayout imgBottom;
    private View progress;
    private TextView empty;
    private GridView gridView;
    private BaseAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        instaSearchSource.setListener(this);
        application.getDataSourceKernel().getExceptionSource().registerListener(this);
        onSourceStateChanged();
        onSourceDataChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        instaSearchSource.setListener(null);
        application.getDataSourceKernel().getExceptionSource().unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instaSearchSource.cancelQuery();
        application.getDataSourceKernel().removeInstaSearchSource(user.getId() + ACTION);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (selectedPhotos.size() > 0) {
            for (int i = 1; i < selectedPhotos.size() + 1; i++) {
                selectedPhotos.get(i).setChecked(false);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Object mUser = (Object) savedInstanceState.getSerializable("user");
            user = (InstaUser) mUser;
            ACTION = savedInstanceState.getInt("action");
            canOpenProf = savedInstanceState.getBoolean("canOpenProf");

            Object select = (Object) savedInstanceState.getSerializable("selectedPhotos");
            selectedPhotos = (LinkedHashMap<Integer, InstaSearchResult>) select;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
        outState.putSerializable("selectedPhotos", selectedPhotos);
        outState.putInt("action", ACTION);
        outState.putBoolean("canOpenProf", canOpenProf);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Object mUser = (Object) savedInstanceState.getSerializable("user");
            user = (InstaUser) mUser;
            ACTION = savedInstanceState.getInt("action");
            canOpenProf = savedInstanceState.getBoolean("canOpenProf");
            Object select = (Object) savedInstanceState.getSerializable("selectedPhotos");
            selectedPhotos = (LinkedHashMap<Integer, InstaSearchResult>) select;

            searchResults = (ArrayList<InstaSearchResult>) savedInstanceState.getSerializable("selectedPhotos");
        } else {
            selectedPhotos = new LinkedHashMap<Integer, InstaSearchResult>();
        }
        if (getArguments() != null) {
            user = (InstaUser) getArguments().getSerializable("user");
            ACTION = getArguments().getInt("action");
            canOpenProf = getArguments().getBoolean("canOpenProf");
        } else {
            user = null;
            ACTION = ACTION_SEARCH_MY_PHOTOS;
            canOpenProf = false;
        }
        View view = inflater.inflate(R.layout.fragment_photo_grid, null);
        if (user == null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    activity.onBackPressed();
                }
            });
        } else {
            setUpView(view);
            if (instaSearchSource.getViewSource() == null) {
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
        instaSearchSource = application.getDataSourceKernel().getInstaSearchSource(user.getId() + ACTION);
        Logger.d(TAG, "instaSearchSource is null = " + (instaSearchSource == null));
        Logger.d(TAG, "instaSearchSource.getViewSource() is null = " + (instaSearchSource.getViewSource() == null));
        image_count = (ImageView) view.findViewById(R.id.image_count);

        buttonMakeCollage = (Button) view.findViewById(R.id.buttonMakeCollage);
        buttonMakeCollage.setOnClickListener(this);


        gridView = (GridView) view.findViewById(R.id.mediaGrid);
        progress = view.findViewById(R.id.loading);
        empty = (TextView) view.findViewById(R.id.empty);
        goneView(empty, false);
        goneView(gridView, false);
        imgBottom = (RelativeLayout) view.findViewById(R.id.imgBottom);

        gridView.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);

        updateBottomLayout();

        gridView.setPadding(0, PreviewConfig.MEDIA_SPACING, 0, PreviewConfig.MEDIA_SPACING);
        gridView.setNumColumns(PreviewConfig.MEDIA_ROW_COUNT);
        gridView.setColumnWidth(PreviewConfig.MEDIA_PREVIEW);
        gridView.setVerticalSpacing(PreviewConfig.MEDIA_SPACING);
        gridView.setHorizontalSpacing(PreviewConfig.MEDIA_SPACING);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InstaSearchResult result = (InstaSearchResult) parent.getItemAtPosition(position);
                if (!result.isChecked()) {
                    if (selectedPhotos.size() < 4) {
                        result.setChecked(!result.isChecked());
                        selectedPhotos.put(selectedPhotos.size() + 1, result);
                    }
                } else {
                    if (selectedPhotos.size() > 0) {
                        result.setChecked(!result.isChecked());
                        selectedPhotos.remove(selectedPhotos.size());
                    }
                }
                updateBottomLayout();
                adapter.notifyDataSetChanged();
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final InstaSearchResult result = (InstaSearchResult) parent.getItemAtPosition(position);
                boolean canOpenProfile = ACTION == ACTION_SEARCH_FEED;
                getRootController().openPreview(result, canOpenProfile);
                return false;
            }
        });

        final Context context = getActivity();
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return searchResults.size() + 1;
            }

            @Override
            public InstaSearchResult getItem(int position) {
                return searchResults.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public int getItemViewType(int position) {
                if (position < searchResults.size()) {
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
                return position < searchResults.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (position < searchResults.size()) {
                    instaSearchSource.getViewSource().onItemsShown(position);

                    if (convertView == null) {
                        FrameLayout res = new FrameLayout(context);
                        GridView.LayoutParams params = new GridView.LayoutParams(PreviewConfig.MEDIA_PREVIEW,
                                PreviewConfig.MEDIA_PREVIEW);
                        res.setLayoutParams(params);

                        InstaPreviewView previewView = new InstaPreviewView(context);
                        previewView.setEmptyDrawable(new ColorDrawable(0xffdfe4ea));
                        previewView.setLayoutParams(new FrameLayout.LayoutParams(PreviewConfig.MEDIA_PREVIEW,
                                PreviewConfig.MEDIA_PREVIEW));
                        res.addView(previewView);

                        TextView size = new TextView(context);
                        size.setBackgroundColor(Color.TRANSPARENT);
                        size.setTextColor(Color.BLACK);
                        size.setTextSize(18);
                        FrameLayout.LayoutParams sizeParams = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                                Gravity.BOTTOM | Gravity.LEFT);
                        size.setLayoutParams(sizeParams);
                        size.setBackgroundColor(Color.WHITE);
                        size.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite, 0, 0, 0);
                        size.setPadding(getSp(4), 0, getSp(16), 0);
                        res.addView(size);

                        ImageView checkState = new ImageView(context);
                        checkState.setImageResource(R.drawable.ic_action_new);
                        FrameLayout.LayoutParams checkSizeParams = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP
                                | Gravity.RIGHT
                        );
                        checkState.setBackgroundColor(Color.GREEN);
                        checkState.setLayoutParams(checkSizeParams);
                        res.addView(checkState);

                        convertView = res;
                    }

                    InstaSearchResult searchResult = getItem(position);

                    InstaPreviewView previewView = (InstaPreviewView) ((ViewGroup) convertView).getChildAt(0);
                    previewView.requestSearch(searchResult);

                    TextView size = (TextView) ((ViewGroup) convertView).getChildAt(1);
                    size.setText(searchResult.getLikesCount());

                    ImageView checkState = (ImageView) ((ViewGroup) convertView).getChildAt(2);
                    if (searchResult.isChecked()) {
                        showView(checkState);
                    } else {
                        hideView(checkState, false);
                    }
                    return convertView;
                } else {
                    ProgressBar res = new ProgressBar(context);
                    GridView.LayoutParams params = new GridView.LayoutParams(PreviewConfig.MEDIA_PREVIEW,
                            PreviewConfig.MEDIA_PREVIEW);
                    res.setLayoutParams(params);

                    res.setVisibility(View.INVISIBLE);
                    if (instaSearchSource.getViewSource() != null) {
                        if (instaSearchSource.getViewSource().getState() == ViewSourceState.IN_PROGRESS) {
                            res.setVisibility(View.VISIBLE);
                        }
                    }
                    return res;
                }
            }
        };
        gridView.setAdapter(adapter);
    }

    private void doSearch(long userId) {
        instaSearchSource.newQuery(ACTION, userId);
        onSourceDataChanged();
        onSourceStateChanged();
    }


    @Override
    public void onSourceStateChanged() {
        if (instaSearchSource.getViewSource() != null) {
            if (instaSearchSource.getViewSource().getItemsCount() == 0) {
                if (instaSearchSource.getViewSource().getState() == ViewSourceState.IN_PROGRESS) {
                    goneView(gridView);
                    goneView(empty, false);
                    showView(progress);
                } else {
                    showView(empty);
                    goneView(gridView);
                    goneView(progress);
                }
            } else {
                goneView(empty);
                showView(gridView);
                goneView(progress);
            }
        } else {
            goneView(gridView);
            goneView(progress);
            showView(empty);
        }

    }

    @Override
    public void onSourceDataChanged() {
        if (instaSearchSource.getViewSource() != null) {
            searchResults = instaSearchSource.getViewSource().getCurrentWorkingSet();
        } else {
            searchResults.clear();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onException(String error) {
        instaSearchSource.cancelQuery();
        empty.setText(error);
        showView(empty);
        goneView(gridView);
        goneView(progress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMakeCollage:
                if (selectedPhotos.size() == 4) {
                    getRootController().openCollagePreview(selectedPhotos);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(application, application.getString(R.string.st_not_enough_photos), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.avatarTouchLayer:
                if (user != null && canOpenProf && (user.getId() != application.getMyId())) {
                    getRootController().openFragmentUserProfile(user);
                }
                break;
        }
    }

    private void updateBottomLayout() {
        if (selectedPhotos.size() == 0) {
            buttonMakeCollage.setVisibility(View.GONE);
            image_count.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_1));
            imgBottom.setVisibility(View.VISIBLE);
        } else if (selectedPhotos.size() == 1) {
            buttonMakeCollage.setVisibility(View.GONE);
            image_count.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_2));
            imgBottom.setVisibility(View.VISIBLE);
        } else if (selectedPhotos.size() == 2) {
            buttonMakeCollage.setVisibility(View.GONE);
            image_count.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_3));
            imgBottom.setVisibility(View.VISIBLE);
        } else if (selectedPhotos.size() == 3) {
            buttonMakeCollage.setVisibility(View.GONE);
            image_count.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_4));
            imgBottom.setVisibility(View.VISIBLE);
        } else if (selectedPhotos.size() == 4) {
            buttonMakeCollage.setVisibility(View.VISIBLE);
            imgBottom.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_search, menu);

        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
        if (user != null) {
            getSherlockActivity().getSupportActionBar().setTitle(user.getDisplayName().toUpperCase());
            MenuItem avatarItem = menu.findItem(R.id.userAvatar);

            InstaPreviewView imageView = (InstaPreviewView) avatarItem.getActionView().findViewById(R.id.image);
            imageView.setEmptyDrawable(R.drawable.ic_action_person);
            imageView.requestUserAvatar(user);

            View touchLayer = avatarItem.getActionView().findViewById(R.id.avatarTouchLayer);
            touchLayer.setOnClickListener(this);
        } else {
            getSherlockActivity().getSupportActionBar().setTitle(R.string.st_user_name_default);
        }


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
}
