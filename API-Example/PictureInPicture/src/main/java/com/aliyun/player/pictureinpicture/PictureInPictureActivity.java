package com.aliyun.player.pictureinpicture;

import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.videoview.AliDisplayView;

/**
 * @author wyq
 * @date 2025/6/26
 * @brief 画中画功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现画中功能
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 可选：设置 traceId 开启播放器单点追查
 * <p>
 * Step 2: 初始化视图
 * - 加载 AliDisplayView 并设置视图回调
 * - 加载画中画按钮
 * <p>
 * Step 3: 设置播放源
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * <p>
 * Step 4: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法开始播放
 * <p>
 * Step 5: 设置画中画参数并开启画中画
 *  - Rational aspectRatio = new Rational(16, 9); // 画中画的宽高比
 *  - PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
 *  - pipBuilder.setAspectRatio(aspectRatio);
 *  - enterPictureInPictureMode(pipBuilder.build());
 * <p>
 * Step 6: 处理画中画的 UI 显示
 * public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
 *     if (isInPictureInPictureMode){
 *         // hide Other UI
 *     } else {
 *         // show Other UI
 *     }
 * }
 * <p>
 * Step 7: 资源清理
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 */
public class PictureInPictureActivity extends AppCompatActivity {

    private final static String TAG = "PipActivity";
    // 播放器
    private AliPlayer mAliPlayer;
    // 播放器视图
    private AliDisplayView mAliDisplayView;
    // 主动唤出画中画
    private Button mBtnPip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_in_picture);
        Log.e(TAG, "onCreate 创建" + (savedInstanceState != null));

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_pip_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2: 初始化视图组件
        setupPlayerView();

        // Step 3 & Step 4: 设置播放源并开始播放
        startPlayback();
    }

    private void setUIShow(boolean isPipShow) {
        if (getSupportActionBar() != null) {
            if (isPipShow) {
                // 进入画中画模式时的处理
                getSupportActionBar().hide();
                mBtnPip.setVisibility(View.GONE);
                Log.e(TAG, "进入画中画模式");
            } else {
                // 退出画中画模式时的处理
                getSupportActionBar().show();
                mBtnPip.setVisibility(View.VISIBLE);
                Log.e(TAG, "退出画中画模式");
            }
        }
    }

    /**
     * 启用画中画
     */
    private void enterPiPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(16, 9); // 画中画的宽高比
            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(aspectRatio);
            enterPictureInPictureMode(pipBuilder.build());
        }
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
    }

    /**
     * Step 2: 初始化视图组件
     * <p>
     * 获取 AliDisplayView 并设置播放视图类型
     */
    private void setupPlayerView() {
        mAliDisplayView = findViewById(R.id.ali_display_view);
        // 可以通过 setPreferDisplayView() 设置播放视图类型
        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);

        mAliPlayer.setDisplayView(mAliDisplayView);

        mBtnPip = findViewById(R.id.btn_pip);
        mBtnPip.setOnClickListener(view -> enterPiPMode());

        Log.d(TAG, "[Step 2] 播放器视图初始化完成");
    }

    /**
     * Step 3: 设置播放源 & Step 4: 开始播放
     */
    private void startPlayback() {
        // Step 3: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        // Step 4: 准备播放
        mAliPlayer.prepare();
        // prepare 以后可以同步调用 start 操作，onPrepared 回调完成后会自动起播
        mAliPlayer.start();

        Log.d(TAG, "[Step 3&4] 开始播放视频: " + Constants.DataSource.SAMPLE_VIDEO_URL);
    }

    /**
     * Step 5: 资源清理
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

    /**
     * 离开当前activity 时调用
     */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        enterPiPMode();

        Log.e(TAG, "画中画 onUserLeaveHint");
    }

    /**
     * 画中画模式监听，检测是否处于画中画模式
     * @param isInPictureInPictureMode True if the activity is in picture-in-picture mode.
     * @param newConfig The new configuration of the activity with the state
     *                  {@param isInPictureInPictureMode}.
     */
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        setUIShow(isInPictureInPictureMode);
    }

    @Override
    protected void onDestroy() {
        // Step 4: 资源清理
        cleanupPlayer();
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInPictureInPictureMode()) {
                finish();
            }
        }
        Log.e(TAG, "OnStop 停止");
    }
}
