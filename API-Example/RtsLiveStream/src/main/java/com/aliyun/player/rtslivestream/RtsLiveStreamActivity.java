package com.aliyun.player.rtslivestream;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.common.Constants;
import com.aliyun.player.common.utils.ToastUtils;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;

/**
 * @author wyq
 * @date 2025/6/26
 * @brief 基础播放功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现 RTS 低延迟直播流的播放。
 * 适用于 Android 平台，集成 RtsSDK 实现 artc:// 协议拉流。
 * <p>
 * ==================== 播放器核心调用流程 ====================
 * Step 1: 加载 RTS 低延迟组件库（System.loadLibrary）
 * Step 2: 创建播放器实例（AliPlayerFactory.createAliPlayer）
 * Step 3: 设置播放视图（SurfaceView + SurfaceHolder.Callback）
 * Step 4: 配置播放参数（如低延迟缓冲策略）
 * Step 5: 设置播放源（UrlSource）
 * Step 6: 准备并开始播放（prepare + start）
 * Step 7: 播放结束或页面销毁时释放资源（stop + release）
 * <p>
 * ==================== 如何接入 RTS 低延迟直播组件 ====================
 * 官方文档：<a href="https://help.aliyun.com/zh/live/pull-streams-over-rts-on-android">...</a>
 * <p>
 * 1. 添加 Maven 仓库
 * 在项目根目录 build.gradle 中添加：
 * maven { url 'http://maven.aliyun.com/nexus/content/repositories/releases' }
 * <p>
 * 2. 添加依赖（build.gradle 模块级）
 * def player_sdk_version = "x.x.x"  // 替换为实际版本
 * def rts_sdk_version = "7.3.0"
 * <p>
 * implementation 'com.aliyun.rts.android:RtsSDK:$rts_sdk_version'
 * implementation 'com.aliyun.sdk.android:AliyunPlayer:$player_sdk_version-full'
 * implementation 'com.aliyun.sdk.android:AlivcArtc:$player_sdk_version'  // 桥接层，版本需一致
 * <p>
 * 3. 加载 RTS 动态库
 * static { System.loadLibrary("RtsSDK"); }
 * <p>
 * 4. 创建播放器并设置播放源即可使用
 */
public class RtsLiveStreamActivity extends AppCompatActivity {

    // 加载 RTS 低延迟直播组件动态库
    // 必须在使用播放器前完成加载
    static {
        System.loadLibrary("RtsSDK");
    }

    private static final String TAG = "RtsLiveStreamActivity";

    // 播放器实例（核心对象）
    private AliPlayer mAliPlayer;
    // 播放画面承载视图
    private SurfaceView mSurfaceView;

    // =============================================================================================
    // == Activity 生命周期处理
    // =============================================================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rts_live_stream);

        // 设置 ActionBar 标题和返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_rts_playback_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 1: 初始化 UI 组件（主要是 SurfaceView）
        initView();

        // Step 2: 创建播放器实例并配置基础参数
        setupPlayer();

