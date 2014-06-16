package org.almiso.collageapp.android.preview.cache;

import android.content.Context;
import android.graphics.Bitmap;

import org.almiso.collageapp.android.mdeia.Optimizer;
import org.almiso.collageapp.android.util.IOUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by almiso on 10.06.2014.
 */
public class ImageStorage {
    private static final String TAG = "ImageStorage";

    private File folder;

    public ImageStorage(Context context, String name) {
        folder = new File(context.getFilesDir(), name);
        folder.mkdirs();
    }

    //TODO Check this function
    //I don`t know how it really works, but it` magic
    private String getFileName(String key) {
//        return folder.getAbsolutePath() + "/" + ToHex(SHA1(key.getBytes()));
        return "";
    }

    public Optimizer.BitmapInfo tryLoadFile(String key, Bitmap reuse) {
        String fileName = getFileName(key);
        if (!new File(fileName).exists()) {
            return null;
        }
        try {
            return Optimizer.loadTo(fileName, reuse);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap tryLoadFile(String key) {
        String fileName = getFileName(key);
        if (!new File(fileName).exists()) {
            return null;
        }

        try {
            return Optimizer.load(fileName);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public byte[] tryLoadData(String key) {
        String fileName = getFileName(key);
        if (!new File(fileName).exists()) {
            return null;
        }

        try {
            return IOUtils.readAll(fileName);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }


}
