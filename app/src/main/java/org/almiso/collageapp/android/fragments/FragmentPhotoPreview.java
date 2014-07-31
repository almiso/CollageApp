package org.almiso.collageapp.android.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.activity.ActivityPhotoPreview;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.media.util.ImageFetcher;
import org.almiso.collageapp.android.media.util.ImageReceiver;
import org.almiso.collageapp.android.media.util.ImageWorker;
import org.almiso.collageapp.android.media.util.VersionUtils;

/**
 * Created by Alexandr Sosorev on 24.07.2014.
 */
public class FragmentPhotoPreview extends CollageFragment {

    protected static String TAG = "FragmentPhotoPreview";

    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String IMAGE_FRAG_POS = "extra_fragment_position";
    private String mImageUrl;
    private int position;
    private ImageView mImageView;

    private Bitmap bitmapToSave = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }

    public static FragmentPhotoPreview newInstance(String imageUrl, int position) {
        FragmentPhotoPreview f = new FragmentPhotoPreview();
        Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putInt(IMAGE_FRAG_POS, position);
        f.setArguments(args);
        return f;
    }

    public FragmentPhotoPreview() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        position = getArguments() != null ? getArguments().getInt(IMAGE_FRAG_POS) : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_preview, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        getActivity().invalidateOptionsMenu();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (ActivityPhotoPreview.class.isInstance(getActivity())) {
            ImageFetcher mImageFetcher = ((ActivityPhotoPreview) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(mImageUrl, mImageView, new ImageReceiver() {
                @Override
                public void onImageReceived(Bitmap bitmap) {
                    bitmapToSave = bitmap;
                    application.getDataSourceKernel().saveTempPhoto(bitmap, position);

                }
            });
        }
        if (View.OnClickListener.class.isInstance(getActivity()) && VersionUtils.hasHoneycomb()) {
            mImageView.setOnClickListener((View.OnClickListener) getActivity());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.ic_save:
                if (bitmapToSave != null) {
                    application.getDataSourceKernel().saveToGallery(bitmapToSave);
                    Toast.makeText(application, R.string.st_photo_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(application, R.string.st_error_photo_loading, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
