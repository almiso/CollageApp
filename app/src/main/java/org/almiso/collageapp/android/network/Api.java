package org.almiso.collageapp.android.network;

import android.content.ContentValues;
import android.util.Log;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.core.model.InstaUser;
import org.almiso.collageapp.android.core.model.InstaUserDependence;
import org.almiso.collageapp.android.fragments.FragmentUserList;
import org.almiso.collageapp.android.log.Logger;
import org.almiso.collageapp.android.tasks.CollageException;
import org.almiso.collageapp.android.util.ApiUtils;
import org.almiso.collageapp.android.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by almiso on 07.06.2014.
 */
public class Api {
    public static final String TAG = "Api";

    private CollageApplication application;

    private static final String PATTERN_API = "https://api.instagram.com/v1/";
    private static final String PATTERN_SEARCH = PATTERN_API + "users/search?q=";
    public static final String PATTERN_SEARCH_USER = PATTERN_API + "users/";
    private static final String PATTERN_COUNT = "&count=";
    private static final String PATTERN_ACCESS_TOKEN = "&access_token=";//46461385.f59def8.42df3b82464943218291370ab65eba76";


    public Api(CollageApplication application) {
        this.application = application;
    }


    public synchronized InstaUser getUser(String userNick) throws CollageException {
        return getUser(userNick, 1);
    }

    public synchronized InstaUser getUser(String userNick, int count) throws CollageException {
        InstaUser user;
        long mUserId = -1;
        String mUrl = PATTERN_SEARCH + userNick + PATTERN_COUNT + count + PATTERN_ACCESS_TOKEN +
                application.getAuthKernel().getAccount().getAccessToken();

        String data = null;
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(mUrl).openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            conn.disconnect();
            conn = null;
            data = sb.toString();
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Url parsing was failed: " + mUrl);
            throw new CollageException(3, "Error during getUserId");
        } catch (IOException ex) {
            Log.d(TAG, mUrl + " does not exists");
            throw new CollageException(3, "Error during getUserId");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Out of memory!!!");
            throw new CollageException(3, "Error during getUserId");
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        JSONArray jArray;
        user = new InstaUser();
        try {
            jArray = (new JSONObject(data)).getJSONArray("data");
            if (jArray.length() == 0) {
                Logger.d(TAG, "jArray.length() == 0");
                throw new CollageException(2, "Error during getUserId");
            }

            JSONObject jObject = jArray.getJSONObject(0);

            user.setFull_name(jObject.getString("username"));
            user.setBio(jObject.getString("bio"));
            user.setWebsite(jObject.getString("website"));
            user.setProfile_picture_url(jObject.getString("profile_picture"));
            user.setFull_name(jObject.getString("full_name"));
            user.setId(Long.parseLong(jObject.getString("id")));

//            JSONObject userProfile = jObject.getJSONObject("counts");
//            user.setMediaCount(Long.parseLong(userProfile.getString("media")));
//            user.setFollowsCount(Long.parseLong(userProfile.getString("follows")));
//            user.setFollowedByCount(Long.parseLong(userProfile.getString("followed_by")));

        } catch (JSONException e) {
            e.printStackTrace();
            throw new CollageException(3, "Error during getUserId");
        }

        return user;
    }

