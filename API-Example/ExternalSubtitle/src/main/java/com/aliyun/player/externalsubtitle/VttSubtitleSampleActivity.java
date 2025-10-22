package com.aliyun.player.externalsubtitle;

import android.os.Bundle;
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
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.videoview.AliDisplayView;
import com.aliyun.subtitle.SubTitleBase;
import com.cicada.player.utils.webVtt.VttSubtitleView;

/**
 * @author junhuiYe
 * @date 2025/10/20
 * @brief 播放器VTT字幕功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现VTT字幕功能演示
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 可选：设置 traceId 开启播放器单点追查
 * <p>
 * Step 2: 初始化视图
 * - 加载 AliDisplayView 并设置视图回调
 * - 加载 VTT字幕视图
 * <p>
 * Step 3: 设置播放源
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * <p>
 * Step 4: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法开始播放
 * <p>
 * Step 5: 字幕设置
 * - 调用 addExtSubtitle() 方法添加字幕
 * <p>
 * Step 6: 设置字幕监听
 * - 调用 setOnSubtitleDisplayListener 字幕监听
 * - 调用 setOnVideoSizeChangedListener 视频尺寸变化监听（VTT字幕必需）
 * <p>
 * Step 7: 清理资源
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 销毁字幕视图，清理相关引用，避免内存泄漏
 */
public class VttSubtitleSampleActivity extends AppCompatActivity {
    private static final String TAG = "VttSubtitleSample";

    // 可选（非必需）：播放起始位置
    private static final long VIDEO_START_TIME_MILLS = 8 * 1000;

    // 根布局
    private FrameLayout mRootFrameLayout;
    // VTT字幕视图
    private VttSubtitleView mVttSubtitleView;

