package com.alivc.player.playerkits.shortvideolist.data;

// Copyright © 2025 Alibaba Cloud. All rights reserved.
//
// Author: junHuiYe
// Date: 2025/11/20
// Brief: 剧集请求体
public class PlayInfoListRequestParams {
    /**
     * @brief 展示页面
     * 默认1，从首页展示。
     */
    public String pageNo;

    /**
     * @brief 展示项
     * 默认10，展示数量。
     */
    public String pageSize;

    /**
     * @brief 排序
     * desc 按时间降序（默认） asc 按时间升序
     */
    public String sortBy;

    public PlayInfoListRequestParams(String pageNo, String pageSize, String sortBy) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
    }
}
