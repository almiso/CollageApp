package org.almiso.collageapp.android.fragments;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.almiso.collageapp.android.BuildConfig;
import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.activity.ActivityPhotoPreview;
import org.almiso.collageapp.android.base.CollageImageFragment;
import org.almiso.collageapp.android.core.ExceptionSourceListener;
import org.almiso.collageapp.android.core.InstaSearchSource;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.media.util.RecyclingImageView;
import org.almiso.collageapp.android.media.util.VersionUtils;
import org.almiso.collageapp.android.preview.PreviewConfig;
import org.almiso.collageapp.android.ui.source.ViewSourceListener;
import org.almiso.collageapp.android.ui.source.ViewSourceState;

import java.util.ArrayList;

/**
 * Created by Alexandr Sosorev on 24.07.2014.
 */
public class FragmentImageGrid extends CollageImageFragment implements AdapterView.OnItemClickListener, ViewSourceListener, ExceptionSourceListener, View.OnClickListener {
    public static final int ACTION_SEARCH_MY_PHOTOS = 1;
    public static final int ACTION_SEARCH_USER_PHOTOS = 3;
    public static final int ACTION_SEARCH_FEED = 4;
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;

    /*
     * Fields
     */
    private InstaUser user;
    private int ACTION;
    private boolean canOpenProf;
    private ArrayList<InstaSearchResult> searchResults = new ArrayList<InstaSearchResult>();
    private InstaSearchSource instaSearchSource;

    private boolean isInEditMode = false;
    private boolean isShowLikesCount = false;

    private ArrayList<InstaSearchResult> mSelectedPhotos;

    /*
     *    UI Controls
     */
    private View progress;
    private TextView empty;
    private GridView gridView;
    private FrameLayout mBottom;
    private TextView tvEmptySelectedPhotos;
    private ViewGroup mContainerView;
    private Button buttonMakeCollage;

    private long start;

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();

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
        application.getDataSourceKernel().removeInstaSearchSource(user.getId() + ACTION);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (selectedPhotos.size() > 0) {
//            for (int i = 1; i < selectedPhotos.size() + 1; i++) {
//                selectedPhotos.get(i).setChecked(false);
//            }
//        }
    }

