package com.aliyun.player.floatwindow;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FloatWindowActivity extends AppCompatActivity {
    private static final String TAG = "FloatWindowActivity";

    private Button mButton;

    private boolean isShowFloatingWindow = false;

    private FloatWindowService mFloatWindowService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floatwindow);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_float_window_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化视图
        initViews();
    }

    @Override
    protected void onDestroy() {
        hideFloatingWindow();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        mButton = findViewById(R.id.btn_view);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShowFloatingWindow) {
                    showFloatingWindow();
                } else {
                    hideFloatingWindow();
                }
            }
        });
    }

    // 显示悬浮窗
    private void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // 如果没有悬浮窗权限，请求权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1000);
            isShowFloatingWindow = true;
        } else {
            // 启动悬浮窗服务
            startService(new Intent(FloatWindowActivity.this, FloatWindowService.class));
            isShowFloatingWindow = true;
        }
        if (mFloatWindowService == null) {
            mFloatWindowService = new FloatWindowService();
        }
    }

    // 隐藏悬浮窗
    private void hideFloatingWindow() {
        Log.e(TAG, "隐藏悬浮窗口");
        getApplicationContext().stopService(new Intent(FloatWindowActivity.this, FloatWindowService.class));
        isShowFloatingWindow = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                Log.e(TAG, "权限请求后启动服务");
                // 权限granted，启动服务
                startService(new Intent(FloatWindowActivity.this, FloatWindowService.class));
            } else {
                // 权限被拒绝
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
