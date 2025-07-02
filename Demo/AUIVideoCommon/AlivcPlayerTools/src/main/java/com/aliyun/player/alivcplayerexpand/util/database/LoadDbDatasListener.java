package com.aliyun.player.alivcplayerexpand.util.database;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;

import java.util.List;

/**
 * 数据库加载数据监听
 *
 * @author hanyu
 */
/****
 * Database load data listening
 *
 * @author hanyu
 */
public interface LoadDbDatasListener {

    /**
     * 数据加载成功
     */
    /****
     * Data load success
     */
    public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList);
}
