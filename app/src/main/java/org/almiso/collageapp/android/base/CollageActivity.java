package org.almiso.collageapp.android.base;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created by almiso on 06.06.2014.
 */
public class CollageActivity extends SherlockFragmentActivity {

    protected CollageApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (CollageApplication) getApplicationContext();
    }

}
