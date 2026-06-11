package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.data.VideoType;
import com.alivc.player.playerkits.shortvideolist.listener.OnPlayerEventListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnSeekChangedListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnViewHolderItemClickListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnViewPagerListener;

import java.util.List;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放适配器
 */
public abstract class AUIVideoListAdapter extends ListAdapter<VideoInfo, ViewHolder> {
    protected OnViewPagerListener mOnViewPagerListener;
    private OnPlayerEventListener mOnPlayerEventListener;
    private OnViewHolderItemClickListener mOnItemClickListener;
    private OnSeekChangedListener mSeekBarListener;
    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_ADVERTISEMENT = 1;
    private long startPlayPosition;
    private int mSelectBandWidth;

    public void setSelectBandWidth(int mSelectBandWidth) {
        this.mSelectBandWidth = mSelectBandWidth;
    }

    public long getStartPlayPosition() {
        return startPlayPosition;
    }

    public void setStartPlayPosition(long startPlayPosition) {
        this.startPlayPosition = startPlayPosition;
    }

    protected AUIVideoListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADVERTISEMENT) {
            return advertisementCreateViewHolder(parent, viewType);
        } else if (viewType == TYPE_VIDEO) {
            return customCreateViewHolder(parent, viewType);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoInfo videoInfo = getItem(position);
        if (holder instanceof AUIVideoListViewHolder) {
            ((AUIVideoListViewHolder) holder).setSelectBandWidth(mSelectBandWidth);
            ((AUIVideoListViewHolder) holder).onBind(videoInfo);
            ((AUIVideoListViewHolder) holder).setOnItemClickListener(mOnItemClickListener);
            ((AUIVideoListViewHolder) holder).setOnSeekBarStateChangeListener(mSeekBarListener);
            ((AUIVideoListViewHolder) holder).setOnPlayerListener(mOnPlayerEventListener);
        }
    }

    public abstract AUIVideoListViewHolder customCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    public abstract AUIShortVideoAdvertisementViewHolder advertisementCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    public void setOnPlayerListener(OnPlayerEventListener listener) {
        mOnPlayerEventListener = listener;
    }

    public void setOnItemClickListener(OnViewHolderItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnSeekBarStateChangeListener(OnSeekChangedListener listener) {
        mSeekBarListener = listener;
    }

    public void setOnViewPagerListener(OnViewPagerListener listener) {
        this.mOnViewPagerListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        List<VideoInfo> currentList = getCurrentList();
        if (currentList.isEmpty()) {
            return TYPE_VIDEO;
        }
        return TYPE_VIDEO;
    }
}