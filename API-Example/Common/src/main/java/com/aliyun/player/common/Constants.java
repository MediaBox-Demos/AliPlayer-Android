package com.aliyun.player.common;

/**
 * @author keria
 * @date 2025/6/3
 * @brief Application constants and configuration values
 * 应用常量和配置值
 */
public final class Constants {

    // 防止实例化
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }

    /**
     * Schema related constants
     * Schema相关常量
     */
    public static final class Schema {
        // Scheme
        private static final String SCHEME = "demo";

        // Hosts
        private static final String HOST_BASIC = "basic";
        private static final String HOST_ADVANCED = "advanced";

        // Basic features paths
        private static final String PATH_PLAYBACK = "/playback";

        // RTS LiveStream paths
        private static final String PATH_RTS = "/rts";

        // PictureInPicture paths
        private static final String PATH_PIP = "/pip";

        //Thumbnail feature paths
        private static final String PATH_THUMBNAIL = "/thumbnail";

        // Complete schema URLs
        public static final String BASIC_PLAYBACK = SCHEME + "://" + HOST_BASIC + PATH_PLAYBACK;

        // RTS schema URLS
        public static final String RTS_LIVE_STREAM = SCHEME + "://" + HOST_BASIC + PATH_RTS;

        public static final String PICTURE_IN_PICTURE = SCHEME + "://" + HOST_ADVANCED + PATH_PIP;

        public static final String THUMBNAIL = SCHEME + "://" + HOST_ADVANCED + PATH_THUMBNAIL;
    }

    /**
     * Data source related constants
     * 数据源相关常量
     */
    public static final class DataSource {
        // URL of the sample video file
        public static final String SAMPLE_VIDEO_URL = "https://alivc-demo-vod.aliyuncs.com/6b357371ef3c45f4a06e2536fd534380/53733986bce75cfc367d7554a47638c0-fd.mp4";

        public static final String SAMPLE_RTS_URL = "";

        public static final String THUMBNAIL_VIDEO_URL = "https://alivc-demo-vod.aliyuncs.com/sv/5f2e5b7f-191dbfe2558/5f2e5b7f-191dbfe2558.mp4";

        public static final String THUMBNAIL_URL = "https://llk-beijng.oss-cn-beijing.aliyuncs.com/vod-e3ddeb/005d826e483171f0bfb316b5feac0102/snapshots/webvtt/15483839-19768706593-1833-2022-301-08227.vtt";
    }
}