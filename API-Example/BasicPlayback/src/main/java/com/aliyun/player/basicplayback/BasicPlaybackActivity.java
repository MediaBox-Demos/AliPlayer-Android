package com.aliyun.player.basicplayback;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;

/**
 * @author keria
 * @date 2025/6/3
 * @brief 基础播放功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现基础的视频播放功能
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 设置播放器渲染视图 SurfaceView
 * - 配置播放器基本参数（如自动播放）
 * <p>
 * Step 2: 设置播放源
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * <p>
 * Step 3: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 播放器会自动开始播放（如果设置了 autoPlay = true）
 * <p>
 * Step 4: 资源清理
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 */
public class BasicPlaybackActivity extends AppCompatActivity {
    private static final String TAG = "BasicPlaybackActivity";

    // 播放器实例
    private AliPlayer mAliPlayer;
    // 播放器视图
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_playback);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.basic_playback_demo_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化视图
        initViews();

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2 & Step 3: 设置播放源并开始播放
        startPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Step 4: 资源清理
        cleanupPlayer();
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
        mSurfaceView = findViewById(R.id.surface_view);
    }

    /**
     * Step 1: 创建播放器实例
     */
    private void setupPlayer() {
        // 1.1 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());

        // 1.2 设置播放器渲染视图
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mAliPlayer.setSurface(holder.getSurface());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                mAliPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                mAliPlayer.setSurface(null);
            }
        });

        // 1.3 配置播放器基本参数
        mAliPlayer.setAutoPlay(true);

        Log.d(TAG, "[Step 1] 播放器创建完成: " + mAliPlayer);
    }

    /**
     * Step 2: 设置播放源 & Step 3: 开始播放
     */
    private void startPlayback() {
        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        // Step 3: 准备播放（会自动开始播放，因为设置了 autoPlay = true）
        mAliPlayer.prepare();

        Log.d(TAG, "[Step 2&3] 开始播放视频: " + Constants.DataSource.SAMPLE_VIDEO_URL);
    }

    /**
     * Step 4: 资源清理
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 4.1 停止播放
            mAliPlayer.stop();

            // 4.2 销毁播放器实例
            mAliPlayer.release();

            // 4.3 清空引用，避免内存泄漏
            mAliPlayer = null;

            Log.d(TAG, "[Step 4] 播放器资源清理完成");
        }
    }
}
