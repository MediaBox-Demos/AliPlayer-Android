package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 短视频列表播放DiffCallback
 */
public class AUIVideoListDiffCallback extends DiffUtil.ItemCallback<VideoInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
        return TextUtils.equals(oldItem.videoId, newItem.videoId);
    }

    @SuppressLint("DiffUtilEquals")
    @Override
    public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
        return oldItem.equals(newItem);
    }
}
