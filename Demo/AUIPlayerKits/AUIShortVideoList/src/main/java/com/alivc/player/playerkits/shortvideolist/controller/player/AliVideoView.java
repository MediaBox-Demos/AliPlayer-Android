package com.alivc.player.playerkits.shortvideolist.controller.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.business.trackinfo.AUIVideoTrackInfoPanelView;
import com.alivc.player.playerkits.shortvideolist.business.trackinfo.AUIVideoTrackInfoUtil;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.listener.OnPlayerEventListener;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.VidAuth;

import java.util.List;
import java.util.Locale;

/**
 * Video rendering and playback components
 * <p>
 * bind AliPlayer and TextureView
 */
public class AliVideoView {
    private long mStartTime;
    private final TextureView mTextureView;

    public VideoInfo mVideoInfo;

    private AliPlayer mAliPlayer;
    private int mPlayerState;

    private OnPlayerEventListener mOnPlayerEventListener;
    private AUIVideoTrackInfoPanelView.OnTrackInfoListener mOnTrackInfoListener;

    private boolean mHasPrepared = false;

    private int mSelectedTrackBitrate;
    // default prepare/play start time
    private static final long DEFAULT_PREPARE_PLAY_START_TIME = 0L;

    // 通过接口设置，可以达到精准seek效果，默认是 IPlayer.SeekMode.Inaccurate
    // Through the interface settings, you can achieve accurate seek effect, the default is IPlayer.SeekMode.Inaccurate.
    // The current SDK default is IPlayer.SeekMode.Inaccurate, i.e.: non-accurate seek
    // SeekMode.Inaccurate by default, i.e. not accurate seek.
    // 如果想要精准seek，请设置为 IPlayer.SeekMode.Accurate
    // If you want accurate seek, please set it to IPlayer.SeekMode.Accurate
    private static final IPlayer.SeekMode DEFAULT_SEEK_MODE = IPlayer.SeekMode.Accurate;


    public AliVideoView(Context context) {
        this(context, DEFAULT_PREPARE_PLAY_START_TIME);
    }

    public AliVideoView(Context context, long startTime) {
        //create TextureView
        mTextureView = new TextureView(context);
        // set start time
        mStartTime = Math.max(startTime, 0L);
    }

