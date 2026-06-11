package com.alivc.player.playerkits.shortvideolist.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
// Copyright © 2025 Alibaba Cloud. All rights reserved.
//
// Author: junHuiYe
// Date: 2025/11/20
// Brief: 剧集实体类

public class VideoInfo implements Serializable {
    /**
     * @brief 剧集中该视频的唯一标识 ID（业务侧定义）。
     * 由业务系统分配，用于标识某部短剧中的一集视频，通常与剧集上下文绑定。
     */
    public String playlistId;

    /**
     * @brief 视频资源的唯一标识 ID（平台侧 ID）。
     * 由视频平台（如阿里云点播）分配的全局唯一视频 ID，用于播放、管理等底层操作。
     */
    public String videoId;

    /**
     * @brief 视频标题。
     * 用于在 UI 中展示该集视频的名称或副标题，例如“第3集：真相揭晓”。
     */
    public String title;

    /**
     * @brief 视频描述信息。
     * 对视频内容的简要介绍或剧情概要。
     */
    public String description;

    /**
     * @brief 视频封面图地址。
     * 用于在列表或详情页展示视频缩略图，通常为 CDN 加速的图片 URL。
     */
    public String coverUrl;

    /**
     * @brief 播放凭证（PlayAuth）。
     * 阿里云点播服务所需的临时播放授权字符串，适用于 vidAuth 播放模式；可能为 nil。
     */
    public String playAuth;

    @NonNull
    @Override
    public String toString() {
        return "VideoInfo{" +
                "playlistId='" + playlistId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", playAuth='" + playAuth + '\'' +
                '}';
    }
}
