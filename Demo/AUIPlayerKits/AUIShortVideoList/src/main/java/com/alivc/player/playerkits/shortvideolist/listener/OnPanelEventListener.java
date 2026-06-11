package com.alivc.player.playerkits.shortvideolist.listener;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 选集列表事件监听
 */
public interface OnPanelEventListener {
    /**
     * 点击收起
     */
    void onClickRetract();

    /**
     * 点击剧集
     *
     * @param videoInfo 单集视频数据
     */
    void onItemClicked(VideoInfo videoInfo);
}
