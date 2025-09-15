package com.aliyun.player.floatwindow;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.common.utils.ToastUtils;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.videoview.AliDisplayView;

/**
 * @author wyq
 * @date 2025/6/6
 * @brief 悬浮窗播放功能演示 - 阿里云播放器 SDK 最佳实践
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
 * - 调用 start() 方法开始播放
 * <p>
 * Step 4: 资源清理
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 * <p>
 * ==================== 悬浮窗 视图调用步骤 ====================
 * Step 1: 获取 WindowManager
 * WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
 * <p>
 * Step 2: 初始化悬浮窗视图
 * View floatingView = LayoutInflater.from(this).inflate(R.layout.view_float_window, null);
 * <p>
 * Step 3: 添加悬浮窗视图到WindowManager
 * windowManager.addView(floatingView, params);
 * <p>
 * Step 4: 移除悬浮窗视图
 * windowManager.removeView(floatingView);
 * <p>
 */
public class FloatWindowService extends Service {
    // 悬浮窗控制器
    private WindowManager windowManager;
    // 悬浮窗视图
    private View floatingView;

    // 悬浮窗视图参数
    private WindowManager.LayoutParams params;

    // 播放器
    private AliPlayer mAliPlayer;

    // 播放器视图
    private AliDisplayView mAliDisplayView;

    // 窗口起始位置x
    private static final int locationDefaultX = 0;
    // 窗口起始位置x
    private static final int locationDefaultY = 100;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Step 1: 初始化视图
        initView();

        // Step 2: 初始化播放器
        setupPlayer();

        // Step 3:起播播放器
        startupPlayer();
    }


    /**
     * Step 1: 初始化视图&&窗口控制器
     */
    private void initView() {
        // 初始化WindowManager和悬浮窗视图
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingView = LayoutInflater.from(this).inflate(R.layout.view_float_window, null);
        // 播放器视图
        mAliDisplayView = floatingView.findViewById(R.id.ali_display_view);

        // 设置WindowManager.LayoutParams
        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, getWindowManagerType(), WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        // 设置初始位置
        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = locationDefaultX;
        params.y = locationDefaultY;

        // 添加触摸监听器以实现拖动
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            int initialX;
            int initialY;
            float initialTouchX;
            float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });

        // 添加关闭按钮点击监听器
        floatingView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
    }

    /**
     * Step:2 初始化播放器&&绑定视图
     */
    private void setupPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());
        // 播放完成后
        mAliPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                // 播放完成后关闭悬浮窗
                stopSelf();
            }
        });

        if (mAliPlayer == null) {
            return;
        }

        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.showToastLong(errorInfo.getExtra());
            }
        });

        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);
        mAliPlayer.setDisplayView(mAliDisplayView);
    }

    /**
     * 起播 播放器 / Player
     */
    private void startupPlayer() {
        UrlSource source = new UrlSource();
        source.setUri(Constants.DataSource.SAMPLE_VIDEO_URL);

        if (mAliPlayer != null) {
            mAliPlayer.setDataSource(source);
            mAliPlayer.prepare();
            mAliPlayer.start();
        }
    }

    /**
     * 在 Android 中，START_STICKY 是一个用于指定服务的启动模式的常量，与 Service 类的 onStartCommand() 方法一起使用。当您在该方法中返回 START_STICKY 时，它表明您的服务应该在被系统停止后自动重新启动。
     * START_STICKY 的作用：
     * 保证服务继续运行: 当您将 START_STICKY 返回到 onStartCommand() 时，Android 系统会在服务被终止后，尝试在稍后的时间重新启动该服务，直到有新的启动请求为止。
     * 不保存 Intent: 当服务重新启动时，系统会将服务的 Intent 设置为 null，这意味着您无法从之前的 Intent 中恢复任何状态。在这种模式下，您可以在服务重新启动时自定义行为，而无需处理丢失的数据。
     * 适用于长时间运行的服务: START_STICKY 通常被用于那些需要长时间运行的后台服务，例如音乐播放、下载或其他需要持续运行的任务。
     *
     * @param intent  The Intent supplied to {@link android.content.Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (floatingView != null && floatingView.getWindowToken() == null) {
            windowManager.addView(floatingView, params);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // 移除悬浮窗视图
        if (floatingView != null) {
            // 移除悬浮窗
            windowManager.removeView(floatingView);
        }
        if (mAliPlayer != null) {
            // 停止播放器播放
            // mAliPlayer.stop();

            // 销毁播放器（同步）
            // mAliPlayer.release();

            // 销毁播放器（异步）（可选） 作用 = stop + release
            mAliPlayer.releaseAsync();

            // 清除引用
            mAliPlayer = null;
        }
        super.onDestroy();
    }

    // 根据Android版本返回适当的窗口类型
    private int getWindowManagerType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
    }
}