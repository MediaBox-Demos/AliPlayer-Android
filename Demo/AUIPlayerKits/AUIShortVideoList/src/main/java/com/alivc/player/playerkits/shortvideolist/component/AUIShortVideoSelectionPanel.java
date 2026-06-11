package com.alivc.player.playerkits.shortvideolist.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIShortVideoPanelAdapter;
import com.alivc.player.playerkits.shortvideolist.listener.OnPanelEventListener;

import java.util.List;

/**
 * @author keria
 * @date 2023/8/29
 * @brief 短视频列表播放-选集列表组件
 */
public class AUIShortVideoSelectionPanel extends FrameLayout {
    private OnPanelEventListener mOnPanelEventListener;
    private AUIShortVideoPanelAdapter mAdapter;
    private ImageView mRetractImageView;
    private RecyclerView mRecyclerView;

    public AUIShortVideoSelectionPanel(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIShortVideoSelectionPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIShortVideoSelectionPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_short_video_selection_panel, this);
        mRetractImageView = view.findViewById(R.id.iv_retract);
        mRetractImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPanelEventListener != null) {
                    mOnPanelEventListener.onClickRetract();
                }
            }
        });
        mAdapter = new AUIShortVideoPanelAdapter();
        mRecyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 6);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setDrawingCacheEnabled(false);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.initListener(mOnPanelEventListener);
    }

    public void initListener(OnPanelEventListener listener) {
        mOnPanelEventListener = listener;
        mAdapter.initListener(mOnPanelEventListener);
    }

    public void setOnClickListener(OnPanelEventListener onClickListener) {
        mAdapter.setOnSmoothToPositionListener(onClickListener);
    }

    public void setEpisodeSelectVideoInfo(VideoInfo selectVideoInfo) {
        mAdapter.setEpisodeSelectVideoInfo(selectVideoInfo);
        mAdapter.notifyDataSetChanged();
    }

    public void setVideoInfoList(List<VideoInfo> videoInfoList) {
        mAdapter.setData(videoInfoList);
        mAdapter.notifyDataSetChanged();
    }
}
