package com.aliyun.auiplayerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListActivity;
import com.alivc.player.scenes.shortplaylistfeeds.AUIShortPlaylistFeedsActivity;
import com.alivc.player.scenes.shortplaylist.AUIShortPlaylistActivity;
import com.alivc.player.settings.backstage.AUIBackstageActivity;
import com.alivc.player.settings.backstage.AUIBackstageSettings;
import com.aliyun.auifullscreen.AUIFullScreenActivity;
import com.aliyun.auiplayerapp.utils.PermissionUtils;
import com.aliyun.auiplayerapp.view.AUIPlayerActionBar;
import com.aliyun.auiplayerapp.view.AUIPlayerBaseListActivity;
import com.aliyun.video.MainActivity;
import com.aliyun.vodplayerview.activity.AliyunPlayerSettingActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerMainActivity extends AUIPlayerBaseListActivity {

    private AUIPlayerActionBar mAUIPlayerActionBar;

    private static final int REQUEST_PERMISSION_STORAGE = 0x0001;

    private static final int INDEX_FEED_FLOW = 0;
    private static final int INDEX_SHORT_VIDEO_LIST = 1;
    private static final int INDEX_SHORT_PLAYLIST_FEEDS = 2;
    private static final int INDEX_SHORT_PLAYLIST_THEATER = 3;
    private static final int INDEX_FULL_SCREEN = 4;
    private static final int INDEX_PARAM = 5;

    private ListModel mListModel;

    @Override
    public int getTitleResId() {
        return R.string.player_title;
    }

    @Override
    public boolean showBackBtn() {
        return !isTaskRoot();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemStatusBar();
        initSettingView();
    }

    private void initSettingView() {
        mAUIPlayerActionBar = findViewById(R.id.aui_player_base_title);
        mAUIPlayerActionBar.getLeftImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mAUIPlayerActionBar.setOnsettingClickListener(new AUIPlayerActionBar.OnSettingClickListener() {
            @Override
            public void onsettingClick() {
                Intent settingView = new Intent(PlayerMainActivity.this, AUIBackstageActivity.class);
                startActivity(settingView);
            }
        });
        mAUIPlayerActionBar.getTitleView().setText(getTitleResId());
        if (showBackBtn()) {
            mAUIPlayerActionBar.showLeftView();
        } else {
            mAUIPlayerActionBar.hideLeftView();
        }
    }

    @Override
    public List<ListModel> createListData() {
        List<ListModel> menu = new ArrayList<>();
        menu.add(new ListModel(INDEX_FEED_FLOW, R.drawable.ic_player_xinxi, getResources().getString(R.string.player_feed_flow), getResources().getString(R.string.player_feed_flow_msg)));
        menu.add(new ListModel(INDEX_SHORT_PLAYLIST_THEATER, R.drawable.ic_player_chenjin, getResources().getString(R.string.player_scene_short_playlist_theater), getResources().getString(R.string.player_scene_short_playlist_theater_msg)));
        menu.add(new ListModel(INDEX_SHORT_PLAYLIST_FEEDS, R.drawable.ic_player_quanping, getResources().getString(R.string.player_scene_short_playlist_feeds), getResources().getString(R.string.player_scene_short_playlist_feeds_msg)));
        menu.add(new ListModel(INDEX_SHORT_VIDEO_LIST, R.drawable.ic_player_chenjin, getResources().getString(R.string.player_video_list), getResources().getString(R.string.player_feed_flow_function_msg)));
        menu.add(new ListModel(INDEX_FULL_SCREEN, R.drawable.ic_player_zidingyi, getResources().getString(R.string.player_full_screen), getResources().getString(R.string.player_video_full_screen_msg)));
        menu.add(new ListModel(INDEX_PARAM, R.drawable.ic_player_zidingyi, getResources().getString(R.string.player_video), getResources().getString(R.string.player_video_params_msg)));
        return menu;
    }

    @Override
    public void onListItemClick(ListModel model) {
        mListModel = model;
        String[] per = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ?
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} :
                new String[]{Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO};
        if (PermissionUtils.checkPermissionsGroup(this, per)) {
            ActivityCompat.requestPermissions((Activity) this, per, REQUEST_PERMISSION_STORAGE);
        } else {
            onModelItemClick(model);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;

        // 判断是否所有的权限都已经授予了
        // Determine if all permissions have been granted.
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }

        if (!isAllGranted) {
            // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
            // Pop up a dialog telling the user why they need the permission, and directing them to manually turn on the permission button in the app's permissions manager.
            Toast.makeText(this, getString(R.string.alivc_recorder_camera_permission_tip), Toast.LENGTH_SHORT).show();
        } else {
            onModelItemClick(mListModel);
        }
    }

    private void onModelItemClick(ListModel model) {
        switch (model.index) {
            case INDEX_FEED_FLOW:
                Intent feedFlow = new Intent(this, MainActivity.class);
                startActivity(feedFlow);
                break;
            case INDEX_SHORT_PLAYLIST_THEATER: {
                Intent intent = new Intent(this, AUIShortPlaylistActivity.class);
                startActivity(intent);
                break;
            }
            case INDEX_SHORT_PLAYLIST_FEEDS: {
                Intent intent = new Intent(getBaseContext(), AUIShortPlaylistFeedsActivity.class);
                startActivity(intent);
                break;
            }
            case INDEX_SHORT_VIDEO_LIST: {
                Intent intent = new Intent(getBaseContext(), AUIShortVideoListActivity.class);
                startActivity(intent);
                break;
            }
            case INDEX_FULL_SCREEN: {
                Intent fullscreenIntent = new Intent(this, AUIFullScreenActivity.class);
                startActivity(fullscreenIntent);
                break;
            }
            case INDEX_PARAM:
                AliyunPlayerSettingActivity.jump(PlayerMainActivity.this);
                break;
        }
    }

    private void hideSystemStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AUIBackstageSettings.updateVideoListConfigurations();
    }
}