    // 播放器实例
    private AliPlayer mAliPlayer;
    // 播放器视图
    private AliDisplayView mAliDisplayView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vtt_subtitle_sample);
        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.menu_vtt_subtitle_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2: 初始化视图
        initViews();

        // Step 2 & Step 3: 设置播放源并准备播放
        startPlayback();

        // Step 4: 播放器状态监听 & Step 5: 字幕设置 & Step 6: 播放器开始播放
        setupPlayerListeners();

        // Step 7：设置字幕监听
        setVttSubtitleListener();
    }

    /**
     * 初始化VTT字幕视图
     */
    private void initSubtitleView() {
        // 用于显示VTT字幕
        mVttSubtitleView = new VttSubtitleView(this);

        // 设置字幕的显示位置 - 添加到布局中央
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER; // 添加到布局中央

        // 将VTT字幕View添加到根布局视图中
        mRootFrameLayout.addView(mVttSubtitleView, params);
    }

    /**
     * 销毁VTT字幕视图
     */
    private void destroySubtitleView() {
        mVttSubtitleView.destroy();
    }

    /**
     * Step 1: 创建播放器实例
     */
    private void setupPlayer() {
        // 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());

        // 可选：推荐使用`播放器单点追查`功能，当使用阿里云播放器 SDK 播放视频发生异常时，可借助单点追查功能针对具体某个用户或某次播放会话的异常播放行为进行全链路追踪，以便您能快速诊断问题原因，可有效改善播放体验治理效率。
        // traceId 值由您自行定义，需为您的用户或用户设备的唯一标识符，例如传入您业务的 userid 或者 IMEI、IDFA 等您业务用户的设备 ID。
        // 传入 traceId 后，埋点日志上报功能开启，后续可以使用播放质量监控、单点追查和视频播放统计功能。
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);

        Log.d(TAG, "[Step 1] 播放器创建完成: " + mAliPlayer);

        if (mAliPlayer == null) {
            return;
        }

        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.showToastLong(errorInfo.getExtra());
            }
        });
    }

    /**
     * Step 2: 初始化视图
     */
    private void initViews() {
        // 根布局
        mRootFrameLayout = findViewById(R.id.vtt_root_layout);

        // 播放器视图
        mAliDisplayView = findViewById(R.id.vtt_sample_ali_display_view);
        // 可以通过 setPreferDisplayView() 设置播放视图类型
        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);

        mAliPlayer.setDisplayView(mAliDisplayView);

        // VTT字幕视图
        initSubtitleView();
        Log.d(TAG, "[Step 2] 视图初始化完成");
    }

    /**
     * Step 3: 设置播放源 & Step 4: 开始播放
     */
    private void startPlayback() {
        // Step 3: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_VTT_SUBTITLE_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        // 可选（非必需）：设置视频播放起始位置
        // Accurate 表示精准 seek 到指定时间戳的位置播放，Inaccurate 表示 seek 到离指定时间戳最近的关键帧位置播放
        mAliPlayer.setStartTime(VIDEO_START_TIME_MILLS, IPlayer.SeekMode.Accurate);

        // Step 4: 准备播放
        mAliPlayer.prepare();
        // prepare 以后可以同步调用 start 操作，onPrepared 回调完成后会自动起播
        mAliPlayer.start();

        Log.d(TAG, "[Step 3&4] 开始播放视频: " + Constants.DataSource.SAMPLE_VIDEO_URL);
    }

    /**
     * Step 5: 字幕设置
     */
    private void setupPlayerListeners() {
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                // step: 5 字幕设置（需要在 onPrepared 中进行设置）
                mAliPlayer.addExtSubtitle(Constants.DataSource.EXT_SUBTITLE_VTT);
                Log.d(TAG, "[Step 5] 添加外挂字幕");
            }
        });
    }

    /**
     * Step 6：设置字幕监听
     */
    private void setVttSubtitleListener() {
        mAliPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleExtAdded(int trackIndex, String url) {
                mAliPlayer.selectExtSubtitle(trackIndex, true);
                Log.d(TAG, "[Step 6] 选择外挂字幕");
            }

            @Override
            public void onSubtitleShow(int trackIndex, long id, String data) {
                // 显示VTT字幕
                if (mVttSubtitleView != null) {
                    mVttSubtitleView.show(id, data);
                }
            }

            @Override
            public void onSubtitleHide(int trackIndex, long id) {
                // 隐藏VTT字幕
                if (mVttSubtitleView != null) {
                    mVttSubtitleView.dismiss(id);
                }
            }

            @Override
            public void onSubtitleHeader(int trackIndex, String header) {
                // 设置VTT字幕头部信息
                if (mVttSubtitleView != null) {
                    mVttSubtitleView.setVttHeader(header);
                }
            }
        });

        // 设置VideoSizeChangedListener，下述流程为必要流程，需要将数据回传到 vttSubtitleView中
        mAliPlayer.setOnVideoSizeChangedListener(new IPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {
                // 获取视图尺寸
                int viewWidth = mRootFrameLayout.getWidth();
                int viewHeight = mRootFrameLayout.getHeight();
                IPlayer.ScaleMode mScaleMode = mAliPlayer.getScaleMode();

                // 计算视频渲染尺寸
                SubTitleBase.VideoDimensions videoDimensions =
                        SubTitleBase.getVideoDimensionsWhenRenderChanged(width, height, viewWidth, viewHeight, mScaleMode);

                // 设置VTT字幕视图的视频渲染尺寸
                if (mVttSubtitleView != null) {
                    mVttSubtitleView.setVideoRenderSize(videoDimensions.videoDisplayWidth, videoDimensions.videoDisplayHeight);
                }

                Log.d(TAG, "[Step 6] 视频尺寸变化 - 原始尺寸: " + width + "x" + height +
                        ", 渲染尺寸: " + videoDimensions.videoDisplayWidth + "x" + videoDimensions.videoDisplayHeight);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        // Step 7：清理资源
        cleanupPlayer();
        destroySubtitleView();

        super.onDestroy();
    }

    /**
     * Step 7: 资源清理
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 7.1 停止播放
            mAliPlayer.stop();

            // 7.2 销毁播放器实例
            mAliPlayer.release();

            // 7.3 清空引用，避免内存泄漏
            mAliPlayer = null;

            Log.d(TAG, "[Step 7] 播放器资源清理完成");
        }
    }
}
