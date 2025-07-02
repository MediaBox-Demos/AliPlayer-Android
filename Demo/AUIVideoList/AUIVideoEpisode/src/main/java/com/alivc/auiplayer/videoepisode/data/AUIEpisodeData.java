package com.alivc.auiplayer.videoepisode.data;

import java.io.Serializable;
import java.util.List;

/**
 * @author keria
 * @date 2023/9/26
 * @brief 短剧剧集数据结构
 */
/****
 * @author keria
 * @date 2023/9/26
 * @brief episode anthology data structure
 */
public class AUIEpisodeData implements Serializable {

    /**
     * 短剧剧集唯一ID
     */
    /****
     * Episode series unique ID
     */
    public String id;

    /**
     * 短剧剧集名称
     */
    /****
     * Episode series title
     */
    public String title;

    /**
     * 短剧剧集视频列表
     */
    /****
     * Episode series video list
     */
    public List<AUIEpisodeVideoInfo> list;

    public static int getEpisodeIndex(AUIEpisodeData episodeData, AUIEpisodeVideoInfo episodeVideoInfo) {
        if (episodeData == null || episodeVideoInfo == null) {
            return -1;
        }
        if (episodeData.list == null || episodeData.list.size() == 0) {
            return -1;
        }
        for (AUIEpisodeVideoInfo videoInfo : episodeData.list) {
            if (videoInfo == episodeVideoInfo) {
                return episodeData.list.indexOf(videoInfo);
            }
        }
        return -1;
    }
}
