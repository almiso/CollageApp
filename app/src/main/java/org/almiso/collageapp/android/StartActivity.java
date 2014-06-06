package org.almiso.collageapp.android;

import android.app.Activity;
import android.os.Bundle;

import static org.almiso.collageapp.android.R.layout;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_start);
    }
}
