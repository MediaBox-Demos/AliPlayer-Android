package com.alivc.player.playerkits.shortvideolist.data;

/**
 * @author junhuiYe
 * @date 2024/9/23
 * @brief
 */
public enum VideoType {
    /**
     * 视频页
     */
    VIDEO("video"),

    /**
     * 广告页
     */
    ADVERTISEMENT("advertise");

    private final String value;

    VideoType(String video) {
        value = video;
    }

    public String getValue() {
        return value;
    }
}
