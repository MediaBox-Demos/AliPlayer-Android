package com.alivc.player.playerkits.shortvideolist.data;

import java.io.Serializable;

// Copyright © 2025 Alibaba Cloud. All rights reserved.
//
// Author: junHuiYe
// Date: 2025/11/21
// Brief: 剧集首页实体类

// TODO: 新功能发布后，考虑是否删除
public class VideoInfoExtension implements Serializable {
    /**
     * @brief 播放列表中的视频总数。
     * 来源于 playlistExtension.count，可能为 null。
     */
    public int count;

    /**
     * @brief 预览视频的 videoId。
     * 来源于 playlistExtension.previewVideoId，用于首帧预览或封面推荐；可能为 null。
     */
    public String previewVideoId;
}
