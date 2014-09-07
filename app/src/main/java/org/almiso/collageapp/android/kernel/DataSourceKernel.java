package org.almiso.collageapp.android.kernel;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import org.almiso.collageapp.android.core.ExceptionSource;
import org.almiso.collageapp.android.core.InstaSearchSource;
import org.almiso.collageapp.android.core.InstaUserSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by almiso on 10.06.2014.
 */
public class DataSourceKernel {
    protected static final String TAG = "DataSourceKernel";

    private ApplicationKernel kernel;
    private ExceptionSource exceptionSource;

    private volatile HashMap<Long, InstaSearchSource> searchSource;
    private volatile HashMap<Long, InstaUserSource> userSource;


    public DataSourceKernel(ApplicationKernel kernel) {
        this.kernel = kernel;
        init();
    }

    private void init() {
        exceptionSource = new ExceptionSource();
        userSource = new HashMap<>();
        searchSource = new HashMap<>();
    }

    public InstaSearchSource getInstaSearchSource(long index) {
        if (searchSource.containsKey(index)) {
            return searchSource.get(index);
        } else {
            InstaSearchSource source = new InstaSearchSource(kernel.getApplication());
            searchSource.put(index, source);
            return source;
        }
    }

    public void removeInstaSearchSource(long index) {
        if (searchSource.containsKey(index)) {
            searchSource.remove(index);
        }
    }

    public InstaUserSource getInstaUserSource(long index) {
        if (userSource.containsKey(index)) {
            return userSource.get(index);
        } else {
            InstaUserSource source = new InstaUserSource(kernel.getApplication());
            userSource.put(index, source);
            return source;
        }
    }

    public ExceptionSource getExceptionSource() {
        return exceptionSource;
    }


    public Uri saveToGallery(Bitmap bitmap) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/CollageApp");

        if (!folder.exists()) {
            folder.mkdir();
        }

        final Calendar c = Calendar.getInstance();
        String nowDate = c.get(Calendar.DAY_OF_MONTH) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-"
                + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE) + "-"
                + c.get(Calendar.SECOND);
        String photoName = "CollageApp img(" + nowDate + ").jpg";

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

        return kernel.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public Uri saveTempPhoto(Bitmap bitmap, int position) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/CollageApp/tmp");

        if (!folder.exists()) {
            folder.mkdir();
        }

//        String photoName = "TempPhoto" + position + ".jpg";
        String photoName = "TempPhoto" + position;

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
        return Uri.fromFile(file);
    }

    public Uri getTempPhoto(int position) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/CollageApp/tmp");

        if (!folder.exists()) {
            folder.mkdir();
        }

//        String photoName = "TempPhoto" + position + ".jpg";
        String photoName = "TempPhoto" + position;

        File file = new File(folder, photoName);
        if (file.exists()) {
            return Uri.fromFile(file);
        }

        return null;
    }

    public void clearTmp() throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + "/CollageApp/tmp");
        delete(folder);
    }

    private void delete(File f) throws IOException {
        if (!f.exists()) {
            return;
        }
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

}
