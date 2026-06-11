package com.alivc.player.playerkits.shortvideolist.listener;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放-列表页事件监听回调
 */
public interface OnViewPagerListener {
    void onInitComplete();

    void onPagePeekStart(int position);

    void onPageSelected(int position, boolean autoStart);

    void onPageRelease(int position);
}
