package com.aliyun.player.multiresolution;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.nativeclass.TrackInfo;

/**
 * Multi-Resolution ViewHolder
 */
public class MultiResolutionViewHolder extends RecyclerView.ViewHolder {
    public MultiResolutionViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @SuppressLint("SetTextI18n")
    public void bindView(@NonNull TrackInfo info, int position, int selectTrackIndex) {
        TextView resolutionTv = itemView.findViewById(R.id.tv_resolution);
        // 设置清晰度文本
        resolutionTv.setText(info.getVideoWidth() + "P");
        // 根据选中状态设置背景颜色
        if (position == selectTrackIndex) {
            resolutionTv.setBackgroundColor(Color.GREEN);
        } else {
            resolutionTv.setBackgroundColor(Color.GRAY);
        }
    }
}
