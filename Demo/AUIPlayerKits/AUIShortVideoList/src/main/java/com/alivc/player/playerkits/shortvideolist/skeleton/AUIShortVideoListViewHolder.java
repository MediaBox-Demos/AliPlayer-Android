package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.business.trackinfo.AUIVideoTrackInfoPanelView;
import com.alivc.player.playerkits.shortvideolist.component.AUIShortVideoSelectionPanel;
import com.alivc.player.playerkits.shortvideolist.controller.player.AliVideoView;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.listener.OnEnterNextPageListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnPanelEventListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnPlayerEventListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnProhibitRefreshListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnSpeedSettingListener;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;
import com.alivc.player.playerkits.shortvideolist.utils.TimeFormatUtils;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.nativeclass.TrackInfo;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放-视频播放ViewHolder
 */
public class AUIShortVideoListViewHolder extends AUIVideoListViewHolder {
    private View mPanelEntranceView;
    private AUIShortVideoSelectionPanel mPanelComponent;
    private AUIVideoTrackInfoPanelView.OnTrackInfoListener mOnTrackInfoListener;

    private OnEnterNextPageListener mOnEnterNextPageListener;
    private OnPanelEventListener mOnPanelEventListener = null;
    private OnProhibitRefreshListener mOnProhibitRefreshListener;

    private final AliVideoView mAliVideoView;

    public AUIShortVideoListViewHolder(View itemView, long startTime) {
        super(itemView);
        initView();
        initCallBack();

        if (startTime != 0) {
            mAliVideoView = new AliVideoView(itemView.getContext(), startTime);
        } else {
            mAliVideoView = new AliVideoView(itemView.getContext());
        }

        mAliVideoView.setOnPlayerListener(new OnPlayerEventListener() {
            @Override
            public void onPrepared(int position) {
                if (mOnPlayerEventListener != null) {
                    mOnPlayerEventListener.onPrepared(getAdapterPosition());
                }
            }

            @Override
            public void onInfo(int position, InfoBean infoBean) {
                if (mOnPlayerEventListener != null) {
                    mOnPlayerEventListener.onInfo(getAdapterPosition(), infoBean);
                }
            }

            @Override
            public void onPlayStateChanged(int position, boolean isPaused) {
                if (mOnPlayerEventListener != null) {
                    mOnPlayerEventListener.onPlayStateChanged(getAdapterPosition(), isPaused);
                }
            }

            @Override
            public void onRenderingStart(int position, long duration) {
                if (mOnPlayerEventListener != null) {
                    position = getAdapterPosition();
                    SLog.i(this, "RENDERING_START", position);
                    mOnPlayerEventListener.onRenderingStart(getAdapterPosition(), duration);
                    seekbarEndTime.setText(TimeFormatUtils.formatMs(duration));
                }
                showCoverImage(false);
            }

            @Override
            public void onCompletion(int position) {
                if (mOnPlayerEventListener != null) {
                    mOnPlayerEventListener.onCompletion(getAdapterPosition());
                }
                if (mOnEnterNextPageListener != null) {
                    mOnEnterNextPageListener.onEnterNextPage(getAdapterPosition() + 1);
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {

            }
        });
        mAliVideoView.setTrackInfoListener(new AUIVideoTrackInfoPanelView.OnTrackInfoListener() {
            @Override
            public void onTrackInfoListUpdated(List<TrackInfo> trackInfoList) {
                mShortVideoSettingPanelPanel.setTrackInfoList(trackInfoList);
            }

            @Override
            public void onTrackInfoSelected(TrackInfo trackInfo) {
            }
        });
        TextureView textureView = mAliVideoView.initTextureView();
        mRootFrameLayout.addView(textureView, 0);
    }

    @Override
    public void onBind(VideoInfo videoInfo) {
        super.onBind(videoInfo);
        showCoverImage(true);
        loadCoverImage(videoInfo);
        mAliVideoView.bindData(videoInfo);
        mAliVideoView.setBandWidth(getSelectBandWidth());
    }

    public boolean isPanelShow() {
        return mPanelComponent.getVisibility() == View.VISIBLE;
    }

    public boolean isSpeedPanelShow() {
        return mShortVideoSettingPanelPanel.getVisibility() == View.VISIBLE;
    }

    public void bind() {
        mAliVideoView.bindVideoPlayer(false);
        mPlayImageView.setVisibility(View.GONE);
    }

    public void unbind() {
        // if page is destroyed, we need to show the cover image, in order to avoid the black screen when slide to pre page
        showCoverImage(true);
        mAliVideoView.unbind();
        mSeekBar.setProgress(0);
    }

    public void rebind() {
        TextureView textureView = mAliVideoView.initTextureView();
        mRootFrameLayout.addView(textureView, 0);
        mAliVideoView.bindVideoPlayer(true);
    }

    public void pause() {
        mAliVideoView.pause();
    }

    public void seek(long playPosition) {
        mAliVideoView.seekTo(playPosition);
    }

    public void setLoop(boolean looperStart) {
        mAliVideoView.setLoop(looperStart);
    }

    public void start() {
        mAliVideoView.start();
    }

    public void setStartTime(long time) {
        mAliVideoView.setStartTime(time);
    }

    public void setEpisodeSelectVideoInfo(VideoInfo selectVideoInfo) {
        mPanelComponent.setEpisodeSelectVideoInfo(selectVideoInfo);
    }

    public AliVideoView getVideoView() {
        return mAliVideoView;
    }

    @Override
    public void changePlayState() {
        super.changePlayState();
        if (mAliVideoView.isPlaying()) {
            mAliVideoView.pause();
            showPlayIcon(true);
        } else {
            mAliVideoView.start();
            showPlayIcon(false);
        }
    }

    private void loadCoverImage(VideoInfo videoInfo) {
        if (!AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY) {
            return;
        }
        if (videoInfo == null || TextUtils.isEmpty(videoInfo.coverUrl)) {
            return;
        }

        String realCoverUrl = AUIShortVideoListUtil.convertURLFromHTTPS2HTTP(videoInfo.coverUrl);
        Glide.with(mCoverImageView.getContext()).load(realCoverUrl).into(mCoverImageView);
        SLog.i(this, "COVER-LOAD", videoInfo);
    }

    private void showCoverImage(boolean enable) {
        if (!AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY) {
            return;
        }
        if (mCoverImageView != null) {
            mCoverImageView.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
        SLog.i(this, "COVER-SHOW", enable);
    }

    private void initView() {
        mPanelEntranceView = itemView.findViewById(R.id.v_panel_entrance);
        mPanelEntranceView.setOnClickListener(v -> {
            if (mOnProhibitRefreshListener != null) {
                mOnProhibitRefreshListener.setOnProhibitRefresh(true);
            }
            showPanel();
        });
        mPanelComponent = itemView.findViewById(R.id.v_panel);
        mVideoItemSettingIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnProhibitRefreshListener != null) {
                    mOnProhibitRefreshListener.setOnProhibitRefresh(true);
                }
                setVisibilityWithAnimation(mShortVideoSettingPanelPanel, true);
                mShortVideoInteractiveComponent.setVisibility(View.GONE);
            }
        });

