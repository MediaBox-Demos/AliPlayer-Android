package com.aliyun.player.playback;

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
 * Step 0: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 可选：设置 traceId 开启播放器单点追查
 * <p>
 * Step 1: 初始化视图
 * - 加载 SurfaceView 并设置视图回调
 * <p>
 * Step 2: 设置播放源
 * - 创建播放数据源对象
 * - 调用 setDataSource() 设置播放数据源
 * <p>
 * Step 3: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法开始播放
 * <p>
 * Step 4: 资源清理
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 */
public class PlaybackSurfaceViewActivity extends AppCompatActivity {
    private static final String TAG = "PlaybackSurfaceView";

    // 播放器实例
    private AliPlayer mAliPlayer;
    // 播放器视图
    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_surface_view);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_basic_playback_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 0: 创建播放器实例
        setupPlayer();

        // Step 1: 初始化视图
        setupPlayerView();

        // Step 2 & Step 3: 设置播放源并开始播放
        startPlayback();
    }

    @Override
    protected void onDestroy() {
        // Step 4: 资源清理
        cleanupPlayer();

        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Step 1: 初始化视图组件
     * <p>
     * 获取 SurfaceView 并设置 SurfaceHolder 回调
     */
    private void setupPlayerView() {
        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (mAliPlayer != null) {
                    mAliPlayer.setSurface(holder.getSurface());
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                if (mAliPlayer != null) {
                    mAliPlayer.surfaceChanged();
                }
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mAliPlayer != null) {
                    mAliPlayer.setSurface(null);
                }
            }
        });

        Log.d(TAG, "[Step 1] 播放器视图初始化完成");
    }

    /**
     * Step 0: 创建播放器实例
     */
    private void setupPlayer() {
        // 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());

        // 可选：推荐使用`播放器单点追查`功能，当使用阿里云播放器 SDK 播放视频发生异常时，可借助单点追查功能针对具体某个用户或某次播放会话的异常播放行为进行全链路追踪，以便您能快速诊断问题原因，可有效改善播放体验治理效率。
        // traceId 值由您自行定义，需为您的用户或用户设备的唯一标识符，例如传入您业务的 userid 或者 IMEI、IDFA 等您业务用户的设备 ID。
        // 传入 traceId 后，埋点日志上报功能开启，后续可以使用播放质量监控、单点追查和视频播放统计功能。
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);

        Log.d(TAG, "[Step 0] 播放器创建完成: " + mAliPlayer);
    }

    /**
     * Step 2: 设置播放源 & Step 3: 开始播放
     */
    private void startPlayback() {
        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        // Step 3: 准备播放
        mAliPlayer.prepare();
        // prepare 以后可以同步调用 start 操作，onPrepared 回调完成后会自动起播
        mAliPlayer.start();

        Log.d(TAG, "[Step 2&3] 开始播放视频: " + Constants.DataSource.SAMPLE_VIDEO_URL);
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

            Log.d(TAG, "[Step 4] 播放器资源清理完成");
        }
    }
}
