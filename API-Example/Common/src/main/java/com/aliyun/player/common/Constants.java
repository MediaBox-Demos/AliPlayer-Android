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

        private static final String PATH_LIVESTREAM = "/livestream";
        private static final String PATH_PLAYBACK_SURFACE_VIEW = "/playback_surface_view";
        private static final String PATH_PLAYBACK_TEXTURE_VIEW = "/playback_texture_view";

        // RTS LiveStream paths
        private static final String PATH_RTS = "/rts";

        // PictureInPicture paths
        private static final String PATH_PIP = "/pip";

        //Thumbnail feature paths
        private static final String PATH_THUMBNAIL = "/thumbnail";

        // ExternalSubtitle feature paths
        private static final String PATH_EXTERNAL_SUBTITLE = "/externalSubtitle";
        // Multi-Resolution paths
        private static final String PATH_MULTI_RESOLUTION = "/multiResolution";
        // Preload feature paths
        private static final String PATH_PRELOAD = "/preload";

        // Preload feature paths
        private static final String PATH_DOWNLOAD = "/downloader";

        private static final String PATH_FLOAT_WINDOW_URL = "/float-window";

        // Complete schema URLs
        public static final String BASIC_PLAYBACK = SCHEME + "://" + HOST_BASIC + PATH_PLAYBACK;

        public static final String BASIC_LIVESTREAM = SCHEME + "://" + HOST_BASIC + PATH_LIVESTREAM;

        public static final String BASIC_PLAYBACK_SURFACE_VIEW = SCHEME + "://" + HOST_BASIC + PATH_PLAYBACK_SURFACE_VIEW;
        public static final String BASIC_PLAYBACK_TEXTURE_VIEW = SCHEME + "://" + HOST_BASIC + PATH_PLAYBACK_TEXTURE_VIEW;

        // RTS schema URLS
        public static final String RTS_LIVE_STREAM = SCHEME + "://" + HOST_BASIC + PATH_RTS;

        // Thumbnail schema URL
        public static final String THUMBNAIL = SCHEME + "://" + HOST_ADVANCED + PATH_THUMBNAIL;

        // External subtitle schema URL
        public static final String EXTERNAL_SUBTITLE = SCHEME + "://" + HOST_ADVANCED + PATH_EXTERNAL_SUBTITLE;
        // Picture in picture schema URLS
        public static final String PICTURE_IN_PICTURE = SCHEME + "://" + HOST_ADVANCED + PATH_PIP;

        // MULTI_RESOLUTION URLS
        public static final String MULTI_RESOLUTION = SCHEME + "://" + HOST_ADVANCED + PATH_MULTI_RESOLUTION;

        //PRELOAD_URL URLS
        public static final String PRELOAD_URL = SCHEME + "://" + HOST_ADVANCED + PATH_PRELOAD;

        public static final String FLOAT_WINDOW_URL = SCHEME + "://" + HOST_ADVANCED + PATH_FLOAT_WINDOW_URL;

        // VID_DOWNLOAD URLS
        public static final String DOWNLOADER = SCHEME + "://" + HOST_ADVANCED + PATH_DOWNLOAD;
    }

    /**
     * Data source related constants
     * 数据源相关常量
     */
    public static final class DataSource {
        // URL of the sample video file
        public static final String SAMPLE_VIDEO_URL = "https://alivc-demo-vod.aliyuncs.com/6b357371ef3c45f4a06e2536fd534380/53733986bce75cfc367d7554a47638c0-fd.mp4";

        // URL of the sample livestream video file
        public static final String SAMPLE_LIVESTREAM_VIDEO_URL = "";

        // URL of the sample switch livestream video file
        public static final String SAMPLE_SWITCH_LIVESTREAM_VIDEO_URL = "";

        // URL of the RTS LiveStream
        public static final String SAMPLE_RTS_URL = "";

        // URL of the subtitle file
        public static final String EXT_SUBTITLE = "https://alivc-player.oss-cn-shanghai.aliyuncs.com/API-Example-File/long-video.srt";

        // URL of the sample thumbnail video file
        public static final String THUMBNAIL_VIDEO_URL = "https://alivc-demo-vod.aliyuncs.com/sv/5f2e5b7f-191dbfe2558/5f2e5b7f-191dbfe2558.mp4";

        // URL of the thumbnail file
        public static final String THUMBNAIL_URL = "https://llk-beijng.oss-cn-beijing.aliyuncs.com/vod-e3ddeb/005d826e483171f0bfb316b5feac0102/snapshots/webvtt/15483839-19768706593-1833-2022-301-08227.vtt";

        // URL of the MULTI_RESOLUTION
        public static final String MULTI_RESOLUTION_URL = "https://alivc-demo-vod.aliyuncs.com/d055a20270a671ef9d064531858c0102/cb2edb31f9674fb880462d8068b4cfd5.m3u8";

        // URL of the request playAuth
        public static final String REQUEST_PLAY_AUTH_URL = "https://alivc-demo.aliyuncs.com/player/getVideoPlayAuth?videoId=";

        // Vid of the request playAuth
        public static final String DOWNLOAD_VID = "6609a2f737cb43e1a79ec2bc6aee781b";

        // URL of the preload video file
        public static final String PRELOAD_VID_URL = "https://alivc-demo-vod.aliyuncs.com/59f748948daa4438b42e42db755ae01e/9d44b2b86d334c6b9df649e35ad0240f.m3u8";
    }
}