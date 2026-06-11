package com.alivc.player.playerkits.shortvideolist.smallwindow;

import android.content.Context;
import android.text.TextUtils;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.videoview.AliDisplayView;

public class SmallWindowPlayer {
    private AliPlayer mAliPlayer;
    private Context mContext;
    private PlayerListener mPlayerListener;

    private AliDisplayView mDisplayView;

    private OnSmallWindowVideoSizeListener mOnSmallWindowVideoSizeListener;

    public interface PlayerListener {
        void onPrepared(int duration);

        void onProgressUpdate(long position);

        void onCompletion();

        void onError(ErrorInfo errorInfo);

        void onPause();

        void onStart();
    }

    public SmallWindowPlayer(Context context) {
        this.mContext = context;
        setupPlayer();
    }

    private void setupPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(mContext);
        setupPlayerListeners();
    }

    public void setupPlayerListeners() {
        mAliPlayer.setOnPreparedListener(() -> {
            int videoWidth = mAliPlayer.getVideoWidth();
            int videoHeight = mAliPlayer.getVideoHeight();
            if (mOnSmallWindowVideoSizeListener != null) {
                if (videoWidth > 0) {
                    mOnSmallWindowVideoSizeListener.onVideoSizeChanged(videoWidth, videoHeight);
                }
            }
            if (mPlayerListener != null) {
                mPlayerListener.onPrepared((int) mAliPlayer.getDuration());
            }
        });

        mAliPlayer.setOnInfoListener(infoBean -> {
            if (infoBean.getCode() == InfoCode.CurrentPosition && mPlayerListener != null) {
                mPlayerListener.onProgressUpdate(infoBean.getExtraValue());
            }
        });

        mAliPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int newState) {
                switch (newState) {
                    case IPlayer.started:
                        if (mPlayerListener != null) {
                            mPlayerListener.onStart();
                        }
                        break;
                    case IPlayer.paused:
                        if (mPlayerListener != null) {
                            mPlayerListener.onPause();
                        }
                        break;
                }
            }
        });

        mAliPlayer.setOnCompletionListener(() -> {
            if (mPlayerListener != null) {
                mPlayerListener.onCompletion();
            }
        });

        mAliPlayer.setOnErrorListener(errorInfo -> {
            if (mPlayerListener != null) {
                mPlayerListener.onError(errorInfo);
            }
        });
    }

    /**
     * 检查播放器是否可用
     */
    public boolean isPlayerAvailable() {
        return mAliPlayer != null;
    }

    public void setDisplayView(AliDisplayView displayView) {
        this.mDisplayView = displayView; // 保存引用
        if (displayView != null) {
            displayView.setPreferDisplayView(AliDisplayView.DisplayViewType.TextureView);
            if (mAliPlayer != null) {
                mAliPlayer.setDisplayView(displayView);
            }
        }
    }

    public void setDataSource(VideoInfo videoInfo, long startTime) {
        if (mAliPlayer == null || videoInfo == null || TextUtils.isEmpty(videoInfo.videoId)) {
            return;
        }

        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(videoInfo.videoId);
        vidAuth.setPlayAuth(videoInfo.playAuth);
        mAliPlayer.setDataSource(vidAuth);
        mAliPlayer.setStartTime(startTime, IPlayer.SeekMode.Accurate);
        mAliPlayer.setOption(IPlayer.ALLOW_PRE_RENDER, 1);
        mAliPlayer.prepare();
        mAliPlayer.start();
    }

    public void start() {
        if (mAliPlayer != null) {
            mAliPlayer.start();
        }
    }

    public void pause() {
        if (mAliPlayer != null) {
            mAliPlayer.pause();
        }
    }

    public long getCurrentPosition() {
        return mAliPlayer != null ? mAliPlayer.getCurrentPosition() : 0;
    }

    public void setPlayerListener(PlayerListener listener) {
        this.mPlayerListener = listener;
    }

    public void setOnSmallWindowVideoSizeListener(OnSmallWindowVideoSizeListener listener) {
        this.mOnSmallWindowVideoSizeListener = listener;
    }

    /**
     * 重新初始化播放器
     */
    public void reinitializePlayer() {
        if (mAliPlayer == null) {
            setupPlayer();
            if (mDisplayView != null) {
                setDisplayView(mDisplayView);
            }
        }
    }

    public void destroy() {
        if (mAliPlayer != null) {
            mAliPlayer.stop();
            mAliPlayer.release();
            mAliPlayer = null;
        }
    }
}
