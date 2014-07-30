package org.almiso.collageapp.android.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageImageFragment;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.dialogs.DialogFactory;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.media.util.ImageShape;
import org.almiso.collageapp.android.network.tasks.AsyncAction;
import org.almiso.collageapp.android.network.tasks.AsyncException;
import org.almiso.collageapp.android.ui.views.BaseCollageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alexandr Sosorev on 28.07.2014.
 */
public class FragmentCollage extends CollageImageFragment implements View.OnClickListener {

    private ArrayList<InstaSearchResult> mSelectedPhotos;
    private BaseCollageView collageView;
    private RelativeLayout rootContainer;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSelectedPhotos = (ArrayList<InstaSearchResult>) savedInstanceState.getSerializable("mSelectedPhotos");
        }
        if (getArguments() != null) {
            mSelectedPhotos = (ArrayList<InstaSearchResult>) getArguments().getSerializable("mSelectedPhotos");
        }
        View view = inflater.inflate(R.layout.fragment_collage, container, false);
        if (mSelectedPhotos == null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    activity.onBackPressed();
                }
            });
        } else {
            setUpView(view);
        }
        return view;
    }

    private void setUpView(View view) {
        Logger.d(TAG, "setUpView");
        collageView = (BaseCollageView) view.findViewById(R.id.collageView);
        rootContainer = (RelativeLayout) view.findViewById(R.id.rootContainer);

        mImageFetcher.setImageSize(100);
        mImageFetcher.setShape(ImageShape.SHAPE_RECTANGLE);
        mImageFetcher.setImageFadeIn(false);

        collageView.setImageFetcher(mImageFetcher);
        collageView.setPhotos(mSelectedPhotos);

        view.findViewById(R.id.buttonBackground).setOnClickListener(this);
        view.findViewById(R.id.buttonSize).setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_collage, menu);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.st_collage);
        activity.getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;
            case R.id.savePhoto:
                savePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBackground:
                requestBackgroundChooser();
                break;
            case R.id.buttonSize:
                requestSizeChooser();
                break;
        }

    }


    private void requestBackgroundChooser() {
        final Context context = getActivity();
        DialogFactory factory = new DialogFactory(context);
        factory.requestBackgroundChooser(collageView);
    }


    private void requestSizeChooser() {
        final Context context = getActivity();
        DialogFactory factory = new DialogFactory(context);
        factory.requestSizeChooser(collageView);
    }

    private void savePhoto() {


        runUiTask(new AsyncAction() {
            private Uri uri;

            @Override
            public void execute() throws AsyncException {
                Bitmap bitmap = Bitmap.createBitmap(collageView.getWidth(),
                        collageView.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                collageView.draw(canvas);

                File folder = new File(Environment.getExternalStorageDirectory() + "/CollageApp");

                if (!folder.exists()) {
                    folder.mkdir();
                }

                final Calendar c = Calendar.getInstance();
                String nowDate = c.get(Calendar.DAY_OF_MONTH) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-"
                        + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE) + "-"
                        + c.get(Calendar.SECOND);
                String photoName = "Img(" + nowDate + ").jpg";

                File file = new File(folder, photoName);
                if (file.exists())
                    file.delete();

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                uri = application.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }

            @Override
            public void afterExecute() {
                Toast.makeText(application, R.string.st_photo_saved, Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle(R.string.st_app_name)
                        .setMessage(R.string.st_offer_to_share)
                        .setPositiveButton(R.string.st_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sharePhoto(uri);
                            }
                        }).setNegativeButton(R.string.st_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }

            @Override
            public void onException(AsyncException e) {
                super.onException(e);
                Toast.makeText(application, R.string.st_error_on_saving, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sharePhoto(Uri uri) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        Intent chooser = Intent.createChooser(shareIntent, getResources().getString(R.string.st_share_using));
        if (chooser.resolveActivity(activity.getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

}