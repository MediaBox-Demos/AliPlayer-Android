package com.alivc.player.playerkits.shortvideolist.listener;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放-item点击监听回调
 */
public interface OnViewHolderItemClickListener {
    void onItemClick(int position);

    void onBackPress();

    void onItemLongClick(int position, boolean touch);
}