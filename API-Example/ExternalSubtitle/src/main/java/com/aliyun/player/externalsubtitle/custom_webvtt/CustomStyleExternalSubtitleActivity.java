package com.aliyun.player.externalsubtitle.custom_webvtt;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.common.utils.ToastUtils;
import com.aliyun.player.externalsubtitle.R;
import com.aliyun.player.nativeclass.PlayerScene;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.videoview.AliDisplayView;
import com.aliyun.subtitle.SubTitleBase;
import com.cicada.player.utils.webVtt.VttSubtitleView;

/**
 * @author yuqiwang
 * @date 2026/01/15
 * @brief Subtitle Styling Demo with AliPlayer SDK – Best Practices
 *
 * 演示如何使用自定义样式解析器（CustomStylerWebVttResolver）实现 WebVTT 外挂字幕的自定义渲染。
 * Demonstrates how to use a custom style resolver (CustomStylerWebVttResolver)
 * to render external WebVTT subtitles with custom styling.
 *
 * <p>
 * The implementation follows 5 key steps:
 * 整体流程分为 5 个关键步骤：
 * Step 1: Initialize the player and set up listeners
 *         初始化播放器并设置监听器
 * Step 2: Initialize UI views (video + subtitle container)
 *         初始化 UI 视图（视频 + 字幕容器）
 * Step 3: Load and start video playback
 *         加载并播放视频
 * Step 4: Handle subtitle loading and display events
 *         处理字幕加载与显示事件
 * Step 5: Clean up resources to prevent memory leaks
 *         清理资源（防止内存泄漏）
 */
public class CustomStyleExternalSubtitleActivity extends AppCompatActivity {

    private static final String TAG = "SubtitleStylerActivity";

    private FrameLayout mRootView;          // Step 2: Root layout container for video and subtitles
    // Step 2: 根布局容器

    private AliPlayer mAliPlayer;           // Step 1: AliPlayer instance
    // Step 1: 播放器实例

    private VttSubtitleView mVttSubtitleView; // Step 2 & 4: Custom View for rendering WebVTT subtitles
    // Step 2 & 4: 自定义字幕显示 View

    private CustomStyleWebVttResolver mResolver; // Step 2: Custom resolver for parsing WebVTT styles
    // Step 2: 自定义字幕样式解析器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitle_external_custom_styler);

