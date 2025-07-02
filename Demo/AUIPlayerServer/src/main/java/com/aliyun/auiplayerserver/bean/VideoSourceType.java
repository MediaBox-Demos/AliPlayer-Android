package com.aliyun.auiplayerserver.bean;

public enum VideoSourceType {
    /**
     * 选用url播放 Select url to play
     */
    TYPE_URL,
    /**
     * 选用sts方式播放 Select sts to play
     */
    TYPE_STS,
    /**
     * 选用直播的方式播放 Select live streaming sts to play
     */
    TYPE_LIVE,
    /**
     * 错误的视频，不在列表中显示 Wrong video, not displayed in the list
     */
    TYPE_ERROR_NOT_SHOW
}
