package org.almiso.collageapp.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by almiso on 10.06.2014.
 */
public class IOUtils {
    private static ThreadLocal<byte[]> buffers = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[4 * 1024];
        }
    };


    public static byte[] readAll(String fileName) throws IOException {
        byte[] res;
        InputStream in = new FileInputStream(fileName);
        res = readAll(in);
        in.close();
        return res;
    }

    public static byte[] readAll(InputStream in) throws IOException {
        return readAll(in, null);
    }

    public static byte[] readAll(InputStream in, ProgressListener listener) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
        byte[] buffer = buffers.get();
        int len;
        int readed = 0;
        try {
            while ((len = bufferedInputStream.read(buffer)) >= 0) {
                Thread.yield();
                os.write(buffer, 0, len);
                readed += len;
                if (listener != null) {
                    listener.onProgress(readed);
                }
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }

    public static byte[] downloadFile(String url) throws IOException {
        return downloadFile(url, null);
    }

    public static byte[] downloadFile(String url, ProgressListener listener) throws IOException {
        URL urlSpec = new URL(url);
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) urlSpec.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            InputStream in = urlConnection.getInputStream();
            return IOUtils.readAll(in, listener);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static interface ProgressListener {
        public void onProgress(int bytes);
    }


    public static String streamToString(InputStream is) throws IOException {
        String string = "";
        if (is != null) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
            } finally {
                is.close();
            }
            string = stringBuilder.toString();
        }
        return string;
    }


}
