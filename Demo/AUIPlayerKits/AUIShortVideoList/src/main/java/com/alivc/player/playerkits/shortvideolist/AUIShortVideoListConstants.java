package com.alivc.player.playerkits.shortvideolist;

/**
 * @author keria
 * @date 2023/9/26
 * @brief 短视频列表播放-常量管理类（外部可配）
 */

/****
 * @author keria
 * @date 2023/9/26
 * @brief short playlist video list constants management (can be configured outside)
 */
public class AUIShortVideoListConstants {

    /**
     * VOD App Server 部署后的接口 API 地址
     * <p>
     * 请将此地址修改为您自己部署的 App Server 地址
     * 客户端将通过该地址访问服务端功能（如鉴权、上传、播放信息获取等）
     */
    private static final String DEFAULT_APP_SERVER_URL = "https://vodappserver-gwzwacvbyf.cn-shanghai.fcapp.run";

    /**
     * 获取播单信息
     */
    public static final String DEFAULT_VIDEO_INFO_LIST_URL = DEFAULT_APP_SERVER_URL + "/appServer/getPlaylistInfo?playListId=";

    /**
     * 获取播单列表信息
     */
    public static final String DEFAULT_PLAYLIST_INFO_LIST_URL = DEFAULT_APP_SERVER_URL + "/appServer/getPlaylistVideos";

    public static final String DEFAULT_VIDEO_ID = "0ebb8c70c42d71f08813f6f6c7490506";

    public static final String PAGE_NO = "1";

    public static final String PAGE_SIZE = "12";

    public static final String SORT_BY = "asc";

    /**
     * player pool capacity, default value is 2
     * <p>
     * can be configured outside; refer to AUIPlayerSettings/AUIBackstage configuration
     */
    public static int PLAYER_POOL_CAPACITY = 2;

    /**
     * enable cover url strategy
     * <p>
     * can be configured outside; refer to AUIPlayerSettings/AUIBackstage configuration
     */
    public static boolean ENABLE_COVER_URL_STRATEGY = true;
}
