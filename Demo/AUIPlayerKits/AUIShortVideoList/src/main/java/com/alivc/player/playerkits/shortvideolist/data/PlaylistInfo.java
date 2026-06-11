package com.alivc.player.playerkits.shortvideolist.data;
// Copyright © 2025 Alibaba Cloud. All rights reserved.
//
// Author: junHuiYe
// Date: 2025/11/20
// Brief: 剧集单个列表实体类

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public final class PlaylistInfo implements Serializable {
    /**
     * @brief 剧集唯一标识 ID。
     * 用于唯一标识一个短剧资源。
     */
    public String playlistId;

    /**
     * @brief 剧集名称（主标题）。
     * 用于在 UI 中展示短剧的正式名称。
     */
    public String playlistName;

    /**
     * @brief 列表描述。
     * 用于在 UI 中展示列表的描述信息。
     */
    public String playlistDescription;

    /**
     * @brief 列表状态
     * 用于处理列表的状态
     */
    public String playlistStatus;

    /**
     * @brief 列表标签
     * 用于在 UI 中展示不同列表的标签信息
     */
    public String playlistTags;

    /**
     * @brief 剧集封面图片 URL。
     * 用于在列表或详情页展示短剧的封面图。
     */
    public String playlistCoverUrl;

    /**
     * @brief 排序规则
     * asc（默认升序） desc 降序
     */
    public String playlistOrderBy;

    /**
     * @brief 播放列表中的拓展项。
     * 用户自定义，可以为null。
     */
    public String playlistExtension;

    /**
     * @brief 列表创建时间。
     * 创建时自动生成。
     */
    public String createTime;

    /**
     * @brief 列表最后修改时间。
     * 修改时自动生成。
     */
    public String modifyTime;

    /**
     * @brief 本次接口请求id
     * 可用于该次接口问题排查
     */
    public String requestId;

    /**
     * @brief 视频信息列表
     * 该播放列表包含的视频列表
     */
    public List<VideoInfo> playlistVideos;

    @NonNull
    @Override
    public String toString() {
        return "PlaylistInfo{" +
                "playlistId='" + playlistId + '\'' +
                ", playlistName='" + playlistName + '\'' +
                ", playlistDescription='" + playlistDescription + '\'' +
                ", playlistStatus='" + playlistStatus + '\'' +
                ", playlistTags='" + playlistTags + '\'' +
                ", playlistCoverUrl='" + playlistCoverUrl + '\'' +
                ", playlistOrderBy='" + playlistOrderBy + '\'' +
                ", playlistExtension='" + playlistExtension + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                ", requestId='" + requestId + '\'' +
                ", playlistVideos=" + playlistVideos +
                '}';
    }
}
