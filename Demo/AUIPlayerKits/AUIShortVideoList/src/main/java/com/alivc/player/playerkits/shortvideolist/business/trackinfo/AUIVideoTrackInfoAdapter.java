package com.alivc.player.playerkits.shortvideolist.business.trackinfo;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.R;
import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 多视频轨播放（可变清晰度）适配器
 */
public class AUIVideoTrackInfoAdapter extends RecyclerView.Adapter<AUIVideoTrackInfoAdapter.ViewHolder> {
    private final List<TrackInfo> sTrackInfoList = new ArrayList<>();
    private AUIVideoTrackInfoPanelView.OnTrackInfoListener mOnTrackInfoListener;

    public AUIVideoTrackInfoAdapter(List<TrackInfo> trackInfoList) {
        sTrackInfoList.clear();
        // only supported display and selection tracks when there are multiple tracks; if track info list is empty or size is 1, do nothing
        if (trackInfoList.isEmpty() || trackInfoList.size() == 1) {
            return;
        }
        sTrackInfoList.addAll(trackInfoList);
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_short_video_play_track_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TrackInfo trackInfo = (position == 0) ? null : sTrackInfoList.get(position - 1);
        String qualityText = (position == 0) ? "AUTO" : AUIVideoTrackInfoUtil.getQuality(trackInfo);
        holder.mTrackInfoTv.setText(qualityText);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // only supported display and selection tracks when there are multiple tracks; if track info list is empty or size is 1, do nothing
                if (mOnTrackInfoListener != null && !sTrackInfoList.isEmpty()) {
                    mOnTrackInfoListener.onTrackInfoSelected(trackInfo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sTrackInfoList.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTrackInfoTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTrackInfoTv = itemView.findViewById(R.id.tv_track_info);
        }
    }

    public void setOnTrackInfoListener(AUIVideoTrackInfoPanelView.OnTrackInfoListener listener) {
        mOnTrackInfoListener = listener;
    }
}
