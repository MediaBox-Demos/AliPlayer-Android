package com.aliyun.player.rtslivestream;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

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
import com.aliyun.player.videoview.AliDisplayView;

/**
 * @author wyq
 * @date 2025/6/26
 * @brief 基础播放功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现 RTS 低延迟直播流的播放。
 * 适用于 Android 平台，集成 RtsSDK 实现 artc:// 协议拉流。
 * <p>
 * ==================== 播放器核心调用流程 ====================
 * Step 1: 加载 RTS 低延迟组件库 System.loadLibrary("RtsSDK")
 * Step 2: 创建播放器实例（AliPlayerFactory.createAliPlayer）
 * Step 3: 设置播放视图（AliDisplayView）
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

    // Step 1:
    // 加载 RTS 低延迟直播组件动态库
    // 必须在使用播放器前完成加载
    static {
        System.loadLibrary("RtsSDK");
    }

    private static final String TAG = "RtsLiveStreamActivity";

    // 播放器实例（核心对象）
    private AliPlayer mAliPlayer;
    // 播放画面承载视图
    private AliDisplayView mAliDisplayView;

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

        // 创建播放器并绑定播放器视图
        setupPlayer();

        // 配置数据源 + 播放参数 + 异常监听，播放数据源
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
    // == 播放器初始化与配置
    // =============================================================================================

    /**
     * Step 2 & 3: 创建播放器实例（AliPlayerFactory.createAliPlayer) 并设置播放视图（AliDisplayView）
     * <p>
     * 执行流程：
     * <ul>
     *   <li>1. 通过工厂创建 AliPlayer 实例</li>
     *   <li>2. 初始化 AliDisplayView（播放器视图），并设置首选显示视图类型为 SurfaceView</li>
     *   <li>3. 绑定播放视图到播放器</li>
     *   <li>4. setTraceId(traceId), 启用单点追查(可选)。</li>
     *   <li>5. 监听播放器 Info 事件，获取本次播放的 TraceID（用于问题排查）</li>
     * </ul>
     */
    private void setupPlayer() {
        // 创建播放器对象
        mAliPlayer = AliPlayerFactory.createAliPlayer(RtsLiveStreamActivity.this);

        Log.d(TAG, "[Step 2] 开始播放视频: " + mAliPlayer);

        // 初始化播放器视图控件
        mAliDisplayView = findViewById(R.id.display_view);

        // 设置首选显示视图类型为 SurfaceView
        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);

        // 绑定视图
        mAliPlayer.setDisplayView(mAliDisplayView);

        // 可选功能：启用单点追查（TraceID）
        // traceId 为用户/设备唯一标识（如 userID、IMEI），用于异常追踪
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);

        // 设置 OnInfo 监听，获取TraceID
        // 注意：这个 TraceId 和 上面的 TraceId 功能不同哦
        // 每一次低延时播放都会有一个traceId，可用于问题排查，可以通过播放器事件回调拿到traceid
        mAliPlayer.setOnInfoListener(infoBean -> {
            if (infoBean.getCode() == InfoCode.DemuxerTraceID) {
                String traceId = infoBean.getExtraMsg();
                Log.e("AliPlayer", "traceId:" + traceId); // 可用于阿里云后台追查
            }
        });

        Log.d(TAG, "[Step 3] 设置播放视图: " + mAliDisplayView);

    }

    // =============================================================================================
    // == 播放控制：设置源 & 开始播放
    // =============================================================================================

    /**
     * 设置播放源并配置启动参数
     * <p>
     * <b>执行流程：</b>
     * <ul>
     * <li>1. 获取播放源 </li>
     * <li>2. 配置播放参数 </li>
     * <li>3. 设置异常监听 setOnError </li>
     * <li>4. 启用 RTS 自动降级功能（网络差时切换至普通流）</li>
     * <li>5. 创建 UrlSource 并设置为播放源 </li>
     * <li>6. 播放数据源 prepare + start </li>
     * </ul>
     */
    private void startupPlayer() {
        // 获取播放地址（来自常量配置）
        String videoUrl = Constants.DataSource.SAMPLE_RTS_URL.trim();

        // 校验播放地址是否为空
        if (TextUtils.isEmpty(videoUrl)) {
            ToastUtils.showToastLong(getString(R.string.set_stream_url_first));
            return;
        }

        // 若为 ARTC 低延迟协议，优化播放参数以降低延迟
        if (videoUrl.contains("artc")) {
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

            Log.d(TAG, "[Step 4] 播放器播放参数配置完成" );
        }


        // 设置播放器异常监听
        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                // toast 打印异常信息
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

        // 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(videoUrl);
        mAliPlayer.setDataSource(urlSource);

        Log.d(TAG, "[Step 5] 设置播放源: " + videoUrl);

        // 开始准备播放（异步过程）
        mAliPlayer.prepare();

        // prepare 后可立即调用 start
        // 实际起播将在 onPrepared 回调中由 SDK 自动触发
        mAliPlayer.start();

        Log.d(TAG, "[Step 6] 开始播放视频: " + videoUrl);
    }

    // =============================================================================================
    // == 资源清理与释放
    // =============================================================================================

    /**
     * Step 7: 释放播放器资源
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
            // 停止播放
            mAliPlayer.stop();

            // 销毁播放器，释放所有内部资源
            mAliPlayer.release();

            // 清空引用，帮助 GC 回收
            mAliPlayer = null;

            Log.d(TAG, "[Step 7] 播放器资源清理完成");
        }
    }
}
