package com.alivc.player.playerkits.shortvideolist.listener;

/**
 * @author junhuiYe
 * @date 2024/9/12
 * @brief 短视频列表播放-seek监听回调
 */
public interface OnSeekChangedListener {
    void onSeek(int position, long seekPosition);
}
