package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.data.VideoType;
import com.alivc.player.playerkits.shortvideolist.listener.OnPanelEventListener;
import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 短剧面板适配器
 */
public class AUIShortVideoPanelAdapter extends RecyclerView.Adapter<AUIShortVideoPanelAdapter.PanelViewHolder> {
    private OnPanelEventListener mOnPanelEventListener = null;
    private VideoInfo mCurrentSelectVideoInfo;

    private final List<VideoInfo> mVideoInfoList = new ArrayList<>();

    public void setData(List<VideoInfo> videoInfoList) {
        mVideoInfoList.clear();
        // TODO: YJH看看是否能优化
        List<VideoInfo> videoInfos = new ArrayList<>();
        for (int i = 0; i < videoInfoList.size(); i++) {
            VideoInfo videoInfo = videoInfoList.get(i);
                videoInfos.add(videoInfo);
        }
        // 将过滤后的列表添加到 mVideoInfoList 中
        mVideoInfoList.addAll(videoInfos);
    }

    public VideoInfo getCurrentVideoInfo() {
        if (mCurrentSelectVideoInfo != null) {
            return mCurrentSelectVideoInfo;
        }
        if (mVideoInfoList != null && !mVideoInfoList.isEmpty()) {
            return mVideoInfoList.get(0);
        }
        return null;
    }

    public void setEpisodeSelectVideoInfo(VideoInfo selectVideoInfo) {
        this.mCurrentSelectVideoInfo = selectVideoInfo;
    }

    @NonNull
    @Override
    public PanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ilr_view_short_video_panel_item, parent, false);
        return new PanelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanelViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position >= mVideoInfoList.size()) {
            return;
        }
        VideoInfo mVideoInfo = mVideoInfoList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPanelEventListener != null) {
                    mOnPanelEventListener.onItemClicked(mVideoInfo);
                }
            }
        });
        holder.bind(position);
        showSelectedEpisode(holder, mVideoInfo);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mVideoInfoList.clear();
    }

    public void setOnSmoothToPositionListener(OnPanelEventListener listener) {
        mOnPanelEventListener = listener;
    }

    @Override
    public int getItemCount() {
        return mVideoInfoList.size();
    }

    public void initListener(OnPanelEventListener listener) {
        mOnPanelEventListener = listener;
    }

    public static class PanelViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPopup = itemView.findViewById(R.id.tv_popup_title);

        public PanelViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(@NonNull Integer popIndex) {
            int index = popIndex + 1;
            mPopup.setText(String.valueOf(index));
        }
    }

    private void showSelectedEpisode(PanelViewHolder holder, VideoInfo selectedVideoInfo) {
        if (holder.mPopup != null && selectedVideoInfo != null) {
            VideoInfo currentVideoInfo = getCurrentVideoInfo();

            if (currentVideoInfo != null) {
                boolean isShowPopUp = AUIShortVideoListUtil.isSameVideoInfo(selectedVideoInfo, getCurrentVideoInfo());
                holder.mPopup.setBackgroundResource(isShowPopUp ?
                        R.drawable.bg_panel_item_select :
                        R.drawable.bg_panel_item);
            } else {
                holder.mPopup.setBackgroundResource(R.drawable.bg_panel_item);
            }
        }
    }
}
