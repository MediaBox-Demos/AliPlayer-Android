package com.aliyun.player.rtslivestream;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;

/**
 * @author wyq
 * @date 2025/6/26
 * @brief 基础播放功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现基础的视频播放功能
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1: 加载 RTS 低延迟直播组件库
 * - 调用 System.loadLibrary("RtsSDK");
 * <p>
 * Step 2: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 设置播放器渲染视图 SurfaceView
 * - 配置播放器基本参数（如自动播放）
 * <p>
 * Step 3: 设置播放源
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * <p>
 * Step 4: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法开始播放
 * <p>
 * Step 5: 资源清理
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 * <p>
 * ==================== 如何接入Rts 低延迟直播组件 ====================
 * <a href="https://help.aliyun.com/zh/live/pull-streams-over-rts-on-android">Android端实现RTS拉流</a>
 * Step 1; 添加Maven仓库地址
 * - 在根目录的build.gradle中添加Maven仓库地址。
 * - 阿里云相关SDK（阿里云播放器） Maven仓库地址
 * - maven { url 'http://maven.aliyun.com/nexus/content/repositories/releases' }
 * <p>
 * Step 2: 添加 maven 依赖
 * - def player_sdk_version = "x.x.x"
 * - def rts_sdk_version = "7.3.0"
 * - implementation 'com.aliyun.rts.android:RtsSDK:$rts_sdk_version' // Rts低延时直播组件
 * - implementation 'com.aliyun.sdk.android:AliyunPlayer:$player_sdk_version-full'// 播放器主库
 * - implementation 'com.aliyun.sdk.android:AlivcArtc:$player_sdk_version' // 播放器与Rts低延时直播组件的桥接层（AlivcArtc），版本号需要与播放器一致，需要和 Rts低延时直播组件 一起集成
 * <p>
 * Step 3: loadRts 库
 * - static { System.loadLibrary("RtsSDK"); }
 * <p>
 * Step 4: 创建播放器&&播放
 * - 调用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 */
public class RtsLiveStreamActivity extends AppCompatActivity {

    // load RTS library
    static {
        System.loadLibrary("RtsSDK");
    }

    private static final String TAG = "RtsLiveStreamActivity";

    // 播放器
    private AliPlayer mAliPlayer;
    // 播放器视图
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rts_live_stream);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_rts_playback_title));
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
     * Step 1： 初始化播放器视图
     */
    private void initView() {
        mSurfaceView = findViewById(R.id.surface_view);
    }

    /**
     * Step 2: 初始化播放器
     */
    private void setupPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(RtsLiveStreamActivity.this);
        // 可选：推荐使用`播放器单点追查`功能，当使用阿里云播放器 SDK 播放视频发生异常时，可借助单点追查功能针对具体某个用户或某次播放会话的异常播放行为进行全链路追踪，以便您能快速诊断问题原因，可有效改善播放体验治理效率。
        // traceId 值由您自行定义，需为您的用户或用户设备的唯一标识符，例如传入您业务的 userid 或者 IMEI、IDFA 等您业务用户的设备 ID。
        // 传入 traceId 后，埋点日志上报功能开启，后续可以使用播放质量监控、单点追查和视频播放统计功能。
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);

        // 绑定视图
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                if (mAliPlayer != null) {
                    mAliPlayer.setSurface(surfaceHolder.getSurface());
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (mAliPlayer != null) {
                    mAliPlayer.surfaceChanged();
                }
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                if (mAliPlayer != null) {
                    mAliPlayer.setSurface(null);
                }
            }
        });
    }

    /**
     * Step 3: 设置播放源 & Step 4: 开始播放
     */
    private void startupPlayer() {
        String videoURL = Constants.DataSource.SAMPLE_RTS_URL.trim();

        if (TextUtils.isEmpty(videoURL)) {
            Toast.makeText(this, getString(R.string.set_stream_url_first), Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(videoURL);
        mAliPlayer.setDataSource(urlSource);

        // Step 3: 准备播放
        mAliPlayer.prepare();
        // prepare 以后可以同步调用 start 操作，onPrepared 回调完成后会自动起播
        mAliPlayer.start();

        Log.d(TAG, "[Step 2&3] 开始播放视频: " + videoURL);

    }

    /**
     * Step 4: 资源清理
     * <p>
     * 方案1：stop + release，适用于通用场景；释放操作有耗时，会阻塞当前线程，直到资源完全释放。
     * 方案2：releaseAsync，无需手动 stop，适用于短剧等场景；异步释放资源，不阻塞线程，内部已自动调用 stop。
     * 注意：执行 release 或 releaseAsync 后，请不要再对播放器实例进行任何操作。
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 4.1 停止播放
            mAliPlayer.stop();

            // 4.2 销毁播放器实例
            mAliPlayer.release();

            // 4.3 清空引用，避免内存泄漏
            mAliPlayer = null;

            Log.d(TAG, "[Step 5] 播放器资源清理完成");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        cleanupPlayer();
        super.onDestroy();
    }
}