    public FragmentImageGrid() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
        outState.putSerializable("mSelectedPhotos", mSelectedPhotos);
        outState.putInt("action", ACTION);
        outState.putBoolean("canOpenProf", canOpenProf);
        outState.putBoolean("isInEditMode", isInEditMode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            user = (InstaUser) savedInstanceState.getSerializable("user");
            ACTION = savedInstanceState.getInt("action");
            canOpenProf = savedInstanceState.getBoolean("canOpenProf");
            mSelectedPhotos = (ArrayList<InstaSearchResult>) savedInstanceState.getSerializable("mSelectedPhotos");
            isInEditMode = savedInstanceState.getBoolean("isInEditMode");
        }


        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());
        mImageFetcher.setImageSize(mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.ic_action_picture_dark);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        start = SystemClock.uptimeMillis();
        if (savedInstanceState != null) {
            user = (InstaUser) savedInstanceState.getSerializable("user");
            ACTION = savedInstanceState.getInt("action");
            mSelectedPhotos = (ArrayList<InstaSearchResult>) savedInstanceState.getSerializable("mSelectedPhotos");
            isInEditMode = savedInstanceState.getBoolean("isInEditMode");
        } else {
            mSelectedPhotos = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_test_loader, container, false);
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
        mSelectedPhotos = new ArrayList<>();
        instaSearchSource = application.getDataSourceKernel().getInstaSearchSource(user.getId() + ACTION);

        mBottom = (FrameLayout) view.findViewById(R.id.mBottom);
        tvEmptySelectedPhotos = (TextView) view.findViewById(android.R.id.empty);
        mContainerView = (ViewGroup) view.findViewById(R.id.container);
        buttonMakeCollage = (Button) view.findViewById(R.id.buttonMakeCollage);
        buttonMakeCollage.setOnClickListener(this);

        gridView = (GridView) view.findViewById(R.id.gridView);
        final GridView mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    if (!VersionUtils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                if (mAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(mGridView.getWidth()
                            / (mImageThumbSize + mImageThumbSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                        mAdapter.setNumColumns(numColumns);
                        mAdapter.setItemHeight(columnWidth);
                        if (BuildConfig.DEBUG) {
                            Logger.d(TAG, "onCreateView - numColumns set to " + numColumns);
                        }
                        mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });

        //Set up first time views visibility
        progress = view.findViewById(R.id.loading);
        empty = (TextView) view.findViewById(R.id.empty);
        goneView(empty, false);
        goneView(gridView, false);
        updateBottom();

    }


    private class ImageAdapter extends BaseAdapter {

        private final Context context;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            this.context = context;

            mImageViewLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
                    GridView.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items
            if (getNumColumns() == 0) {
                return 0;
            }
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
        public View getView(int position, View convertView, ViewGroup container) {
            if (position < searchResults.size()) {
                instaSearchSource.getViewSource().onItemsShown(position);
                ImageView imageView;

                if (convertView == null) {
                    FrameLayout res = new FrameLayout(context);
                    GridView.LayoutParams params = new GridView.LayoutParams(PreviewConfig.MEDIA_PREVIEW,
                            PreviewConfig.MEDIA_PREVIEW);
                    res.setLayoutParams(params);


                    //Photo
                    imageView = new RecyclingImageView(context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setLayoutParams(mImageViewLayoutParams);
                    imageView.setFocusable(false);
                    res.addView(imageView);

                    //Text of likes count
                    TextView tvSize = new TextView(context);
                    tvSize.setBackgroundColor(Color.TRANSPARENT);
                    tvSize.setTextColor(Color.BLACK);
                    tvSize.setTextSize(18);
                    FrameLayout.LayoutParams sizeParams = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.BOTTOM | Gravity.LEFT);
                    tvSize.setLayoutParams(sizeParams);
                    tvSize.setBackgroundColor(Color.WHITE);
                    tvSize.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_favorite, 0, 0, 0);
                    tvSize.setPadding(getSp(4), 0, getSp(16), 0);
                    tvSize.setFocusable(false);
                    res.addView(tvSize);

                    convertView = res;
                }

                InstaSearchResult searchResult = getItem(position);
                mImageFetcher.loadImage(searchResult.getThumbnailUrl(), (ImageView) ((ViewGroup) convertView).getChildAt(0));

                TextView tvSize = (TextView) ((ViewGroup) convertView).getChildAt(1);
                tvSize.setText(searchResult.getLikesCount());

                if (isShowLikesCount) {
                    showView(tvSize);
                } else {
                    goneView(tvSize);
                }

                return convertView;
            } else {
                ProgressBar progressBar = new ProgressBar(context);
                GridView.LayoutParams params = new GridView.LayoutParams(PreviewConfig.MEDIA_PREVIEW,
                        PreviewConfig.MEDIA_PREVIEW);
                progressBar.setLayoutParams(params);
                progressBar.setFocusable(false);

                progressBar.setVisibility(View.INVISIBLE);
                if (instaSearchSource.getViewSource() != null) {
                    if (instaSearchSource.getViewSource().getState() == ViewSourceState.IN_PROGRESS) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
                return progressBar;
            }
        }

        /**
         * Sets the item height. Useful for when we know the column width so the
         * height can be set to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }

    private void doSearch(long userId) {
        instaSearchSource.newQuery(ACTION, userId);
        onSourceDataChanged();
        onSourceStateChanged();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatarTouchLayer:
                if (user != null && canOpenProf && (user.getId() != application.getMyId())) {
                    getRootController().openFragmentUserProfile(user);
                }
                break;
            case R.id.buttonMakeCollage:
                getRootController().openFragmentCollage(mSelectedPhotos);
                break;
        }
    }


    private void updateBottom() {
        if (isInEditMode) {
            showView(mBottom);
            if (mSelectedPhotos.isEmpty() || mSelectedPhotos.size() == 0) {
                showView(tvEmptySelectedPhotos);
                buttonMakeCollage.setEnabled(false);
            } else {
                hideView(tvEmptySelectedPhotos);
                buttonMakeCollage.setEnabled(true);
            }
        } else {
            goneView(mBottom);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!isInEditMode) {
            // Just open photo preview
            Intent i = new Intent(getActivity(), ActivityPhotoPreview.class);
            i.putExtra(ActivityPhotoPreview.EXTRA_IMAGE, position);
            i.putExtra(ActivityPhotoPreview.EXTRA_USER, user);
            i.putExtra(ActivityPhotoPreview.EXTRA_ACTION, ACTION);
            if (VersionUtils.hasJellyBean()) {
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
                getActivity().startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }
        } else {
            // Select photo and add it to list
            final InstaSearchResult result = (InstaSearchResult) adapterView.getItemAtPosition(position);
            mSelectedPhotos.add(result);
            addItem(result);
            updateBottom();
        }
    }

    private void addItem(final InstaSearchResult result) {
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(application).inflate(R.layout.item_img_selected,
                mContainerView, false);

        ImageView img = (ImageView) newView.findViewById(R.id.imgSelected);
        mImageFetcher.loadImage(result.getThumbnailUrl(), img);

        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContainerView.removeView(newView);
                mSelectedPhotos.remove(result);
                updateBottom();
            }
        });

        mContainerView.addView(newView, 0);

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
            if ((SystemClock.uptimeMillis() - start) < 200) {
                goneView(gridView);
                goneView(empty, false);
                showView(progress);
            } else {
                goneView(gridView);
                goneView(progress);
                showView(empty);
            }
        }
    }

    @Override
    public void onSourceDataChanged() {
        if (instaSearchSource.getViewSource() != null) {
            searchResults = instaSearchSource.getViewSource().getCurrentWorkingSet();
        } else {
            searchResults.clear();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_grid, menu);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setSubtitle(null);

        if (user != null) {
            activity.getSupportActionBar().setTitle(user.getDisplayName().toUpperCase());
            MenuItem avatarItem = menu.findItem(R.id.userAvatar);

            RecyclingImageView imageView = (RecyclingImageView) avatarItem.getActionView().findViewById(R.id.image);
            mImageFetcher.loadImage(user.getProfile_picture_url(), imageView);

            View touchLayer = avatarItem.getActionView().findViewById(R.id.avatarTouchLayer);
            touchLayer.setOnClickListener(this);
        } else {
            activity.getSupportActionBar().setTitle(R.string.st_user_name_default);
        }

        MenuItem mEditMode = menu.findItem(R.id.editMode);
        String titleEdit = isShowLikesCount ? getResources().getString(R.string.st_close_edit_mode)
                : getResources().getString(R.string.st_open_edit_mode);
        mEditMode.setTitle(titleEdit);

        MenuItem mLikesCount = menu.findItem(R.id.likesCount);
        String titleLikes = isShowLikesCount ? getResources().getString(R.string.st_hide_likes_count)
                : getResources().getString(R.string.st_show_likes_count);
        mLikesCount.setTitle(titleLikes);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;
            case R.id.editMode:
                isInEditMode = !isInEditMode;
                activity.invalidateOptionsMenu();
                updateBottom();
                return true;
            case R.id.likesCount:
                isShowLikesCount = !isShowLikesCount;
                activity.invalidateOptionsMenu();
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
