package org.almiso.collageapp.android.core;

import android.text.TextUtils;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.fragments.FragmentUserList;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.network.Api;
import org.almiso.collageapp.android.ui.source.ViewSource;
import org.almiso.collageapp.android.ui.source.ViewSourceListener;
import org.almiso.collageapp.android.ui.source.ViewSourceState;
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
 * Created by almiso on 21.06.2014.
 */
public class InstaUserSource {

    protected static final String TAG = "InstaUserSource";

    private CollageApplication application;
    private ViewSource<InstaUser, InstaUser> viewSource;
    private boolean isDestroyed;
    private HttpClient client;
    private boolean canLoadMore = true;
    private ViewSourceListener listener;


    private int ACTION;
    private long userId;
    private String nextUrl = "";

    public InstaUserSource(CollageApplication application) {
        isDestroyed = false;
        this.application = application;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        client = new DefaultHttpClient(httpParams);
        client.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    }

    public ViewSource<InstaUser, InstaUser> getViewSource() {
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

    public void cancelSearch() {
        this.userId = 1;
        this.nextUrl = null;
        this.ACTION = 0;
        if (viewSource != null) {
            viewSource.destroy();
            viewSource = null;
        }
    }

    private String prepareUrl() {
        if (TextUtils.isEmpty(nextUrl)) {
            nextUrl = Api.PATTERN_SEARCH_USER + userId + getFollowType(ACTION) + "access_token=" +
                    application.getAuthKernel().getAccount().getAccessToken();

            return nextUrl;
        } else {
            return nextUrl;
        }
    }

    private String getFollowType(int action) {
        if (action == FragmentUserList.ACTION_FOLLOWS) {
            return "/follows?";
        } else if (action == FragmentUserList.ACTION_FOLLOWED_BY) {
            return "/followed-by?";
        } else {
            return "/follows?";
        }
    }

    public void newSearch(int action, long userId) {

        this.ACTION = action;
        this.userId = userId;
        this.canLoadMore = true;

        if (viewSource != null) {
            viewSource.destroy();
            viewSource = null;
        }
        viewSource = new ViewSource<InstaUser, InstaUser>() {
            @Override
            protected InstaUser[] loadItems(int offset) {
                while (!isDestroyed && canLoadMore) {
                    try {
                        String url = prepareUrl();
                        return doRequest(url, offset);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return new InstaUser[0];
            }

            @Override
            protected long getSortingKey(InstaUser obj) {
                return -obj.getId();
            }

            @Override
            protected long getItemKey(InstaUser obj) {
                return obj.getId();
            }

            @Override
            protected long getItemKeyV(InstaUser obj) {
                return obj.getId();
            }

            @Override
            protected ViewSourceState getInternalState() {
                return ViewSourceState.COMPLETED;
            }

            @Override
            protected InstaUser convert(InstaUser item) {
                return item;
            }
        };
        viewSource.onConnected();
        if (listener != null) {
            viewSource.addListener(listener);
        }
    }

    private InstaUser[] doRequest(String newUrl, int offset) throws Exception {

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

        InstaUser[] results = new InstaUser[array.length()];

        ArrayList<InstaUser> arrayList = new ArrayList<InstaUser>();
        for (int i = 0; i < results.length; i++) {
            JSONObject jObject = array.getJSONObject(i);
            InstaUser user = new InstaUser();
            user.setFull_name(jObject.getString("username"));
            user.setBio(jObject.getString("bio"));
            user.setWebsite(jObject.getString("website"));
            user.setProfile_picture_url(jObject.getString("profile_picture"));
            user.setFull_name(jObject.getString("full_name"));
            user.setId(Long.parseLong(jObject.getString("id")));
            arrayList.add(user);
        }
        InstaUser[] resArray = arrayList.toArray(new InstaUser[arrayList.size()]);

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
