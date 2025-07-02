package com.alivc.auiplayer.videoepisode.listener;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;

public interface OnPanelEventListener {
    /**
     * 点击收起
     */
    /****
     * Click to retract
     */
    void onClickRetract();

    /**
     * 点击剧集
     *
     * @param episodeVideoInfo 短剧单集视频数据
     */
    /****
     * Click to episode
     *
     * @param episodeVideoInfo episode info
     */
    void onItemClicked(AUIEpisodeVideoInfo episodeVideoInfo);
}
