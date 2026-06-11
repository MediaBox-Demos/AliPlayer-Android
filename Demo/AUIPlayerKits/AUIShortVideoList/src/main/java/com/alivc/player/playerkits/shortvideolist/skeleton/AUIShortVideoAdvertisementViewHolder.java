package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.annotation.SuppressLint;
import android.view.View;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 广告页ViewHolder
 */
public class AUIShortVideoAdvertisementViewHolder extends AUIVideoListViewHolder {
    @SuppressLint("ClickableViewAccessibility")
    public AUIShortVideoAdvertisementViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(VideoInfo videoInfo) {
        super.onBind(videoInfo);
    }
}
