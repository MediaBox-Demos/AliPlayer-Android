package com.aliyun.player.alivcplayerexpand.util.database;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;

import java.util.List;

public interface LoadDbTvListDatasListenerr {

    /**
     * 查询对应tvid电视剧列表
     */
    /****
     * Query the corresponding tv list corresponding to the tvid
     */
    void onLoadTvListSuccess(List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos);
}
