package org.almiso.collageapp.android.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.mdeia.Optimizer;
import org.almiso.collageapp.android.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;


/**
 * Created by almiso on 13.06.2014.
 */
public class FragmentInstaCollage extends CollageFragment implements View.OnClickListener {
    protected static String TAG = "FragmentInstaCollage";


    private ImageView preview1;
    private ImageView preview2;
    private ImageView preview3;
    private ImageView preview4;

    private ImageView image;

    private ProgressBar loading1;
    private ProgressBar loading2;
    private ProgressBar loading3;
    private ProgressBar loading4;

    private Button shareImage;

    private boolean isClosed = false;


    private LinkedHashMap<Integer, InstaSearchResult> photos;

    private boolean complited1 = false;
    private boolean complited2 = false;
    private boolean complited3 = false;
    private boolean complited4 = false;

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private Bitmap bitmap4;

    private Bitmap resultPhoto;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Object inputPhotos = (Object) savedInstanceState.getSerializable("photos");
            photos = (LinkedHashMap<Integer, InstaSearchResult>) inputPhotos;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("photos", photos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Object inputPhotos = (Object) savedInstanceState.getSerializable("photos");
            photos = (LinkedHashMap<Integer, InstaSearchResult>) inputPhotos;
        }
        Bundle args = getArguments();
        if (args != null) {
            photos = (LinkedHashMap<Integer, InstaSearchResult>) args.get("photos");
        } else {
            photos = null;
        }

        View view = (inflater).inflate(R.layout.fragment_insta_collage, container, false);

        if (photos == null) {
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
        preview1 = (ImageView) view.findViewById(R.id.preview1);
        preview2 = (ImageView) view.findViewById(R.id.preview2);
        preview3 = (ImageView) view.findViewById(R.id.preview3);
        preview4 = (ImageView) view.findViewById(R.id.preview4);

        image = (ImageView) view.findViewById(R.id.image);


        loading1 = (ProgressBar) view.findViewById(R.id.loading1);
        loading2 = (ProgressBar) view.findViewById(R.id.loading2);
        loading3 = (ProgressBar) view.findViewById(R.id.loading3);
        loading4 = (ProgressBar) view.findViewById(R.id.loading4);

        shareImage = (Button) view.findViewById(R.id.buttonShareImage);
        shareImage.setOnClickListener(this);
        shareImage.setEnabled(false);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) preview1.getLayoutParams();
        layoutParams.height = width / 2;
        layoutParams.width = width / 2;

        preview1.setLayoutParams(layoutParams);
        preview2.setLayoutParams(layoutParams);
        preview3.setLayoutParams(layoutParams);
        preview4.setLayoutParams(layoutParams);

