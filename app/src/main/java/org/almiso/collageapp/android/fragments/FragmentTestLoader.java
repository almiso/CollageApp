package org.almiso.collageapp.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.loader.ImageFetcherOld;
import org.almiso.collageapp.android.loader.Images;
import org.almiso.collageapp.android.log.Logger;

/**
 * Created by Alexandr Sosorev on 22.07.2014.
 */
public class FragmentTestLoader extends CollageFragment implements AdapterView.OnItemClickListener {

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcherOld mImageFetcher;


    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
//        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mImageFetcher.closeCache();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAdapter = new ImageAdapter(getActivity());

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        // The ImageFetcherFAWEF takes care of loading images into our ImageView
        // children asynchronously
        mImageFetcher = new ImageFetcherOld(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.ic_action_picture_dark);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), 0.25f);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_loader, container, false);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {


        final GridView mGridView = (GridView) view.findViewById(R.id.mediaGrid);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                } else {
//                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(mGridView.getWidth()
                            / (mImageThumbSize + mImageThumbSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                        mAdapter.setNumColumns(numColumns);
                        mAdapter.setItemHeight(columnWidth);
                        Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                        mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
                    GridView.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            if (getNumColumns() == 0) {
                return 0;
            }
            return Images.imageThumbUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return Images.imageThumbUrls[position];
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            Logger.d(TAG, "on getView");
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else {
                imageView = (ImageView) convertView;
            }
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            mImageFetcher.loadImage(Images.imageThumbUrls[position], imageView);
            return imageView;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, mItemHeight);
//            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
            notifyDataSetChanged();
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }


//    processBitmap
}
