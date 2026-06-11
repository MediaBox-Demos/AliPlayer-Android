package com.alivc.player.settings.backstage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.alivc.player.settings.backstage.sp.SharedPrefBusinessManager;
import com.alivc.player.settings.backstage.util.FloatingWindowPermissionHelper;
import com.alivc.player.settings.backstage.widget.AUIPlayerTextSwitch;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AUIBackstageActivity extends AppCompatActivity {
    private String content;
    private static Handler sMainHandler = new Handler(Looper.getMainLooper());

    private static final int DEFAULT_PLAYER_POOL_CAPACITY = 2;
    private static final int PLAYER_POOL_CAPACITY_THREE = 3;

    private static final ArrayList<String> spinnerArray = new ArrayList<>();
    private static final ArrayList<String> spinnerURLArray = new ArrayList<>();

    private ArrayAdapter<String> spinnerAdapter;

    private TextView mClearLocalCacheTv;
    private TextView mBuildVersionTv;
    private AUIPlayerTextSwitch mCoverImageFallbackBtn;
    private AUIPlayerTextSwitch mPlayerPoolCapacityBtn;

    private Button mPipPermissionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_backstage);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        spinnerArray.clear();
        spinnerURLArray.clear();
    }

    private void initView() {
        mClearLocalCacheTv = findViewById(R.id.btn_clear_local_cache);
        mClearLocalCacheTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
                Toast.makeText(AUIBackstageActivity.this, getString(R.string.clear_local_cache_done), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mBuildVersionTv = findViewById(R.id.tv_build_version);
        mBuildVersionTv.setText(getBuildVersion());

        mCoverImageFallbackBtn = findViewById(R.id.btn_enable_cover_image_fallback);
        mCoverImageFallbackBtn.setTextViewText(getString(R.string.enable_cover_image_fallback_strategy));
        mCoverImageFallbackBtn.setOnSwitchToggleListener(new AUIPlayerTextSwitch.OnSwitchToggleListener() {
            @Override
            public void onSwitchToggled(boolean isChecked) {
                SharedPrefBusinessManager.setCoverFallbackStrategy(isChecked);

                // update video list configurations
                AUIBackstageSettings.updateVideoListConfigurations();
            }
        });

        mPlayerPoolCapacityBtn = findViewById(R.id.btn_player_pool_capacity);
        mPlayerPoolCapacityBtn.setTextViewText(getString(R.string.player_pool_capacity_three));
        mPlayerPoolCapacityBtn.setOnSwitchToggleListener(new AUIPlayerTextSwitch.OnSwitchToggleListener() {
            @Override
            public void onSwitchToggled(boolean isChecked) {
                int capacity = isChecked ? PLAYER_POOL_CAPACITY_THREE : DEFAULT_PLAYER_POOL_CAPACITY;
                if (SharedPrefBusinessManager.getPlayerPoolCapacity(DEFAULT_PLAYER_POOL_CAPACITY) != capacity) {
                    SharedPrefBusinessManager.setPlayerPoolCapacity(capacity);

                    // update video list configurations
                    AUIBackstageSettings.updateVideoListConfigurations();

                    killApp();
                }
            }
        });

        mPipPermissionBtn = findViewById(R.id.btn_player_pip_permission);
        mPipPermissionBtn.setText(getString(R.string.player_pip_permission));
        mPipPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestFloatingWindowPermission();
            }
        });

        spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_select, spinnerArray);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
    }

    private void initData() {
        // init cover image fallback strategy state
        mCoverImageFallbackBtn.setSwitchChecked(AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY);
        mPlayerPoolCapacityBtn.setSwitchChecked(AUIShortVideoListConstants.PLAYER_POOL_CAPACITY == PLAYER_POOL_CAPACITY_THREE);
    }

    private int getSpinnerSelection() {
        int selection = -1;
        String selectedURL = SharedPrefBusinessManager.getSelectedPlayListInfoListUrl();
        if (!TextUtils.isEmpty(selectedURL)) {
            for (int i = 0; i < spinnerURLArray.size(); i++) {
                if (TextUtils.equals(selectedURL, spinnerURLArray.get(i))) {
                    selection = i;
                    break;
                }
            }
        }
        return selection;
    }

    /**
     * 清除本地缓存
     */
    private void clearCache() {
        AliPlayerGlobalSettings.clearCaches();
        Glide.get(AUIBackstageActivity.this).clearMemory();
    }

    /**
     * 获取编译版本
     */
    private String getBuildVersion() {
        StringBuilder sb = new StringBuilder();
        String sdkVersion = AliPlayerFactory.getSdkVersion();
        sb.append("Build Version: ").append(sdkVersion).append("\n");
        return sb.toString();
    }

    /**
     * 强制退出APP
     */
    private static void killApp() {
        try {
            Thread.sleep(2 * 1000L);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkAndRequestFloatingWindowPermission() {
        if (FloatingWindowPermissionHelper.hasFloatingWindowPermission(this)) {
            // 有权限，启动悬浮窗
            Toast.makeText(this, getString(R.string.pip_permission_already_open), Toast.LENGTH_SHORT).show();
        } else {
            // 没有权限，显示说明对话框
            FloatingWindowPermissionHelper.showPermissionDialog(this,
                    new FloatingWindowPermissionHelper.OnPermissionDialogListener() {
                        @Override
                        public void onConfirm() {
                            // 用户确认，跳转到权限设置页面
                            FloatingWindowPermissionHelper.requestFloatingWindowPermission(AUIBackstageActivity.this);
                        }

                        @Override
                        public void onCancel() {
                            // 用户取消，显示提示
                            Toast.makeText(AUIBackstageActivity.this, getString(R.string.pip_need_permission), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}