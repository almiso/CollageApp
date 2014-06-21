package org.almiso.collageapp.android.fragments;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.mdeia.Optimizer;
import org.almiso.collageapp.android.preview.InstaPreviewView;
import org.almiso.collageapp.android.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by almiso on 10.06.2014.
 */
public class FragmentPreviewPhoto extends CollageFragment implements View.OnClickListener {


    private InstaSearchResult instaSearchResult;
    private boolean isClosed = false;
    private ImageView preview;
    private ProgressBar progressBar;

    private boolean wasLoaded = false;
    private boolean wasSaved = false;
    private Bitmap savingPhoto;

    private boolean canOpenProf;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            canOpenProf = savedInstanceState.getBoolean("canOpenProf");
            instaSearchResult = (InstaSearchResult) savedInstanceState.getSerializable("result");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("canOpenProf", canOpenProf);
        outState.putSerializable("result", instaSearchResult);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isClosed = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!wasLoaded) {
            loadOriginal();
        } else {
            preview.setImageBitmap(savingPhoto);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            canOpenProf = savedInstanceState.getBoolean("canOpenProf");
            instaSearchResult = (InstaSearchResult) savedInstanceState.getSerializable("result");
        }
        if (getArguments() != null) {
            canOpenProf = getArguments().getBoolean("canOpenProf");
            instaSearchResult = (InstaSearchResult) getArguments().getSerializable("result");
        } else {
            canOpenProf = false;
            instaSearchResult = null;
        }
        View view = (inflater).inflate(R.layout.fragment_preview_photo, container, false);
        if (instaSearchResult == null) {
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
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        goneView(progressBar);
        preview = (ImageView) view.findViewById(R.id.preview);
        isClosed = false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatarTouchLayer:
                if (canOpenProf && (instaSearchResult.getAuthor().getId() != application.getMyId())) {
                    getRootController().openFragmentUserProfile(instaSearchResult.getAuthor());
                }
                break;
        }
    }

    private void loadOriginal() {
        showView(progressBar);
        Bitmap thumbPreview = application.getUiKernel().getInstaMediaLoader().tryLoadInstaPreview(instaSearchResult);
        if (thumbPreview != null) {
            preview.setImageBitmap(thumbPreview);
        }
        new Thread() {
            @Override
            public void run() {
                while (!isClosed) {
                    byte[] data;
                    try {
                        data = application.getUiKernel().getWebImageStorage().
                                tryLoadData(instaSearchResult.getStandardResolutionUrl());
                        if (data == null) {
                            data = IOUtils.downloadFile(instaSearchResult.getStandardResolutionUrl(), new IOUtils.ProgressListener() {
                                @Override
                                public void onProgress(final int bytes) {
                                    secureCallback(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress((bytes) / 1000);
                                        }
                                    });
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                            return;
                        }
                        continue;
                    }

                    try {
                        final Bitmap res = Optimizer.optimize(data);
                        if (res == null) {
                            throw new Exception("unable to load image");
                        }
                        Logger.d(TAG, "data.length = " + data.length);
                        secureCallback(new Runnable() {
                            @Override
                            public void run() {
                                goneView(progressBar);
                                preview.setImageBitmap(res);
                                savingPhoto = res;
                                wasLoaded = true;
                            }
                        });
                    } catch (Throwable e) {
                        secureCallback(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), R.string.st_error_download_image, Toast.LENGTH_LONG)
                                        .show();
                                goneView(progressBar);
                            }
                        });
                    }
                    return;
                }
            }
        }.start();
    }


    private void saveImage(Bitmap savingPhoto) {
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
            savingPhoto.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(getActivity(), R.string.st_photo_saved, Toast.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        application.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_preview, menu);

        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getSherlockActivity().getSupportActionBar().setTitle(R.string.st_photo);
        getSherlockActivity().getSupportActionBar().setSubtitle(null);

        MenuItem avatarItem = menu.findItem(R.id.userAvatar);
        InstaPreviewView imageView = (InstaPreviewView) avatarItem.getActionView().findViewById(R.id.image);
        imageView.setEmptyDrawable(R.drawable.ic_action_person);
        if (instaSearchResult != null) {
            imageView.requestUserAvatar(instaSearchResult.getAuthor());
        }
        View touchLayer = avatarItem.getActionView().findViewById(R.id.avatarTouchLayer);
        touchLayer.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                activity.onBackPressed();
                return true;
            case R.id.ic_save:
                if (wasLoaded) {
                    if (!wasSaved) {
                        wasSaved = true;
                        saveImage(savingPhoto);
                    } else {
                        Toast.makeText(getActivity(), R.string.st_photo_was_saved, Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.st_photo_not_loaded, Toast.LENGTH_LONG)
                            .show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}