package org.almiso.collageapp.android.core;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.core.model.InstaSearchResult;
import org.almiso.collageapp.android.fragments.FragmentPhotoGrid;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.ui.source.ViewSource;
import org.almiso.collageapp.android.ui.source.ViewSourceListener;
import org.almiso.collageapp.android.ui.source.ViewSourceState;
import org.almiso.collageapp.android.util.ApiUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by almiso on 10.06.2014.
 */


public class InstaSearchSource {

    protected static final String TAG = "InstaSearchSource";
    private static final int PHOTO_COUNT = 21;

    private CollageApplication application;
    private ViewSource<InstaSearchResult, InstaSearchResult> viewSource;
    private boolean isDestroyed;
    private HttpClient client;
    private boolean canLoadMore = true;
    private ViewSourceListener listener;

    private int ACTION;
    private long userId;
    private String nextUrl = "";

    public InstaSearchSource(CollageApplication application) {
        isDestroyed = false;
        this.application = application;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        client = new DefaultHttpClient(httpParams);
        client.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    }

    public ViewSourceListener getListener() {
        return listener;
    }

    public ViewSource<InstaSearchResult, InstaSearchResult> getViewSource() {
        return viewSource;
    }

    public void setListener(ViewSourceListener listener) {
        if (viewSource != null) {
            viewSource.removeListener(this.listener);
        }
        this.listener = listener;
        if (viewSource != null && listener != null) {
            viewSource.addListener(listener);
        }
    }

    public void cancelQuery() {
        this.userId = 1;
        this.nextUrl = null;
        this.ACTION = 0;
        if (viewSource != null) {
            viewSource.destroy();
            viewSource = null;
        }
    }

    private String prepareUrl() {
        String url;
        if (nextUrl == null || nextUrl.equals("")) {
            if (ACTION == FragmentPhotoGrid.ACTION_SEARCH_MY_BEST_PHOTOS) {
                url = ApiUtils.PATTERN_MY_BEST_PHOTOS + "count=" + PHOTO_COUNT + "&access_token=" +
                        application.getAuthKernel().getAccount().getAccessToken();
            } else {
                url = ApiUtils.PATTERN_USER + String.valueOf(this.userId)
                        + "/media/recent/?count=" + PHOTO_COUNT +
                        "&access_token=" + application.getAuthKernel().getAccount().getAccessToken();
            }
            nextUrl = url;
            return url;
        } else {
            return nextUrl;
        }
    }


    public void newQuery(int action, long userId) {

        this.ACTION = action;
        this.userId = userId;
        this.canLoadMore = true;

        if (viewSource != null) {
            viewSource.destroy();
            viewSource = null;
        }
        viewSource = new ViewSource<InstaSearchResult, InstaSearchResult>() {
            @Override
            protected InstaSearchResult[] loadItems(int offset) {
                while (!isDestroyed && canLoadMore) {
                    try {
                        String url = prepareUrl();
                        return doRequest(url, offset);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return new InstaSearchResult[0];
            }

            @Override
            protected long getSortingKey(InstaSearchResult obj) {
                return -obj.getIndex();
            }

            @Override
            protected long getItemKey(InstaSearchResult obj) {
                return obj.getIndex();
            }

            @Override
            protected long getItemKeyV(InstaSearchResult obj) {
                return obj.getIndex();
            }

            @Override
            protected ViewSourceState getInternalState() {
                return ViewSourceState.COMPLETED;
            }

            @Override
            protected InstaSearchResult convert(InstaSearchResult item) {
                return item;
            }
        };
        viewSource.onConnected();
        if (listener != null) {
            viewSource.addListener(listener);
        }
    }

    private InstaSearchResult[] doRequest(String newUrl, int offset) throws Exception {

        Logger.d(TAG, "<------------------- doRequest ------------------->");
        Logger.d(TAG, "newUrl = " + newUrl);

        HttpGet get = new HttpGet(newUrl);

        HttpResponse response = client.execute(get);
        if (response.getEntity().getContentLength() == 0) {
            this.canLoadMore = false;
            Logger.d(TAG, "Error: " + response.getStatusLine().getReasonPhrase());
            application.getDataSourceKernel().getExceptionSource().
                    notifyException(application.getResources().getString(R.string.st_no_photos));
            throw new IOException();
        }

        if (response.getStatusLine().getStatusCode() == 404) {
            this.canLoadMore = false;
            Logger.d(TAG, "Error: 404 " + response.getStatusLine().getReasonPhrase());
            application.getDataSourceKernel().getExceptionSource().
                    notifyException(application.getResources().getString(R.string.st_error_loading));
            throw new IOException();
        }

        if (response.getStatusLine().getStatusCode() == 400) {
            this.canLoadMore = false;
            Logger.d(TAG, "Error: 400 " + response.getStatusLine().getReasonPhrase());
            application.getDataSourceKernel().getExceptionSource().
                    notifyException(application.getResources().getString(R.string.st_user_private_acc));
            throw new IOException();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);
        byte[] data = outputStream.toByteArray();
        String responseData = new String(data);
        JSONObject res = new JSONObject(responseData);

        JSONArray array = res.getJSONArray("data");

        InstaSearchResult[] results = new InstaSearchResult[array.length()];

        ArrayList<InstaSearchResult> arrayList = new ArrayList<InstaSearchResult>();
        for (int i = 0; i < results.length; i++) {

            JSONObject record = array.getJSONObject(i);

            String id = record.getString("id");
            int index = offset + i;
            String type = record.getString("type");

            JSONObject arrLikes = record.getJSONObject("likes");
            long likes = arrLikes.getLong("count");

            JSONObject allPhotos = record.getJSONObject("images");
            String lowResolutionUrl = allPhotos.getJSONObject("low_resolution").getString("url");
            String thumbnailUrl = allPhotos.getJSONObject("thumbnail").getString("url");
            String standardResolutionUrl = allPhotos.getJSONObject("standard_resolution").getString("url");

            InstaSearchResult entry = new InstaSearchResult(id, index, "type", likes, lowResolutionUrl, thumbnailUrl,
                    standardResolutionUrl);
            if ("image".equalsIgnoreCase(type)) {
                arrayList.add(entry);
            }

        }
        InstaSearchResult[] resArray = arrayList.toArray(new InstaSearchResult[arrayList.size()]);

        boolean canLoadMore = res.getJSONObject("pagination").has("next_url");
        if (canLoadMore) {
            this.nextUrl = res.getJSONObject("pagination").getString("next_url");
            this.canLoadMore = canLoadMore;
        } else {
            this.canLoadMore = false;
        }

        return resArray;
    }

}
