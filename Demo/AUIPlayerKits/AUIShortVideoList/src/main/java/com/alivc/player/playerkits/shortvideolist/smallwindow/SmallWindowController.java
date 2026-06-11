package com.alivc.player.playerkits.shortvideolist.smallwindow;

import android.os.Handler;
import android.os.Looper;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.videoview.AliDisplayView;

public class SmallWindowController {
    private static final long HIDE_DELAY = 3000;

    private SmallWindowView mSmallWindowView;
    private SmallWindowPlayer mSmallWindowPlayer;
    private Handler mHandler;
    private OnSmallWindowListener mListener;

    // 状态管理
    private boolean smallWindowIsShow = false;
    private boolean isPlaying = false;
    private boolean isControlsVisible = true;
    private int startVideoIndex;
    private int currentPosition;

    // 延迟隐藏控件的Runnable
    private final Runnable mHideRunnable = () -> mSmallWindowView.hideControls();

    public SmallWindowController(SmallWindowView view) {
        this.mSmallWindowView = view;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mSmallWindowPlayer = new SmallWindowPlayer(view.getContext());
        initController();
    }

    public void initController() {
        mSmallWindowPlayer.setPlayerListener(new SmallWindowPlayer.PlayerListener() {
            @Override
            public void onPrepared(int duration) {
                handlePlayerPrepared(duration);
            }

            @Override
            public void onProgressUpdate(long position) {
                mSmallWindowView.updateProgress((int) position);
            }

            @Override
            public void onCompletion() {
                handlePlayCompletion();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                if (mListener != null) {
                    mListener.onSmallWindowError(currentPosition, errorInfo);
                }
            }

            @Override
            public void onPause() {
                isPlaying = false;
                mSmallWindowView.updatePlayButton(true); // 显示播放按钮
            }

            @Override
            public void onStart() {
                // 小窗播放&&显示
                smallWindowIsShow = true;
                isPlaying = true;
                mSmallWindowView.updatePlayButton(false); // 显示暂停按钮
            }
        });

        mSmallWindowView.setViewListener(new SmallWindowView.ViewListener() {
            @Override
            public void onPlayPauseClick() {
                togglePlayPause();

                // 播放/暂停后重新开始计时
                showControlsAndResetTimer();
            }

            @Override
            public void onCloseClick() {
                handleClose();
            }

            @Override
            public void onReturnClick() {
                handleReturn();
            }

            @Override
            public void onTouchEvent() {
                // 点击切换显示/隐藏状态
                toggleControlsVisibility();
            }
        });
    }

    /**
     * 切换控件显示/隐藏状态
     */
    private void toggleControlsVisibility() {
        if (isControlsVisible) {
            // 当前显示，点击后隐藏
            hideControls();
        } else {
            // 当前隐藏，点击后显示并开始计时
            showControlsAndResetTimer();
        }
    }

    public void startPlayback(VideoInfo videoInfo, long startTime) {
        mSmallWindowPlayer.setDataSource(videoInfo, startTime);
        // 开始播放时显示控件并开始计时
        showControlsAndResetTimer();
    }

    public void setDisPlayView(AliDisplayView aliDisplayView) {
        mSmallWindowPlayer.setDisplayView(aliDisplayView);
    }

    private void togglePlayPause() {
        if (isPlaying) {
            mSmallWindowPlayer.pause();
        } else {
            mSmallWindowPlayer.start();
        }
    }

    public void showControlsAndResetTimer() {
        showControls();
        resetHideTimer();
    }

    /**
     * 显示控件
     */
    private void showControls() {
        isControlsVisible = true;
        mSmallWindowView.showControls();
    }

    /**
     * 隐藏控件
     */
    public void hideControls() {
        isControlsVisible = false;
        mSmallWindowView.hideControls();
        // 隐藏时取消计时器
        cancelHideTimer();
    }

    /**
     * 重置隐藏计时器
     */
    private void resetHideTimer() {
        // 先取消之前的计时器
        cancelHideTimer();
        // 重新开始计时
        mHandler.postDelayed(mHideRunnable, HIDE_DELAY);
    }

    /**
     * 取消隐藏计时器
     */
    private void cancelHideTimer() {
        mHandler.removeCallbacks(mHideRunnable);
    }

    private void handlePlayerPrepared(int duration) {
        currentPosition = startVideoIndex;
        if (mListener != null) {
            mListener.onSmallWindowPrepared(currentPosition, duration);
        }
        mSmallWindowView.setMaxProgress(duration);

        // 准备完成后显示控件并开始计时
        showControlsAndResetTimer();
    }

    private void handlePlayCompletion() {
        startVideoIndex++;
        currentPosition = startVideoIndex;
        if (mListener != null) {
            mListener.onSmallWindowCompleted(currentPosition);
            VideoInfo videoInfo = mListener.onSmallWindowRequestSwitch(currentPosition);

            if (videoInfo == null) {
                return;
            }
            // 切换到下一个视频
            VideoInfo nextVideo = new VideoInfo();
            nextVideo.videoId = videoInfo.videoId;
            nextVideo.playAuth = videoInfo.playAuth;
            mSmallWindowPlayer.setDataSource(nextVideo, 0);
        }
    }

    private void handleClose() {
        if (mListener != null) {
            mListener.onSmallWindowClosed(currentPosition, mSmallWindowPlayer.getCurrentPosition());
        }
    }

    private void handleReturn() {
        if (mListener != null) {
            mListener.onSmallWindowReturn(currentPosition, mSmallWindowPlayer.getCurrentPosition());
        }
    }

    public void setListener(OnSmallWindowListener listener) {
        this.mListener = listener;
    }

    public void syncVideoIndex(int videoIndex) {
        this.startVideoIndex = videoIndex;
    }

    public void setOnSmallWindowVideoSizeListener(OnSmallWindowVideoSizeListener listener) {
        mSmallWindowPlayer.setOnSmallWindowVideoSizeListener(listener);
    }

    /**
     * 重新初始化播放器
     */
    public void reinitializePlayer() {
        if (!mSmallWindowPlayer.isPlayerAvailable()) {
            mSmallWindowPlayer.reinitializePlayer();
            // 重新设置显示视图
            mSmallWindowPlayer.setDisplayView(mSmallWindowView.getDisplayView());
        }
    }

    /**
     * 检查控制器是否可用
     */
    public boolean isAvailable() {
        return mSmallWindowPlayer != null && mSmallWindowPlayer.isPlayerAvailable();
    }

    public void destroy() {
        cancelHideTimer();

        if (mListener != null && smallWindowIsShow) {
            mListener.onSmallWindowHide(currentPosition, mSmallWindowPlayer.getCurrentPosition());
            smallWindowIsShow = false;
        }
        mSmallWindowPlayer.destroy();
    }
}