        // Step 3 & 4: 设置播放源并启动播放流程
        startupPlayer();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 点击 ActionBar 返回按钮时关闭页面
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        // 页面销毁时清理播放器资源，防止内存泄漏或后台播放
        cleanupPlayer();
        super.onDestroy();
    }

    // =============================================================================================
    // == UI 与视图初始化
    // =============================================================================================

    /**
     * 初始化播放器视图组件
     * 获取布局中定义的 SurfaceView，用于承载视频画面
     */
    private void initView() {
        mSurfaceView = findViewById(R.id.surface_view);
    }

    // =============================================================================================
    // == 播放器初始化与配置
    // =============================================================================================

    /**
     * Step 2: 初始化播放器实例并设置基础行为
     * <p>
     * 执行流程：
     * 1. 使用工厂方法创建 AliPlayer 实例
     * 2. 设置准备完成回调（onPrepared）自动开始播放
     * 3. 注册 onInfo 回调以获取调试信息（如 TraceID）
     * 4. 绑定 SurfaceView 的生命周期，实现画面渲染
     */
    private void setupPlayer() {
        // 创建播放器对象
        mAliPlayer = AliPlayerFactory.createAliPlayer(RtsLiveStreamActivity.this);

        // 可选功能：启用单点追查（TraceID）
        // traceId 为用户/设备唯一标识（如 userID、IMEI），用于异常追踪
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);

        // 设置准备完成监听器：播放器准备好后自动开始播放
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                mAliPlayer.start();
            }
        });

        // 监听播放器信息回调，用于接收调试信息
        mAliPlayer.setOnInfoListener(infoBean -> {
            if (infoBean.getCode() == InfoCode.DemuxerTraceID) {
                String traceId = infoBean.getExtraMsg();
                Log.e("AliPlayer", "traceId:" + traceId); // 可用于阿里云后台追查
            }
        });

        // 绑定 SurfaceView 的 SurfaceHolder，实现画面渲染
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                // Surface 创建时绑定到播放器
                if (mAliPlayer != null) {
                    mAliPlayer.setSurface(surfaceHolder.getSurface());
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
                // Surface 大小变化时通知播放器
                if (mAliPlayer != null) {
                    mAliPlayer.surfaceChanged();
                }
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                // Surface 销毁时解绑，避免继续渲染
                if (mAliPlayer != null) {
                    mAliPlayer.setSurface(null);
                }
            }
        });
    }

    // =============================================================================================
    // == 播放控制：设置源 & 开始播放
    // =============================================================================================

    /**
     * Step 3 & 4: 设置播放源并启动播放
     * <p>
     * 执行流程：
     * 1. 获取配置的 RTS 播放地址
     * 2. 校验地址有效性
     * 3. 若为 artc 协议，配置低延迟参数（缓冲策略）
     * 4. 启用 RTS 自动降级功能（网络差时切换至普通流）
     * 5. 创建 UrlSource 并设置为播放源
     * 6. 调用 prepare() 准备播放（异步）
     * 7. 调用 start() 发起播放请求（prepare 完成后自动起播）
     */
    private void startupPlayer() {
        // 获取播放地址（来自常量配置）
        String videoURL = Constants.DataSource.SAMPLE_RTS_URL.trim();

        // 校验播放地址是否为空
        if (TextUtils.isEmpty(videoURL)) {
            ToastUtils.showToastLong(getString(R.string.set_stream_url_first));
            return;
        }

        // 若为 ARTC 低延迟协议，优化播放参数以降低延迟
        if (videoURL.contains("artc")) {
            // 1. 获取当前播放器配置
            PlayerConfig config = mAliPlayer.getConfig();

            // 设置最大允许延迟为 1 秒（毫秒）
            config.mMaxDelayTime = 1000;
            // 起播时最小缓冲时长（10ms，极低缓冲）
            config.mStartBufferDuration = 10;
            // 卡顿时最大缓冲上限（10ms）
            config.mHighBufferDuration = 10;

            // 2. 将修改后的配置应用到播放器
            mAliPlayer.setConfig(config);
        }

        if (mAliPlayer == null) {
            return;
        }

        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.showToastLong(errorInfo.getExtra());
            }
        });

        // 全局设置：启用 RTS 自动降级功能（默认开启）
        // 当网络不佳时，自动切换至普通直播流保障连续性
        AliPlayerGlobalSettings.setOption(AliPlayerGlobalSettings.ALLOW_RTS_DEGRADE, 1);

        // 可选功能：自定义降级流地址（当前未启用）
        // PlayerConfig config = mAliPlayer.getConfig();
        // UrlSource downgradeSource = new UrlSource();
        // downgradeSource.setUri(downgradeUrl);
        // mAliPlayer.enableDowngrade(downgradeSource, config);

        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(videoURL);
        mAliPlayer.setDataSource(urlSource);

        // Step 3: 开始准备播放（异步过程）
        mAliPlayer.prepare();

        // prepare 后可立即调用 start
        // 实际起播将在 onPrepared 回调中由 SDK 自动触发
        mAliPlayer.start();

        Log.d(TAG, "[Step 2&3] 开始播放视频: " + videoURL);
    }

    // =============================================================================================
    // == 资源清理与释放
    // =============================================================================================

    /**
     * Step 4: 释放播放器资源
     * <p>
     * 清理流程：
     * 1. 停止播放任务
     * 2. 销毁播放器实例（释放解码器、网络等资源）
     * 3. 置空引用，防止内存泄漏
     * <p>
     * 注意：
     * - 推荐使用 stop() + release() 组合（同步释放，适用于通用场景）
     * - 也可使用 releaseAsync()（异步释放，不阻塞主线程，适用于短剧等场景）
     * - 释放后不可再操作播放器实例
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 4.1 停止播放
            mAliPlayer.stop();

            // 4.2 销毁播放器，释放所有内部资源
            mAliPlayer.release();

            // 4.3 清空引用，帮助 GC 回收
            mAliPlayer = null;

            Log.d(TAG, "[Step 5] 播放器资源清理完成");
        }
    }
}
