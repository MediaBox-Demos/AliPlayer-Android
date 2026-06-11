package com.alivc.player.playerkits.shortvideolist.data;
// Copyright © 2025 Alibaba Cloud. All rights reserved.
//
// Author: junHuiYe
// Date: 2025/11/20
// Brief: 榜单信息实体类

import androidx.annotation.NonNull;

import java.io.Serializable;

public class RankInfo implements Serializable {
    /**
     * @brief 榜单唯一标识 ID。
     * 用于唯一标识一个榜单，通常由服务端生成并返回，不可重复。
     */
    public String rankId;

    /**
     * @brief 榜单名称。
     * 榜单的可读性标题，例如“热播榜”、“新剧推荐”等，用于 UI 展示。
     */
    public String rankName;

    /**
     * @brief 榜单描述。
     * 对榜单内容或规则的简要说明，例如“根据播放量实时更新”
     */
    public String description;

    @NonNull
    @Override
    public String toString() {
        return "RankInfo{" +
                "rankId='" + rankId + '\'' +
                ", rankName='" + rankName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
