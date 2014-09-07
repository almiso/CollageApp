package org.almiso.collageapp.android.fragments;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.activity.ActivityPhotoPreview;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.media.util.Constants;
import org.almiso.collageapp.android.media.util.ImageFetcher;
import org.almiso.collageapp.android.media.util.ImageReceiver;
import org.almiso.collageapp.android.media.util.ImageWorker;
import org.almiso.collageapp.android.media.util.VersionUtils;
import org.almiso.collageapp.android.network.tasks.AsyncAction;
import org.almiso.collageapp.android.network.tasks.AsyncException;
import org.almiso.collageapp.android.network.tasks.RecoverCallback;

import java.util.Random;

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
            case R.id.item_save:
                savePhotoToGallery();
                return true;
            case R.id.item_retweet:
                retweetPhoto();
                return true;
            case R.id.item_share:
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sharePhoto() {
        runUiTask(new AsyncAction() {
            private int tmpPosition = 20000;

            @Override
            public void execute() throws AsyncException {
                if (bitmapToSave != null) {
                    application.getDataSourceKernel().saveTempPhoto(bitmapToSave, tmpPosition);
                } else {
                    throw new AsyncException(
                            AsyncException.ExceptionType.CUSTOM_ERROR, getResources().getString(R.string.st_error_photo_loading), true);
                }
            }

            @Override
            public void afterExecute() {
                if (application.getDataSourceKernel().getTempPhoto(tmpPosition) == null) {
                    Toast.makeText(application, R.string.st_error_on_saving, Toast.LENGTH_SHORT).show();
                    return;
                }

                final Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, application.getDataSourceKernel().getTempPhoto(tmpPosition));
                shareIntent.setType("image/*");
                Intent chooser = Intent.createChooser(shareIntent, getResources().getString(R.string.st_share_using));
                if (chooser.resolveActivity(activity.getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });
    }


    private void savePhotoToGallery() {
        runUiTask(new AsyncAction() {

            @Override
            public void execute() throws AsyncException {
                if (bitmapToSave != null) {
                    application.getDataSourceKernel().saveToGallery(bitmapToSave);
                } else {
                    throw new AsyncException(
                            AsyncException.ExceptionType.CONNECTION_ERROR, true);
                }
            }


            @Override
            public void afterExecute() {
                Toast.makeText(application, R.string.st_photo_saved, Toast.LENGTH_SHORT).show();
            }
        }, new RecoverCallback() {
            @Override
            public void onError(AsyncException e, Runnable onRepeat, Runnable onCancel) {
                Toast.makeText(application, R.string.st_error_photo_loading, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isInstagramInstalled() {
        boolean isInstalled = false;

        try {
            ApplicationInfo info = application.getPackageManager().getApplicationInfo(Constants.INSTAGRAM_PACKAGE, 0);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled = false;
        }

        return isInstalled;
    }

    private void retweetPhoto() {
        runUiTask(new AsyncAction() {
//            private Bitmap bitmap;

            Random rnd = new Random();
            private int tmpPosition = rnd.nextInt();

            @Override
            public void execute() throws AsyncException {

                if (!isInstagramInstalled()) {
                    throw new AsyncException(
                            AsyncException.ExceptionType.CUSTOM_ERROR, getResources().getString(R.string.st_no_instagram), false);
                }

                if (bitmapToSave != null) {
                    application.getDataSourceKernel().saveTempPhoto(getRetweetBitmap(bitmapToSave), tmpPosition);
                } else {
                    throw new AsyncException(
                            AsyncException.ExceptionType.CUSTOM_ERROR, getResources().getString(R.string.st_error_photo_loading), true);
                }
            }


            @Override
            public void afterExecute() {
                if (application.getDataSourceKernel().getTempPhoto(tmpPosition) == null) {
                    Toast.makeText(application, R.string.st_error_on_saving, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, application.getDataSourceKernel().getTempPhoto(tmpPosition));
                String retweetRetweet = getString(R.string.st_repost_subtext);
                shareIntent.putExtra(Intent.EXTRA_TEXT, retweetRetweet);
                shareIntent.setType("image/*");
                shareIntent.setPackage(Constants.INSTAGRAM_PACKAGE);
                startActivity(shareIntent);
            }
        });
    }

    private Bitmap getRetweetBitmap(Bitmap inBitmap) {


        Bitmap bitmap = Bitmap.createBitmap(inBitmap);
        String text = getString(R.string.st_repost_text);
        try {
            Canvas canvas = new Canvas(bitmap);

            Paint paintRect = new Paint();
            paintRect.setColor(Color.parseColor("#b3696969"));


            RectF rect = new RectF(0, bitmap.getHeight() - getSp(32), bitmap.getWidth(), bitmap.getHeight());
            canvas.drawRect(rect, paintRect);

            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(getSp(18));
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.WHITE);
            textPaint.setStrokeWidth(2.0f);
            textPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
            canvas.drawText(text, rect.left + getSp(4), rect.bottom - getSp(9), textPaint);


            return bitmap;
        } catch (Exception e) {
            Logger.e(TAG, "Error on getRetweetBitmap()", e);
            return null;
        }
    }


}
