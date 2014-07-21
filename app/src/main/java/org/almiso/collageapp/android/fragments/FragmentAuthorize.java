package org.almiso.collageapp.android.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.almiso.collageapp.android.R;
import org.almiso.collageapp.android.activity.StartActivity;
import org.almiso.collageapp.android.base.CollageFragment;
import org.almiso.collageapp.android.tasks.AsyncAction;
import org.almiso.collageapp.android.tasks.AsyncException;
import org.almiso.collageapp.android.tasks.CollageException;
import org.almiso.collageapp.android.util.ApiUtils;

/**
 * Created by almiso on 13.06.2014.
 */
public class FragmentAuthorize extends CollageFragment {

    protected static final String TAG = "FragmentAuthorize";

    private String request_token;
    private ProgressBar loading;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorize, null);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        WebView webView = (WebView) view.findViewById(R.id.web);
        loading = (ProgressBar) view.findViewById(R.id.loading);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        webView.setWebViewClient(new AuthWebViewClient());
        webView.setWebChromeClient(new WebLoading());

        webView.loadUrl(ApiUtils.PATTERN_AUTH);
    }

    public class AuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(ApiUtils.CALLBACK_URL)) {
                System.out.println(url);
                String parts[] = url.split("=");
                request_token = parts[1];
                logIn(request_token);
                return true;
            }
            return false;
        }
    }

    private class WebLoading extends WebChromeClient {
        public void onProgressChanged(WebView webView, int progress) {
            if (progress == 100) {
                goneView(loading);
            } else {
                showView(loading);
                loading.setProgress(progress);
            }
        }
    }

    private void logIn(final String token) {
        runUiTask(new AsyncAction() {
            private ContentValues result;

            @Override
            public void execute() throws AsyncException {
                try {
                    result = application.getApi().logIn(token);
                } catch (CollageException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void afterExecute() {
                if (result.containsKey("access_token")) {

                    application.getAuthKernel().logIn(result.getAsLong("id"),
                            result.getAsString("username"),
                            result.getAsString("full_name"),
                            result.getAsString("profile_picture"),
                            result.getAsString("access_token"),
                            result.getAsString("request_token"));

                    getRootController().clearBackStack();
                    ((StartActivity) activity).doInitApp(false);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.st_sing_in);
        activity.getSupportActionBar().setSubtitle(null);
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

}
