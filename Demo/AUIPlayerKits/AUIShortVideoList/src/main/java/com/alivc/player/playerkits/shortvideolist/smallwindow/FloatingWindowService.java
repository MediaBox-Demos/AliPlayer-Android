package com.alivc.player.playerkits.shortvideolist.smallwindow;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;

/**
 * @author junhuiYe
 * @date 2025/7/24 16:31
 * @brief 悬浮窗服务
 */
public class FloatingWindowService extends Service {
    // 小窗视图
    private SmallWindowView mSmallWindowView;
    // 默认出现位置
    private static final int locationDefaultX = 0;

    private static final int locationDefaultY = 100;

    // 贴边边距
    private static final int FLOATING_WINDOW_MARGIN = 20;

    // 悬浮窗尺寸管理器
    private FloatingWindowSizeManager mFloatingWindowSizeManager;
    // 窗口管理器
    private WindowManager mWindowManager;
    // 窗口布局参数
    private WindowManager.LayoutParams mLayoutParams;

    // 悬浮窗视图
    private View mFloatingView;

    // 触摸位置
    private int initialX, initialY;

    private float initialTouchX, initialTouchY;

    private boolean isFloatingViewVisible = false;

    public class MyBinder extends Binder {
        public FloatingWindowService getService() {
            return FloatingWindowService.this;
        }
    }

    private MyBinder mBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化视图
        initView();

        // 悬浮窗拖动
        setupTouchView();
    }

    // 初始化视图
    private void initView() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mFloatingWindowSizeManager = new FloatingWindowSizeManager(this);
        mSmallWindowView = new SmallWindowView(this);
        mFloatingView = mSmallWindowView;
        initLayoutParams();
    }

    // 初始化悬浮窗视图参数
    private void initLayoutParams() {
        mSmallWindowView.setOnSmallWindowVideoSizeListener(new OnSmallWindowVideoSizeListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {
                FloatingWindowSizeManager.WindowSize mWindowSize = mFloatingWindowSizeManager.calculateWindowSize(width, height);
                mLayoutParams = new WindowManager.LayoutParams(mWindowSize.width, mWindowSize.height, getWindowManagerType(), WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
                mLayoutParams.gravity = Gravity.TOP | Gravity.START;
                mLayoutParams.x = locationDefaultX + FLOATING_WINDOW_MARGIN;
                mLayoutParams.y = locationDefaultY;
                showFloatingWindow();
            }

            @Override
            public void onVideoInfoError(String error) {

            }
        });
    }

    private int getWindowManagerType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    // 视图拖动
    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchView() {
        mFloatingView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = mLayoutParams.x;
                    initialY = mLayoutParams.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = Math.abs(event.getRawX() - initialTouchX);
                    float deltaY = Math.abs(event.getRawY() - initialTouchY);

                    if (deltaX > 20 || deltaY > 20) {
                        mLayoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        mLayoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                        // 边界检查
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

                        mLayoutParams.x = Math.max(0, Math.min(mLayoutParams.x, displayMetrics.widthPixels - mFloatingView.getWidth()));
                        mLayoutParams.y = Math.max(0, Math.min(mLayoutParams.y, displayMetrics.heightPixels - mFloatingView.getHeight()));

                        if (isFloatingViewVisible) {
                            mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    // 贴边效果
                    snapToEdge();
                    return false;
            }
            return false;
        });
    }

    private void snapToEdge() {
        // 贴边效果实现
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        if (mLayoutParams.x < screenWidth / 3) {
            // 贴左边
            mLayoutParams.x = FLOATING_WINDOW_MARGIN;
        } else {
            // 贴右边
            mLayoutParams.x = screenWidth - mFloatingView.getWidth() - FLOATING_WINDOW_MARGIN;
        }

        if (isFloatingViewVisible) {
            mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
        }
    }

    // Service启动时调用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleIntent(intent);
        }

        if (!isFloatingViewVisible && mFloatingView != null) {
            try {
                // 添加视图到视图管理器
                mWindowManager.addView(mFloatingView, mLayoutParams);
                isFloatingViewVisible = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    // 处理数据
    private void handleIntent(Intent intent) {
        checkAvailability();

        VideoInfo videoInfo = (VideoInfo) intent.getSerializableExtra(SmallWindowConstants.KEY_VIDEO_INFO);
        long currentPlayPosition = intent.getLongExtra(SmallWindowConstants.KEY_CURRENT_PLAY_POSITION, 0);
        if (mSmallWindowView != null) {
            mSmallWindowView.startPlayback(videoInfo, currentPlayPosition);
        }
    }

    public void handleIntent(VideoInfo videoInfo, long startTime) {
        checkAvailability();
        if (mSmallWindowView != null) {
            mSmallWindowView.startPlayback(videoInfo, startTime);
        }
    }

    // 检查可用性
    private void checkAvailability() {
        // 确保FloatingControllerView可用
        if (mSmallWindowView == null) {
            initView();
        }

        // 检查播放器是否可用，如果不可用则重新初始化
        mSmallWindowView.reinitializePlayer();
    }

    /**
     * 隐藏悬浮窗
     */
    public void hideFloatingWindow() {
        if (isFloatingViewVisible && mFloatingView != null && mWindowManager != null) {
            try {
                mWindowManager.removeView(mFloatingView);
                isFloatingViewVisible = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 销毁播放器
        if (mSmallWindowView != null) {
            mSmallWindowView.destroy();
        }
    }

    /**
     * 显示悬浮窗
     */
    public void showFloatingWindow() {
        if (!isFloatingViewVisible && mFloatingView != null && mWindowManager != null) {
            try {
                mWindowManager.addView(mFloatingView, mLayoutParams);
                isFloatingViewVisible = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isFloatingViewVisible && mFloatingView != null && mWindowManager != null) {
            mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
        }
    }

    // 设置小窗事件监听器
    public void setSmallWindowListener(OnSmallWindowListener onSmallWindowListener) {
        if (mSmallWindowView != null) {
            mSmallWindowView.setSmallWindowListener(onSmallWindowListener);
        }
    }

    public void syncVideoIndex(int videoIndex) {
        if (mSmallWindowView != null) {
            mSmallWindowView.syncVideoIndex(videoIndex);
        }
    }

    @Override
    public void onDestroy() {
        hideFloatingWindow(); // 确保销毁时移除悬浮窗

        // 销毁时才真正释放播放器资源
        if (mSmallWindowView != null) {
            mSmallWindowView.destroy();
            mSmallWindowView = null;
        }

        super.onDestroy();
    }
}
