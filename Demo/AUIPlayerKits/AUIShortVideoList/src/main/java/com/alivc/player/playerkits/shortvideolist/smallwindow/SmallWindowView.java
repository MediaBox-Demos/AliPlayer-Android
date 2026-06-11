package com.alivc.player.playerkits.shortvideolist.smallwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.aliyun.player.videoview.AliDisplayView;

/**
 * @author junhuiYe
 * @date 2025/8/13
 * @brief 小窗视图 支持动态尺寸
 */

public class SmallWindowView extends FrameLayout {
    // 悬浮窗视图
    private View mFloatingView;

    // 播放器状态按钮
    private ImageView isPlayingBtn;

    // 拖动按钮
    private SeekBar mSeekBar;

    // 关闭按钮
    private FrameLayout mSmallWindowCloseBtn;

    // 返回按钮
    private FrameLayout mSmallWindowReturnBtn;

    private AliDisplayView mAliDisplayView;

    // 视图监听
    private ViewListener mViewListener;

    private SmallWindowController mController;

    public interface ViewListener {
        void onPlayPauseClick();

        void onCloseClick();

        void onReturnClick();

        void onTouchEvent();
    }

    public SmallWindowView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context mContext) {
        // 创建Controller
        mController = new SmallWindowController(this);
        mFloatingView = LayoutInflater.from(mContext).inflate(R.layout.ilr_view_small_window, this, false);
        initViews();
        setupClickListeners();
        addView(mFloatingView);

        // 初始状态：显示控件
        mController.showControlsAndResetTimer();
    }

    private void initViews() {
        isPlayingBtn = mFloatingView.findViewById(R.id.floating_isPlaying);
        mAliDisplayView = mFloatingView.findViewById(R.id.ali_display_view);
        mController.setDisPlayView(mAliDisplayView);
        mSmallWindowCloseBtn = mFloatingView.findViewById(R.id.pip_close);
        mSmallWindowReturnBtn = mFloatingView.findViewById(R.id.pip_open);
        mSeekBar = mFloatingView.findViewById(R.id.floating_seekBar);
        // 禁止seek滑动
        mSeekBar.setOnTouchListener((v, event) -> true);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupClickListeners() {
        mFloatingView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                // 只在按下时触发，避免重复触发
                if (mViewListener != null) {
                    mViewListener.onTouchEvent();
                }
            }
            return false;
        });

        isPlayingBtn.setOnClickListener(v -> {
            if (mViewListener != null) {
                mViewListener.onPlayPauseClick();
            }
        });

        mSmallWindowCloseBtn.setOnClickListener(v -> {
            if (mViewListener != null) {
                mViewListener.onCloseClick();
            }
        });

        mSmallWindowReturnBtn.setOnClickListener(v -> {
            if (mViewListener != null) {
                mViewListener.onReturnClick();
            }
        });
    }

    // UI更新方法
    public void updatePlayButton(boolean isPaused) {
        isPlayingBtn.setImageResource(isPaused ?
                R.drawable.ic_video_start_icon : R.drawable.ic_video_pause_icon);
    }

    public void updateProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    public void setMaxProgress(int max) {
        mSeekBar.setMax(max);
    }

    public void showControls() {
        setControlsVisibility(View.VISIBLE);
    }

    public void hideControls() {
        setControlsVisibility(View.GONE);
    }

    private void setControlsVisibility(int visibility) {
        isPlayingBtn.setVisibility(visibility);
        mSmallWindowCloseBtn.setVisibility(visibility);
        mSmallWindowReturnBtn.setVisibility(visibility);
        mSeekBar.setVisibility(visibility);
    }

    public AliDisplayView getDisplayView() {
        return mAliDisplayView;
    }

    public void setViewListener(ViewListener listener) {
        this.mViewListener = listener;
    }

    public void startPlayback(VideoInfo videoInfo, long startTime) {
        mController.startPlayback(videoInfo, startTime);
    }

    public void setSmallWindowListener(OnSmallWindowListener listener) {
        mController.setListener(listener);
    }

    public void syncVideoIndex(int videoIndex) {
        mController.syncVideoIndex(videoIndex);
    }

    public void setOnSmallWindowVideoSizeListener(OnSmallWindowVideoSizeListener listener){
        mController.setOnSmallWindowVideoSizeListener(listener);
    }

    /**
     * 重新初始化播放器
     */
    public void reinitializePlayer() {
        if (mController != null && !isAvailable()) {
            mController.reinitializePlayer();
        }
    }

    /**
     * 检查是否可用
     */
    public boolean isAvailable() {
        return mController != null && mController.isAvailable();
    }

    public void destroy() {
        mController.destroy();
    }
}