        // Set up ActionBar title and back button
        // 设置 ActionBar 标题和返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.menu_subtitle_styler_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ====== Execute core workflow ======
        // ====== 执行核心流程 ======
        setupPlayer();      // Step 1: Initialize player
        // Step 1: 初始化播放器
        setupView();        // Step 2: Initialize UI
        // Step 2: 初始化 UI
        startPlayback();    // Step 3: Start playback
        // Step 3: 开始播放
    }

    /**
     * Step 1: Initialize AliPlayer
     * - Create player instance
     * - Set up listeners for preparation, errors, video size changes, and subtitle events
     *
     * Step 1: 初始化阿里云播放器
     * - 创建播放器实例
     * - 设置准备完成、错误、视频尺寸变化、字幕显示等监听器
     */
    private void setupPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());
        Log.d(TAG, "Player created: " + mAliPlayer);
        if (mAliPlayer == null) return;

        // 设置播放场景
        mAliPlayer.setPlayerScene(PlayerScene.LONG);

        // Listen for player prepared event → trigger external subtitle loading
        // 监听播放器准备完成 → 触发外挂字幕加载
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                // Step 4 entry point: Add external subtitle track
                // Step 4 的起点：添加外挂字幕轨道
                mAliPlayer.addExtSubtitle(Constants.DataSource.EXT_SUBTITLE);
            }
        });

        // Handle playback errors
        // 错误处理
        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.showToastLong(errorInfo.getExtra());
            }
        });

        // Sync subtitle rendering area when video size changes
        // 视频尺寸变化时，同步更新字幕渲染参考区域
        mAliPlayer.setOnVideoSizeChangedListener((width, height) -> {
            int viewWidth = mRootView.getWidth();
            int viewHeight = mRootView.getHeight();
            IPlayer.ScaleMode scaleMode = mAliPlayer.getScaleMode();

            // Calculate actual video render dimensions considering scale mode
            // 计算视频实际渲染区域（考虑缩放模式）
            SubTitleBase.VideoDimensions dims =
                    SubTitleBase.getVideoDimensionsWhenRenderChanged(width, height, viewWidth, viewHeight, scaleMode);

            // Step 4: Notify subtitle view to update its reference render size
            // Step 4: 通知字幕 View 更新其定位基准
            if (mVttSubtitleView != null) {
                mVttSubtitleView.setVideoRenderSize(dims.videoDisplayWidth, dims.videoDisplayHeight);
            }
        });

        // Listen to subtitle-related events (show/hide/header)
        // 字幕事件监听（核心：字幕显示/隐藏/头部信息）
        mAliPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleExtAdded(int trackIndex, String url) {
                handleOnSubtitleExtAdded(trackIndex, url); // Step 4: Enable subtitle track
                // Step 4: 启用字幕轨道
            }

            @Override
            public void onSubtitleShow(int trackIndex, long id, String data) {
                if (mVttSubtitleView != null) {
                    Log.d(TAG, "[onSubtitleShow] data: " + data);
                    mVttSubtitleView.show(id, data); // Step 4: Display subtitle
                    // Step 4: 显示字幕
                }
            }

            @Override
            public void onSubtitleHide(int trackIndex, long id) {
                Log.d(TAG, "[onSubtitleHide] dismiss ID: " + id);
                mVttSubtitleView.dismiss(id); // Step 4: Hide subtitle
                // Step 4: 隐藏字幕
            }

            @Override
            public void onSubtitleHeader(int i, String header) {
                Log.d(TAG, "[onSubtitleHeader] header: " + header);
                if (!TextUtils.isEmpty(header)) {
                    mVttSubtitleView.setVttHeader(header); // Step 4: Apply WebVTT header styles
                    // Step 4: 设置 WebVTT 头部样式
                }
            }
        });
    }

    /**
     * Step 2: Initialize UI Views
     * - Bind video display area
     * - Create and add custom subtitle View to layout
     *
     * Step 2: 初始化 UI 视图
     * - 绑定视频显示区域
     * - 创建并添加自定义字幕 View 到布局中
     */
    private void setupView() {
        mRootView = findViewById(R.id.styler_root_layout);
        // Step 2: Video rendering view
        AliDisplayView mDisplayView = findViewById(R.id.styler_ali_display_view);

        // Use TextureView for video rendering (supports overlaying Views like subtitles)
        // 使用 TextureView 渲染视频（支持叠加 View，如字幕）
        mDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.TextureView);
        mAliPlayer.setDisplayView(mDisplayView);

        // Initialize subtitle components
        // 初始化字幕组件
        initSubtitleView();
    }

    /**
     * Step 2 (Sub-step): Initialize subtitle rendering components
     * - Create custom resolver to parse WebVTT styles
     * - Create VttSubtitleView and add it on top of video
     *
     * Step 2 (子步骤): 初始化字幕渲染组件
     * - 创建自定义解析器（用于解析 WebVTT 中的样式）
     * - 创建 VttSubtitleView 并添加到根布局（覆盖在视频上）
     */
    private void initSubtitleView() {
        mResolver = new CustomStyleWebVttResolver(this);
        mVttSubtitleView = new VttSubtitleView(this, mResolver);

        // Center-align subtitles
        // 设置字幕居中显示
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        mVttSubtitleView.setLayoutParams(params);

        // Add subtitle view on top of video container
        // 将字幕 View 添加到视频容器之上
        mRootView.addView(mVttSubtitleView);
    }

    /**
     * Step 3: Start video playback
     * - Set data source (sample video URL)
     * - Call prepare() + start()
     *
     * Step 3: 开始播放视频
     * - 设置数据源（示例视频 URL）
     * - 调用 prepare + start
     */
    private void startPlayback() {
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        mAliPlayer.prepare();
        mAliPlayer.start(); // Can call start() immediately after prepare(); playback starts automatically in onPrepared()
        // prepare 后可立即 start，onPrepared 后自动播放
    }

    /**
     * Step 4: Handle external subtitle track loaded event
     * - Select and enable the subtitle track
     *
     * Step 4: 处理外挂字幕轨道加载完成事件
     * - 选中并启用该字幕轨道
     */
    private void handleOnSubtitleExtAdded(int trackIndex, String url) {
        Log.d(TAG, "[handleOnSubtitleExtAdded] Subtitle URL: " + url);
        mAliPlayer.selectExtSubtitle(trackIndex, true); // true means enable the track
        // true 表示启用
    }

    /**
     * Step 5: Clean up player resources to avoid memory leaks
     *
     * Step 5: 清理播放器资源（防止内存泄漏）
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            mAliPlayer.stop();
            mAliPlayer.release();
            mAliPlayer = null;
            Log.d(TAG, "[Step 5] Player resources cleaned up");
        }
    }

    /**
     * Step 5: Destroy subtitle-related components
     *
     * Step 5: 销毁字幕相关组件
     */
    private void destroyView() {
        if (mVttSubtitleView != null) {
            mVttSubtitleView.destroy();
        }
        if (mResolver != null) {
            mResolver.destroy();
        }
        mVttSubtitleView = null;
        mResolver = null;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        // Step 5: Clean up all resources when Activity is destroyed
        // Step 5: Activity 销毁时，统一清理资源
        cleanupPlayer();
        destroyView();
        super.onDestroy();
    }
}
