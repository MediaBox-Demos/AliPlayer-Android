package com.aliyun.player.rtslivestream;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;
import com.google.android.material.snackbar.Snackbar;

public class RtsLiveStreamActivity extends AppCompatActivity {

    // load RTS library
    static {
        System.loadLibrary("RtsSDK");
    }

    private static final String TAG = "RtsLiveStreamActivity";

    private AliPlayer mAliPlayer;
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rts_live_stream);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.rts_livestream_demo_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // 初始化播放器视图
        initView();

        // Step 1: 初始化播放器
        setupPlayer();

        // Step 2: 设置播放源 & Step 3: 开始播放
        startupPlayer();
    }

    /**
     * 初始化播放器视图
     */
    private void initView() {
        mSurfaceView = findViewById(R.id.surface_view);
    }


    /**
     * Step 1: 初始化播放器
     */
    private void setupPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(RtsLiveStreamActivity.this);

        // 绑定视图
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                mAliPlayer.setSurface(surfaceHolder.getSurface());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                mAliPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                mAliPlayer.setSurface(null);
            }
        });


        //启用自动播放
        mAliPlayer.setAutoPlay(true);

    }


    /**
     * Step 2: 设置播放源 & Step 3: 开始播放
     */
    private void startupPlayer() {
        String videoURL = Constants.DataSource.SAMPLE_RTS_URL;

        if ("".equals(videoURL) || videoURL.contains(" ")) {
            Snackbar.make(mSurfaceView, "请先设置直播URL", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).setTextColor(Color.WHITE).show();
            return;
        }

        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(videoURL);
        mAliPlayer.setDataSource(urlSource);

        // Step 3: 准备播放（会自动开始播放，因为设置了 autoPlay = true）
        mAliPlayer.prepare();

        Log.d(TAG, "[Step 2&3] 开始播放视频: " + videoURL);

    }

    /**
     * recycle AliPlayer
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 4.1 停止播放
            mAliPlayer.stop();

            // 4.2 销毁播放器实例
            mAliPlayer.release();

            // (可选) 作用等同于上述两步
            // mAliPlayer.releaseAsync();

            // 4.3 清空引用，避免内存泄漏
            mAliPlayer = null;

            Log.d(TAG, "[Step 4] 播放器资源清理完成");
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupPlayer();
    }
}
