package com.alivc.player.settings.backstage;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CustomCaptureActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(TextView.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}