    public ContentValues logIn(String request_token) throws CollageException {

        ContentValues result = new ContentValues();

        String mUrl = ApiUtils.PATTERN_AUTH_LOGIN + request_token;
        try {
            URL url = new URL(mUrl);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
            outputStreamWriter.write("client_id=" + ApiUtils.PATTERN_CLIENT_ID +
                    "&client_secret=" + ApiUtils.PATTERN_CLIENT_SECRET +
                    "&grant_type=authorization_code" +
                    "&redirect_uri=" + ApiUtils.CALLBACK_URL
                    + "&code=" + request_token);
            outputStreamWriter.flush();

            String response = IOUtils.streamToString(httpsURLConnection.getInputStream());
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();

            String access_token = jsonObject.getString("access_token");

            long id = jsonObject.getJSONObject("user").getLong("id");
            String username = jsonObject.getJSONObject("user").getString("username");
            String full_name = jsonObject.getJSONObject("user").getString("full_name");
            String profile_picture = jsonObject.getJSONObject("user").getString("profile_picture");

            result.put("id", id);
            result.put("username", username);
            result.put("full_name", full_name);
            result.put("profile_picture", profile_picture);
            result.put("access_token", access_token);
            result.put("request_token", request_token);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized InstaUserDependence getUserDependence(long userId) throws CollageException {
        InstaUserDependence data = null;

        String mUrl = PATTERN_SEARCH_USER + userId + "/?access_token=" +
                application.getAuthKernel().getAccount().getAccessToken();

        Logger.d(TAG, "Loading user dependence by url = " + mUrl);

        HttpURLConnection conn = null;
        String result = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            conn = (HttpURLConnection) new URL(mUrl).openConnection();
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setDoInput(true);
            conn.connect();


//            int responseCode = conn.getResponseCode();
//            if (responseCode != 200) {
//                InputStream errorStream = conn.getErrorStream();
//                if (errorStream != null) {
//                    String errorResult;
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
//
//
//                    String errorLine = null;
//                    while ((errorLine = reader.readLine()) != null) {
//                        stringBuilder.append(errorLine + "\n");
//                    }
//                    errorResult = stringBuilder.toString();
//                    try {
//
//                        JSONObject jObject = new JSONObject(errorResult);
//                        JSONObject errObj = jObject.getJSONObject("meta");
//
//                        String error_type = errObj.getString("error_type");
//                        String error_message = errObj.getString("error_message");
//
//                        Logger.d(TAG, "error_type  = " + error_type);
//                        Logger.d(TAG, "error_message  = " + error_message);
//                        if (error_type.equals("APINotAllowedError") ||
//                                error_message.equalsIgnoreCase("you cannot view this resource")) {
//                            Logger.d(TAG, "Success! pushing CollageException.");
//                            throw new CollageException(5, "Error APINotAllowedError");
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            } else {
//
//            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            conn.disconnect();
            conn = null;
            result = stringBuilder.toString();

        } catch (MalformedURLException ex) {
            Log.e(TAG, "Url parsing was failed: " + mUrl);
            throw new CollageException(3, "Error during getUserDependence");
        } catch (IOException ex) {
            Log.d(TAG, "IOException on " + mUrl);
            InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                String errorResult;
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                String errorLine = null;
                try {
                    while ((errorLine = errorReader.readLine()) != null) {
                        stringBuilder.append(errorLine + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new CollageException(4, "Error during getUserDependence");
                }
                errorResult = stringBuilder.toString();
                try {
                    JSONObject jObject = new JSONObject(errorResult);
                    JSONObject errObj = jObject.getJSONObject("meta");

                    String error_type = errObj.getString("error_type");
                    String error_message = errObj.getString("error_message");

                    Logger.d(TAG, "error_type  = " + error_type);
                    Logger.d(TAG, "error_message  = " + error_message);
                    if (error_type.equals("APINotAllowedError") ||
                            error_message.equalsIgnoreCase("you cannot view this resource")) {
                        return  new InstaUserDependence(userId, 0, 0, 0, true);
//                        throw new CollageException(5, "Error APINotAllowedError");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                throw new CollageException(4, "Error during getUserDependence");
            }
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Out of memory!!!");
            throw new CollageException(3, "Error during getUserDependence");
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        JSONArray jArray;
        try {
            JSONObject jObject = (new JSONObject(result)).getJSONObject("data");
            JSONObject res = jObject.getJSONObject("counts");

            data = new InstaUserDependence(userId,
                    Long.parseLong(res.getString("media")),
                    Long.parseLong(res.getString("follows")),
                    Long.parseLong(res.getString("followed_by")), false);

        } catch (JSONException e) {
            e.printStackTrace();
            throw new CollageException(3, "Error during getUserDependence");
        }

        return data;
    }

    public ArrayList<InstaUser> getFriends(long userId, int action) throws CollageException {
        ArrayList<InstaUser> data = null;

        String mUrl = PATTERN_SEARCH_USER + userId + getFollowType(action) + "access_token=" +
                application.getAuthKernel().getAccount().getAccessToken();

        HttpURLConnection conn = null;
        String result = null;
        try {
            conn = (HttpURLConnection) new URL(mUrl).openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            conn.disconnect();
            conn = null;
            result = sb.toString();
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Url parsing was failed: " + mUrl);
            throw new CollageException(3, "Error during getUserDependence");
        } catch (IOException ex) {
            Log.d(TAG, mUrl + " does not exists");
            throw new CollageException(3, "Error during getUserDependence");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Out of memory!!!");
            throw new CollageException(3, "Error during getUserDependence");
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        JSONArray jArray;

        try {
            jArray = (new JSONObject(result)).getJSONArray("data");
            if (jArray.length() == 0) {
                Logger.d(TAG, "jArray.length() == 0");
                throw new CollageException(2, "Error during getUserId");
            }
            data = new ArrayList<InstaUser>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                InstaUser user = new InstaUser();
                user.setFull_name(jObject.getString("username"));
                user.setBio(jObject.getString("bio"));
                user.setWebsite(jObject.getString("website"));
                user.setProfile_picture_url(jObject.getString("profile_picture"));
                user.setFull_name(jObject.getString("full_name"));
                user.setId(Long.parseLong(jObject.getString("id")));
                data.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new CollageException(3, "Error during getUserId");
        }


        return data;
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
}
