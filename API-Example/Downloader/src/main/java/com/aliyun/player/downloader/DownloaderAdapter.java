package com.aliyun.player.downloader;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.nativeclass.TrackInfo;

import java.util.List;

/**
 * @author junhuiYe
 * @date 2025/6/20 17:05
 * @brief 下载列表
 */
public class DownloaderAdapter extends RecyclerView.Adapter<DownloaderAdapter.MyViewHolder> {
    private final List<TrackInfo> mTrackInfoList;
    private int selectedPosition = -1; // 记录当前选中的位置，-1表示没有选中
    private OnItemClickListener onItemClickListener; // 点击事件监听器

    public DownloaderAdapter(List<TrackInfo> mTrackInfoList) {
        this.mTrackInfoList = mTrackInfoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_downloader_item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TrackInfo trackInfo = mTrackInfoList.get(position);
        holder.mVideoDefinition.setText(trackInfo.getVodDefinition());
        holder.mVideoType.setText(trackInfo.getVodFormat());
        holder.mVideoSize.setText(formatSize(trackInfo.getVodFileSize()));

        // 设置选中状态的UI效果
        if (position == selectedPosition) {
            // 选中状态的样式
            holder.itemView.setSelected(true);
            holder.itemView.setBackgroundResource(R.drawable.selected_item_background); // 需要创建选中背景
            holder.mVideoDefinition.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.selected_text_color));
        } else {
            // 未选中状态的样式
            holder.itemView.setSelected(false);
            holder.itemView.setBackgroundResource(R.drawable.normal_item_background); // 需要创建普通背景
            holder.mVideoDefinition.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.normal_text_color));
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;

            // 刷新之前选中的item和当前选中的item
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(selectedPosition);

            // 回调点击事件
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, trackInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrackInfoList.size();
    }

    /**
     * 获取当前选中的位置
     */
    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * 设置选中的位置
     */
    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;

        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    /**
     * 获取当前选中的TrackInfo
     */
    public TrackInfo getSelectedTrackInfo() {
        if (selectedPosition >= 0 && selectedPosition < mTrackInfoList.size()) {
            return mTrackInfoList.get(selectedPosition);
        }
        return null;
    }

    /**
     * 设置点击事件监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * 点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position, TrackInfo trackInfo);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mVideoDefinition;
        private final TextView mVideoType;
        private final TextView mVideoSize;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mVideoDefinition = itemView.findViewById(R.id.definition);
            mVideoType = itemView.findViewById(R.id.videoType);
            mVideoSize = itemView.findViewById(R.id.videoSize);
        }
    }

    /**
     * 格式化大小
     */
    private static String formatSize(long size) {
        int kb = (int) (size / 1024f);
        if (kb < 1024) {
            return kb + "KB";
        }

        int mb = (int) (kb / 1024f);
        return mb + "MB";
    }
}