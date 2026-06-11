package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.component.AUIShortVideoSettingPanel;
import com.alivc.player.playerkits.shortvideolist.component.AUIShortVideoInteractiveComponent;
import com.alivc.player.playerkits.shortvideolist.business.floatinglayer.AUIVideoTimeFloatingLayer;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.listener.OnViewHolderItemClickListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnSeekChangedListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnPlayerEventListener;
import com.alivc.player.playerkits.shortvideolist.business.playspeed.AUIVideoPlaySpeedDisplayView;
import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.utils.TimeFormatUtils;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放ViewHolder
 */
public abstract class AUIVideoListViewHolder extends RecyclerView.ViewHolder {
    private static boolean mEnableTitle = true;
    private static boolean mEnableAuth = true;
    private static boolean mEnableSeekbar = true;
    protected static boolean mEnablePlayIcon = true;
    protected long mCurrentTimeWithLong = 0;
    protected OnPlayerEventListener mOnPlayerEventListener;
    protected OnViewHolderItemClickListener mListener;
    protected OnSeekChangedListener mSeekBarListener;
    private int mSelectBandWidth;

    public int getSelectBandWidth() {
        return mSelectBandWidth;
    }

    public void setSelectBandWidth(int mSelectBandWidth) {
        this.mSelectBandWidth = mSelectBandWidth;
    }

    public void onBind(VideoInfo videoInfo) {
        mVideoTitleTextView.setText(videoInfo.title);
        mAuthorTextView.setText("@阿里云视频云 MediaBox");
        mAuthorTextView.setText(R.string.Alibaba_clound_video_cloud_MediaBox);
        mVideoTitleTextView.setVisibility(mEnableTitle ? View.VISIBLE : View.GONE);
        mAuthorTextView.setVisibility(mEnableAuth ? View.VISIBLE : View.GONE);
        mSeekBar.setProgress(0);
        mSeekBar.setVisibility(mEnableSeekbar ? View.VISIBLE : View.GONE);
    }

    protected final FrameLayout mRootFrameLayout;
    protected final ImageView mCoverImageView;
    public final AppCompatSeekBar mSeekBar;
    protected final TextView mVideoTitleTextView;
    protected final TextView mAuthorTextView;
    protected final ImageView mPlayImageView;
    protected final ImageView mBackImageView;
    protected final ImageView mVideoItemSettingIv;
    protected final AUIVideoPlaySpeedDisplayView mPlaySpeedDisplayView;
    protected final AUIShortVideoSettingPanel mShortVideoSettingPanelPanel;
    private final AUIVideoTimeFloatingLayer mFloatingLayer;
    protected final AUIShortVideoInteractiveComponent mShortVideoInteractiveComponent;

    private final TextView seekbarCurrentTime;
    protected final TextView seekbarEndTime;

    @SuppressLint("ClickableViewAccessibility")
    public AUIVideoListViewHolder(View itemView) {
        super(itemView);

        mRootFrameLayout = itemView.findViewById(R.id.fm_root);

        mCoverImageView = itemView.findViewById(R.id.iv_cover);
        mCoverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mVideoItemSettingIv = itemView.findViewById(R.id.iv_video_item_setting);

        mSeekBar = itemView.findViewById(R.id.seekbar);
        mVideoTitleTextView = itemView.findViewById(R.id.tv_video_title);
        mAuthorTextView = itemView.findViewById(R.id.tv_author);
        mPlayImageView = itemView.findViewById(R.id.iv_play);
        mBackImageView = itemView.findViewById(R.id.iv_back);
        mPlaySpeedDisplayView = itemView.findViewById(R.id.v_play_speed_display);
        mPlaySpeedDisplayView.setPlaySpeed(2.0f);
        mShortVideoSettingPanelPanel = itemView.findViewById(R.id.v_aui_setting_panel);
        mFloatingLayer = itemView.findViewById(R.id.ic_time_floate_layer_view);
        mShortVideoInteractiveComponent = itemView.findViewById(R.id.iv_interactive);

        seekbarCurrentTime = itemView.findViewById(R.id.seekbar_current_time);
        seekbarEndTime = itemView.findViewById(R.id.seekbar_end_time);

        mSeekBar.setVisibility(mEnableSeekbar ? View.VISIBLE : View.GONE);
        mVideoTitleTextView.setVisibility(mEnableTitle ? View.VISIBLE : View.GONE);
        mAuthorTextView.setVisibility(mEnableAuth ? View.VISIBLE : View.GONE);
        mPlayImageView.setVisibility(View.GONE);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                seekbarCurrentTime.setText(TimeFormatUtils.formatMs(seekBar.getProgress()));
                String mProgressCurrentTime = TimeFormatUtils.formatMs(seekBar.getProgress());
                mCurrentTimeWithLong = seekBar.getProgress();
                mFloatingLayer.setTimeFloatLayerContent(mProgressCurrentTime, seekbarEndTime.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mFloatingLayer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mSeekBarListener != null) {
                    mSeekBarListener.onSeek(getAdapterPosition(), seekBar.getProgress());
                    mFloatingLayer.setVisibility(View.GONE);
                }
            }
        });

        mRootFrameLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition());
            }
        });

        mRootFrameLayout.setOnLongClickListener(v -> {
            if (mListener != null) {
                mListener.onItemLongClick(getAdapterPosition(), true);
            }
            return true;
        });

        mRootFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (mListener != null) {
                            mListener.onItemLongClick(getAdapterPosition(), false);
                        }
                        break;
                }
                return false;
            }
        });

        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onBackPress();
                }
            }
        });
    }

    public void setOnPlayerListener(OnPlayerEventListener listener) {
        mOnPlayerEventListener = listener;
    }

    public void setOnItemClickListener(OnViewHolderItemClickListener listener) {
        mListener = listener;
    }

    public void setOnSeekBarStateChangeListener(OnSeekChangedListener listener) {
        mSeekBarListener = listener;
    }

    public ViewGroup getRootView() {
        return mRootFrameLayout;
    }

    public static void enableTitleTextView(boolean isShown) {
        mEnableTitle = isShown;
    }

    public static void enableAuthTextView(boolean isShown) {
        mEnableAuth = isShown;
    }

    public ProgressBar getSeekBar() {
        return mSeekBar;
    }

    public void showPlayIcon(boolean isShown) {
        if (mEnablePlayIcon) {
            mPlayImageView.setVisibility(isShown ? View.VISIBLE : View.GONE);
        } else {
            mPlayImageView.setVisibility(View.GONE);
        }
    }

    public void changePlayState() {
    }

    private static int dip2px(Context paramContext, float paramFloat) {
        return (int) (0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
    }

    public long getCurrentTimeWithLong() {
        return mCurrentTimeWithLong;
    }
}