        mShortVideoSettingPanelPanel.setOnSpeedSettingListener(new OnSpeedSettingListener() {
            @Override
            public void onClickSpeed(float speed) {
                mAliVideoView.setSpeed(speed);
                setVisibilityWithAnimation(mShortVideoSettingPanelPanel, false);
                Toast.makeText(itemView.getContext(), "切换至" + speed + "x倍速", Toast.LENGTH_SHORT).show();
            }
        });
        mShortVideoSettingPanelPanel.setOnTrackInfoListener(new AUIVideoTrackInfoPanelView.OnTrackInfoListener() {
            @Override
            public void onTrackInfoListUpdated(List<TrackInfo> trackInfoList) {
            }

            @Override
            public void onTrackInfoSelected(TrackInfo trackInfo) {
                mShortVideoInteractiveComponent.setVisibility(View.VISIBLE);
                setVisibilityWithAnimation(mShortVideoSettingPanelPanel, false);
                if (trackInfo == null) {
                    return;
                }
                if (mOnTrackInfoListener != null) {
                    mOnTrackInfoListener.onTrackInfoSelected(trackInfo);
                }
                mAliVideoView.selectTrackByTrackInfo(trackInfo);
            }
        });
    }

    private void showPanel() {
        mPanelEntranceView.setVisibility(View.GONE);
        setVisibilityWithAnimation(mPanelComponent, true);
    }

    public void hidePanel() {
        setVisibilityWithAnimation(mPanelComponent, false);
        mPanelEntranceView.setVisibility(View.VISIBLE);
        mShortVideoSettingPanelPanel.setVisibility(View.GONE);
        mShortVideoInteractiveComponent.setVisibility(View.VISIBLE);
    }

    private void initCallBack() {
        mPanelComponent.initListener(new OnPanelEventListener() {
            @Override
            public void onClickRetract() {
                hidePanel();
            }

            @Override
            public void onItemClicked(VideoInfo videoInfo) {
                if (mOnPanelEventListener != null) {
                    mOnPanelEventListener.onItemClicked(videoInfo);
                }
            }
        });
    }

    // Long press to set time speed
    public void showTimeSpeed(boolean mTimeSpeedDisplay) {
        mPlaySpeedDisplayView.setVisibility(mTimeSpeedDisplay ? View.VISIBLE : View.GONE);
        mAliVideoView.setSpeed(mTimeSpeedDisplay ? 2 : 1);
    }

    public boolean hidePanelIfNeed() {
        boolean panelShow = isPanelShow();
        if (panelShow) {
            hidePanel();
        }
        return panelShow;
    }

    public boolean hideSpeedPanel() {
        boolean speedPanelShow = isSpeedPanelShow();
        if (speedPanelShow) {
            setVisibilityWithAnimation(mShortVideoSettingPanelPanel, false);
            mShortVideoInteractiveComponent.setVisibility(View.VISIBLE);
        }
        return speedPanelShow;
    }

    private void setVisibilityWithAnimation(View view, boolean visible) {
        if (view == null) {
            return;
        }
        float fromY = visible ? 1.0f : 0.0f;
        float toY = visible ? 0.0f : 1.0f;
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, toY);
        animation.setDuration(330L);
        view.startAnimation(animation);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setOnEnterNextPageListener(OnEnterNextPageListener onEnterNextPageListener) {
        mOnEnterNextPageListener = onEnterNextPageListener;
    }

    public void setOnProhibitRefreshListener(OnProhibitRefreshListener onProhibitRefreshListener) {
        mOnProhibitRefreshListener = onProhibitRefreshListener;
    }

    public void setOnTrackInfoListener(AUIVideoTrackInfoPanelView.OnTrackInfoListener onTrackInfoListener) {
        mOnTrackInfoListener = onTrackInfoListener;
    }
}