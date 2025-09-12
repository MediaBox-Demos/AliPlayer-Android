package com.aliyun.player.livestream;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.videoview.AliDisplayView;

/**
 * @author 叶俊辉
 * @date 2025/9/11
 * @brief 基础直播流播放功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现基础的直播流播放功能，包括清晰度切换
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 0: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 可选：设置 traceId 开启播放器单点追查
 * <p>
 * Step 1: 初始化视图
 * - 加载 AliDisplayView 并设置视图回调
 * - 初始化控制按钮
 * <p>
 * Step 2: 设置播放源
 * - 创建播放数据源对象
 * - 调用 setDataSource() 设置播放数据源
 * <p>
 * Step 3: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法开始播放
 * <p>
 * Step 4: 设置直播流切换功能
 * - 设置清晰度切换按钮监听
 * - 设置流切换回调监听
 * <p>
 * Step 5: 资源清理
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 */
public class BasicLiveStreamActivity extends AppCompatActivity {
    private static final String TAG = "BasicLiveStreamActivity";

    // 播放器实例
    private AliPlayer mAliPlayer;
    // 播放器视图
    private AliDisplayView mAliDisplayView;
    // 清晰度切换按钮
    private Button mSwitchResolutionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_live_stream);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_basic_live_stream_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 0: 创建播放器实例
        setupPlayer();

        // Step 1: 初始化视图
        setupPlayerView();

        // Step 2 & Step 3: 设置播放源并开始播放
        startPlayback();

        // Step 4: 设置直播流切换功能
        setupStreamSwitching();
    }

    @Override
    protected void onDestroy() {
        // Step 5: 资源清理
        cleanupPlayer();

        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
     * Step 1: 初始化视图组件
     * <p>
     * 获取 AliDisplayView 并设置播放视图类型，初始化控制按钮
     */
    private void setupPlayerView() {
        mAliDisplayView = findViewById(R.id.basic_livestream_ali_display_view);
        // 可以通过 setPreferDisplayView() 设置播放视图类型
        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);

        mAliPlayer.setDisplayView(mAliDisplayView);

        // 初始化清晰度切换按钮
        mSwitchResolutionBtn = findViewById(R.id.basic_livestream_switch_resolution);

        // 校验播放地址是否为空
        if (TextUtils.isEmpty(Constants.DataSource.SAMPLE_LIVESTREAM_VIDEO_URL)) {
            Toast.makeText(this, getString(R.string.set_stream_url_first), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "[Step 1] 播放器视图初始化完成");
    }

    /**
     * Step 2: 设置播放源 & Step 3: 开始播放
     */
    private void startPlayback() {
        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_LIVESTREAM_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        // Step 3: 准备播放
        mAliPlayer.prepare();
        // prepare 以后可以同步调用 start 操作，onPrepared 回调完成后会自动起播
        mAliPlayer.start();

        Log.d(TAG, "[Step 2&3] 开始播放直播流: " + Constants.DataSource.SAMPLE_LIVESTREAM_VIDEO_URL);
    }

    /**
     * Step 4: 设置直播流切换功能
     * <p>
     * 设置清晰度切换按钮监听和流切换回调监听
     */
    private void setupStreamSwitching() {
        // 设置清晰度切换按钮点击监听
        mSwitchResolutionBtn.setOnClickListener(view -> {
            mAliPlayer.switchStream(Constants.DataSource.SAMPLE_SWITCH_LIVESTREAM_VIDEO_URL);
        });

        // 设置流切换回调监听
        mAliPlayer.setOnStreamSwitchedListener(new IPlayer.OnStreamSwitchedListener() {
            @Override
            public void onSwitchedSuccess(String url) {
                Log.i(TAG, String.format(getString(R.string.app_common_onSwitchedSuccess), url));
            }

            @Override
            public void onSwitchedFail(String url, ErrorInfo errorInfo) {
                Log.i(TAG, String.format(getString(R.string.app_common_onSwitchedFail), url, errorInfo.getMsg()));
            }
        });

        Log.d(TAG, "[Step 4] 直播流切换功能设置完成");
    }

    /**
     * Step 5: 资源清理
     * <p>
     * 方案1：stop + release，适用于通用场景；释放操作有耗时，会阻塞当前线程，直到资源完全释放。
     * 方案2：releaseAsync，无需手动 stop，适用于短剧等场景；异步释放资源，不阻塞线程，内部已自动调用 stop。
     * 注意：执行 release 或 releaseAsync 后，请不要再对播放器实例进行任何操作。
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 5.1 停止播放
            mAliPlayer.stop();

            // 5.2 销毁播放器实例
            mAliPlayer.release();

            // 5.3 清空引用，避免内存泄漏
            mAliPlayer = null;

            Log.d(TAG, "[Step 5] 播放器资源清理完成");
        }
    }
}