    private void initListener() {
        mAliPlayer.setOnPreparedListener(() -> {
            mHasPrepared = true;
            if (mOnPlayerEventListener != null) {
                mOnPlayerEventListener.onPrepared(-1);
            }
            // update track info list when player is prepared
            if (mOnTrackInfoListener != null) {
                List<TrackInfo> trackInfoList = mAliPlayer.getMediaInfo().getTrackInfos();
                mOnTrackInfoListener.onTrackInfoListUpdated(AUIVideoTrackInfoUtil.filterVideoTrackInfoList(trackInfoList));
            }
        });

        mAliPlayer.setOnInfoListener(infoBean -> {
            if (mOnPlayerEventListener != null) {
                mOnPlayerEventListener.onInfo(-1, infoBean);
            }
        });

        mAliPlayer.setOnStateChangedListener(i -> {
            SLog.i(this, "PLAYER-CBK-STATE_CHANGED", "STATE: " + mPlayerState + "->" + i);
            mPlayerState = i;
            if (mOnPlayerEventListener != null) {
                mOnPlayerEventListener.onPlayStateChanged(-1, mPlayerState == IPlayer.paused);
            }
        });

        mAliPlayer.setOnRenderingStartListener(() -> {
            SLog.i(this, "PLAYER-CBK-RENDER_START");
            if (mOnPlayerEventListener != null) {
                mOnPlayerEventListener.onRenderingStart(-1, mAliPlayer.getDuration());
            }
        });

        mAliPlayer.setOnCompletionListener(() -> {
            SLog.i(this, "PLAYER-CBK-COMPLETION");
            mHasPrepared = false;
            if (mOnPlayerEventListener != null) {
                mOnPlayerEventListener.onCompletion(-1);
            }
        });

        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                // TODO ErrorCode.ERROR_ARTP_UNKNOWN 后续会变更命名
                if (errorInfo.getCode() == ErrorCode.ERROR_ARTP_UNKNOWN) {
                    Activity activity = (Activity) mTextureView.getContext();
                    if (activity == null) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mTextureView.getContext(), R.string.check_license_tip, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        mAliPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            /**
             * 切换成功xq
             *
             * @param trackInfo 流信息。见{@link TrackInfo}
             */
            /****
             * The stream is switched.
             *
             * @param trackInfo Stream information. See {@link TrackInfo}.
             */
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {
                if (trackInfo == null || trackInfo.mType != TrackInfo.Type.TYPE_VIDEO) {
                    return;
                }
                String formatText = String.format(Locale.getDefault(), "Select Track %s Success~", AUIVideoTrackInfoUtil.getQuality(trackInfo));
                Toast.makeText(mTextureView.getContext(), formatText, Toast.LENGTH_SHORT).show();
            }

            /**
             * 切换失败
             *
             * @param trackInfo 流信息。见{@link TrackInfo}
             * @param errorInfo 错误信息。见{@link ErrorInfo}
             */
            /****
             * Failed to switch the stream.
             *
             * @param trackInfo Stream information. See {@link TrackInfo}.
             * @param errorInfo Error message. See {@link ErrorInfo}.
             */
            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
                if (trackInfo == null || trackInfo.mType != TrackInfo.Type.TYPE_VIDEO) {
                    return;
                }
                String formatText = String.format(Locale.getDefault(), "Select Track %s Failed! Error: %d, %s~", AUIVideoTrackInfoUtil.getQuality(trackInfo), errorInfo.getCode().getValue(), errorInfo.getMsg());
                Toast.makeText(mTextureView.getContext(), formatText, Toast.LENGTH_SHORT).show();
            }
        });

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                SLog.i(this, "PLAYER-CBK-SURFACE_AVAILABLE");
                mAliPlayer.setSurface(new Surface(surfaceTexture));
                SLog.i(this, "PLAYER-API-SET_SURFACE", surfaceTexture);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                mAliPlayer.surfaceChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                SLog.i(this, "PLAYER-CBK-SURFACE_DESTROYED");
                mAliPlayer.setSurface(null);
                SLog.i(this, "PLAYER-API-SET_SURFACE_NULL", surfaceTexture);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });
    }

    /**
     * TextureView add to FrameLayout
     */
    public TextureView initTextureView() {
        SLog.i(this, "INIT_TEXTURE_VIEW");
        //remove
        removeTextureView();
        return mTextureView;
    }

    public void removeTextureView() {
        SLog.i(this, "REM_TEXTURE_VIEW");
        if (mTextureView.getParent() != null) {
            ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
        }
    }

    public void bindData(VideoInfo videoInfo) {
        SLog.i(this, "DATA", mAliPlayer, mVideoInfo + "->" + videoInfo);
        mVideoInfo = videoInfo;
    }

    /**
     * pre-render: Player seekTo(0) after setSurface and prepared
     */

    /**
     * Player set data source and prepare
     */
    public void bindVideoPlayer(boolean forceResume) {
        if (mVideoInfo == null || TextUtils.isEmpty(mVideoInfo.playAuth) || TextUtils.isEmpty(mVideoInfo.videoId)) {
            return;
        }

        mAliPlayer = AliPlayerPool.getInstance().acquire(mVideoInfo);
        assert mAliPlayer != null;

        if (!forceResume && mHasPrepared) {
            SLog.w(this, "REBIND", mAliPlayer, mVideoInfo);
            return;
        }
        SLog.w(this, "BIND", mAliPlayer, mVideoInfo, "FORCE: " + forceResume);

        initListener();

        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(mVideoInfo.videoId);
        vidAuth.setPlayAuth(mVideoInfo.playAuth);

        mAliPlayer.setDataSource(vidAuth);

        mAliPlayer.setStartTime(mStartTime, DEFAULT_SEEK_MODE);
        mStartTime = 0;

        // 设置指定清晰度
        if (mSelectedTrackBitrate > 0) {
            mAliPlayer.setDefaultBandWidth(mSelectedTrackBitrate);
        }
        SLog.i(this, "DefaultBandWidth ", mSelectedTrackBitrate);
        mAliPlayer.prepare();
        SLog.i(this, "PLAYER-API-PREPARE", "VID: " + mVideoInfo.videoId);

    }

    public void unbind() {
        SLog.w(this, "UNBIND", mAliPlayer, mVideoInfo);
        if (mHasPrepared && mAliPlayer != null) {
            SLog.i(this, "PLAYER-API-PAUSE_STOP");
            mAliPlayer.pause();
            mAliPlayer.stop();
        }

        // Recycle player instances immediately if the initial capacity of the AliPlayerPool is set to 2.
        // This helps to manage resources efficiently when the pool can only accommodate a limited number
        // of player instances. Ensure that mVideoInfo is not null before attempting to recycle.
        if (mVideoInfo != null && AliPlayerPool.INITIAL_CAPACITY <= 2) {
            AliPlayerPool.getInstance().recycle(mVideoInfo);
        }

        mHasPrepared = false;
    }

    public boolean isPlaying() {
        return mPlayerState == IPlayer.started;
    }

    public void setOnPlayerListener(OnPlayerEventListener onPlayerEventListener) {
        this.mOnPlayerEventListener = onPlayerEventListener;
    }

    public void setTrackInfoListener(AUIVideoTrackInfoPanelView.OnTrackInfoListener trackInfoListener) {
        this.mOnTrackInfoListener = trackInfoListener;
    }

    public void start() {
        if (mAliPlayer != null) {
            SLog.i(this, "PLAYER-API-START");
            mAliPlayer.start();
        }
    }

    public void pause() {
        if (mAliPlayer != null) {
            SLog.i(this, "PLAYER-API-PAUSE");
            mAliPlayer.pause();
        }
    }

    public void seekTo(long progress) {
        if (mAliPlayer != null) {
            SLog.i(this, "PLAYER-API-SEEK");
            mAliPlayer.seekTo(progress, DEFAULT_SEEK_MODE);
        }
    }

    public void setStartTime(long time) {
        if (mAliPlayer != null) {
            mAliPlayer.setStartTime(time, IPlayer.SeekMode.Accurate);
        }
    }

    public void setLoop(boolean looperStart) {
        if (mAliPlayer != null) {
            SLog.i(this, "PLAYER-API-SET-LOOP");
            mAliPlayer.setLoop(looperStart);
        }
    }

    public void setSpeed(float time) {
        if (time > 0 && mAliPlayer != null) {
            mAliPlayer.setSpeed(time);
        }
    }

    /**
     * 多码率清晰度切换，仅适用于HLS多码率流
     *
     * @param trackInfo 音视频轨
     * @see <a href="https://help.aliyun.com/zh/vod/developer-reference/advanced-features#p-kfm-7bv-9el">网络自适应切换视频清晰度</a>
     * <p>
     * 144p: 256 x 144
     * 240p: 426 x 240
     * 360p: 640 x 360
     * 480p (也称为SD, 标清): 854 x 480
     * 540p: 960 x 540
     * 720p (也称为HD, 高清): 1280 x 720
     * 1080p (也称为Full HD, 全高清): 1920 x 1080
     * 1440p (也称为2K, 普通需注意720p也叫HD，切莫混淆): 2560 x 1440
     * 2160p (也称为4K, 超高清): 3840 x 2160
     * 4320p (也称为8K, 超高清): 7680 x 4320
     */
    public void selectTrackByTrackInfo(TrackInfo trackInfo) {
        if (mAliPlayer != null) {
            mAliPlayer.selectTrack(trackInfo == null ? TrackInfo.AUTO_SELECT_INDEX : trackInfo.getIndex());
            Toast.makeText(mTextureView.getContext(), String.format("Select Track %s~", AUIVideoTrackInfoUtil.getQuality(trackInfo)), Toast.LENGTH_SHORT).show();
            if (trackInfo == null) {
                return;
            }
            if (mSelectedTrackBitrate == trackInfo.getVideoBitrate()) {
                return;
            }
            mSelectedTrackBitrate = trackInfo.getVideoBitrate();
        }
    }

    // set the bandwidth size
    public void setBandWidth(int bandWidth) {
        if (mAliPlayer == null) {
            return; // 提前返回，避免不必要的逻辑执行
        }
        if (mSelectedTrackBitrate == bandWidth) {
            return;
        }
        SLog.i(this, "[API][SET_BANDWIDTH]", mSelectedTrackBitrate + "->" + bandWidth);
        // 如果当前尚未prepare，直接设置trackBitrate，并在prepare前设置setDefaultBandWidth接口来实现指定清晰度
        if (!mHasPrepared) {
            mSelectedTrackBitrate = bandWidth;
            return;
        }
        // 如果当前已经prepare，直接通过selectTrack切换清晰度
        List<TrackInfo> trackInfoList = mAliPlayer.getMediaInfo().getTrackInfos();

        for (TrackInfo trackInfo : trackInfoList) {
            if (trackInfo != null && trackInfo.getVideoBitrate() == bandWidth) {
                selectTrackByTrackInfo(trackInfo);
                break;
            }
        }
    }
}