        image.setVisibility(View.GONE);
        setUpPreviews();
        isClosed = false;
    }

    private void setUpPreviews() {

        InstaSearchResult photo1 = photos.get(1);
        Bitmap photoPreview1 = application.getUiKernel().getInstaMediaLoader().tryLoadInstaPreview(photo1);
        if (photoPreview1 != null) {
            preview1.setImageBitmap(photoPreview1);
        }
        InstaSearchResult photo2 = photos.get(2);
        Bitmap photoPreview2 = application.getUiKernel().getInstaMediaLoader().tryLoadInstaPreview(photo2);
        if (photoPreview2 != null) {
            preview2.setImageBitmap(photoPreview2);
        }
        InstaSearchResult photo3 = photos.get(3);
        Bitmap photoPreview3 = application.getUiKernel().getInstaMediaLoader().tryLoadInstaPreview(photo3);
        if (photoPreview3 != null) {
            preview3.setImageBitmap(photoPreview3);
        }
        InstaSearchResult photo4 = photos.get(4);
        Bitmap photoPreview4 = application.getUiKernel().getInstaMediaLoader().tryLoadInstaPreview(photo4);
        if (photoPreview4 != null) {
            preview4.setImageBitmap(photoPreview4);
        }

        loadPhoto1(photo1, loading1, preview1, 1);
        loadPhoto1(photo2, loading2, preview2, 2);
        loadPhoto1(photo3, loading3, preview3, 3);
        loadPhoto1(photo4, loading4, preview4, 4);

    }

    private void loadPhoto1(final InstaSearchResult photo, final ProgressBar loading, final ImageView preview,
                            final int count) {
        new Thread() {
            @Override
            public void run() {
                while (!isClosed) {
                    byte[] data;
                    try {
                        data = application.getUiKernel().getWebImageStorage()
                                .tryLoadData(photo.getStandardResolutionUrl());
                        if (data == null) {
                            data = IOUtils.downloadFile(photo.getStandardResolutionUrl(),
                                    new IOUtils.ProgressListener() {
                                        @Override
                                        public void onProgress(final int bytes) {
                                            secureCallback(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loading.setProgress((100 * bytes) / 1000);
                                                }
                                            });
                                        }
                                    }
                            );
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
                        secureCallback(new Runnable() {
                            @Override
                            public void run() {
                                goneView(loading);
                                preview.setImageBitmap(res);
                                setComplited(count, res);
                                updateGlobalPreview();
                            }

                            private void setComplited(int count, Bitmap bitmap) {
                                if (count == 1) {
                                    complited1 = true;
                                    bitmap1 = bitmap;
                                } else if (count == 2) {
                                    complited2 = true;
                                    bitmap2 = bitmap;
                                } else if (count == 3) {
                                    complited3 = true;
                                    bitmap3 = bitmap;
                                } else if (count == 4) {
                                    complited4 = true;
                                    bitmap4 = bitmap;
                                }

                            }
                        });
                    } catch (Throwable e) {
                        secureCallback(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                                goneView(loading);
                            }
                        });
                    }
                    return;
                }
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isClosed = true;
    }

    private void updateGlobalPreview() {
        if (complited1 && complited2 && complited3 && complited4) {
            if (bitmap1 != null && bitmap2 != null && bitmap3 != null && bitmap4 != null) {

                LayoutInflater inflater = (LayoutInflater) application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.dialog_add_text_on_photo, null);

                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setView(view)
                        .setTitle(R.string.st_photo)
                        .setPositiveButton(R.string.st_add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText edPhotoText = (EditText) view.findViewById(R.id.edPhotoText);
                                String text = edPhotoText.getText().toString().trim();
                                makeCollage(text);
                            }
                        }).setNegativeButton(R.string.st_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                makeCollage("");
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();


            }
        }
    }


    private void makeCollage(String text) {
        goneView(preview1);
        goneView(preview2);
        goneView(preview3);
        goneView(preview4);
        resultPhoto = combineImages(bitmap1, bitmap2, bitmap3, bitmap4, text);
        if (resultPhoto != null) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) preview1.getLayoutParams();
            layoutParams.height = width;
            layoutParams.width = width;
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            image.setLayoutParams(layoutParams);

            image.setImageBitmap(resultPhoto);
            showView(image);
            shareImage.setEnabled(true);
        }
    }

    private Bitmap combineImages(Bitmap bm1, Bitmap bm2, Bitmap bm3, Bitmap bm4, String text) {
        Bitmap image = null;

        image = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().widthPixels,
                Bitmap.Config.ARGB_8888);

        float edge = getResources().getDisplayMetrics().widthPixels / 2;

        Canvas canvas = new Canvas(image);
        canvas.drawBitmap(bm1, null, new RectF(0f, 0f, edge, edge), null);
        canvas.drawBitmap(bm2, null, new RectF(edge, 0f, edge * 2, edge), null);
        canvas.drawBitmap(bm3, null, new RectF(0f, edge, edge, edge * 2), null);
        canvas.drawBitmap(bm4, null, new RectF(edge, edge, edge * 2, edge * 2), null);
        if (!TextUtils.isEmpty(text)) {

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            paint.setTextSize(getSp(28f));
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(10f, 10f, 10f, Color.BLACK);
            paint.setTypeface(Typeface.SERIF);


            Rect rectText = new Rect();
            paint.getTextBounds(text, 0, text.length(), rectText);
            int x = 0;
            if (getResources().getDisplayMetrics().widthPixels > text.length()) {
                x = (image.getWidth() / 2) - (rectText.width() / 2);
            }


            Logger.d(TAG, "x = " + x);
            canvas.drawText(text, x, image.getHeight() - rectText.height() * 2, paint);


        }

        return image;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSherlockActivity().getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getSherlockActivity().getSupportActionBar().setTitle(R.string.st_collage);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonShareImage:
                sharePhoto();
                break;
        }
    }

    private void sharePhoto() {
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
            resultPhoto.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        Uri uri = application.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        final Intent emailIntent1 = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent1.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent1.setType("image/jpeg");
        Intent chooser = Intent.createChooser(emailIntent1, getResources().getString(R.string.st_share_using));
        if (chooser.resolveActivity(activity.getPackageManager()) != null) {
            startActivity(chooser);
        }

//        startActivity(Intent.createChooser(emailIntent1, getResources().getString(R.string.st_share_using)));
    }
}
