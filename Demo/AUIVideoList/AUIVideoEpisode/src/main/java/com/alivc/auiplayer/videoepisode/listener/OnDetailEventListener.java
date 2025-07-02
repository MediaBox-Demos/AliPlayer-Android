package com.alivc.auiplayer.videoepisode.listener;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;

public interface OnDetailEventListener {
    /**
     * 点击用户昵称
     *
     * @param episodeVideoInfo 短剧数据
     */
    /****
     * Click username
     *
     * @param episodeVideoInfo episode data
     */
    void onClickAuthor(AUIEpisodeVideoInfo episodeVideoInfo);
}
