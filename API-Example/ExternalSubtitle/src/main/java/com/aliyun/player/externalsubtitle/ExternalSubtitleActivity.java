package com.aliyun.player.externalsubtitle;


import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;
import com.aliyun.subtitle.SubtitleView;

/**
 * @author junhuiYe
 * @date 2025/6/6
 * @brief 播放器字幕功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现字幕功能演示
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 设置播放器渲染视图 TextureView
 * - 配置播放器基本参数
 * <p>
 * Step 2: 设置播放源
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * <p>
 * Step 3: 准备播放
 * - 调用 prepare() 方法准备播放
 * <p>
 * Step 4: 播放器状态监听
 * - 调用 setOnPreparedListener 准备完成监听
 * - 调用 setOnCompletionListener 播放完成监听
 * Step 5: 字幕设置
 * - 调用 addExtSubtitle() 方法添加字幕
 * Step 6: 开始播放
 * - 调用 start() 方法开始播放
 * Step 7: 设置字幕监听
 * - 调用 setOnSubtitleDisplayListener 字幕监听
 * Step 8: 清理资源
 * - 调用 releaseAsync() 异步销毁播放器实例
 */
public class ExternalSubtitleActivity extends AppCompatActivity {
    private static final String TAG = "ExternalSubtitleActivity";
    // 字幕视图
    private SubtitleView subtitleView;
    private FrameLayout mFrameLayout;
    // 字幕文件
    private String EXT_SUBTITLE = "";

    // 播放器实例
    private AliPlayer mAliPlayer;
    // 播放器视图
    private TextureView mTextureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_subtitle);
        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.basic_playback_demo_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化视图
        initViews();

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2 & Step 3: 设置播放源并开始播放
        startPlayback();

        // Step 4: 播放器状态监听 & Step 5: 字幕设置 & Step 6: 播放器开始播放
        playbackStatusListener();

        // Step 7：设置字幕监听
        setExternalSubtitleListener();
    }

    private void initViews() {
        mFrameLayout = findViewById(R.id.external_subtitle_main);
        mTextureView = findViewById(R.id.texture_view);
        //用于显示SRT和VTT字幕
        subtitleView = new SubtitleView(this);
        //将字幕View添加到布局视图中
        mFrameLayout.addView(subtitleView);
    }

    private void setupPlayer() {
        // 1.1 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);

        // 1.2 设置播放器渲染视图
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                mAliPlayer.setSurface(new Surface(surface));
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                mAliPlayer.surfaceChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                mAliPlayer.setSurface(null);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });
    }

    /**
     * Step 2: 设置播放源 & Step 3: 开始播放
     */
    @SuppressLint("LongLogTag")
    private void startPlayback() {
        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        mAliPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
        // Step 3: 准备播放
        mAliPlayer.prepare();

        Log.d(TAG, "[Step 2&3] 开始播放视频: " + Constants.DataSource.SAMPLE_VIDEO_URL);
    }

    /**
     * Step 4: 播放器状态监听
     * Step 5: 字幕设置
     * Step 6: 播放器开始播放
     */
    private void playbackStatusListener() {
        // Step4：准备完成监听
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                // step: 5 字幕设置（需要在 onPrepared 中进行设置）
                mAliPlayer.addExtSubtitle(EXT_SUBTITLE);
                // Step 6: 播放器开始播放
                mAliPlayer.start();
            }
        });

        // 播放完成监听
        mAliPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {

            }
        });
    }

    // Step 7：设置字幕监听
    private void setExternalSubtitleListener() {
        mAliPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleExtAdded(int trackIndex, String url) {
            }

            @Override
            public void onSubtitleShow(int trackIndex, long id, String data) {
                // 字幕
                SubtitleView.Subtitle subtitle = new SubtitleView.Subtitle();
                subtitle.id = id + "";
                subtitle.content = data;
                // 显示字幕
                subtitleView.show(subtitle);
            }

            @Override
            public void onSubtitleHide(int trackIndex, long id) {
                // 去除字幕
                subtitleView.dismiss(id + "");
            }

            @Override
            public void onSubtitleHeader(int trackIndex, String header) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Step：清理资源
        mAliPlayer.releaseAsync();
    }
}