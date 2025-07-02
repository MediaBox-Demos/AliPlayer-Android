package com.alivc.auiplayer.videoepisode.data;

import java.util.Locale;

/**
 * @author keria
 * @date 2023/9/26
 * @brief 短剧常量管理
 */

/****
 * @author keria
 * @date 2023/9/26
 * @brief episode constants management
 */
public class AUIEpisodeConstants {
    /**
     * 私有加密视频源
     *
     * @note 该视频源采用了MP4私有加密功能，仅能在当前APP license下进行播放
     * @note 更多信息请查看模块文档里面，私有加密相关的介绍
     * @see <a href="https://help.aliyun.com/zh/vod/user-guide/alibaba-cloud-proprietary-cryptography">阿里云视频加密（私有加密）</a>
     */
    /****
     * Private encrypted video source
     *
     * @note This video source uses the MP4 private encryption function and can only be played under the current APP license
     * @note For more information, please see the module documentation, private encryption related introduction
     * @see <a href="https://help.aliyun.com/zh/vod/user-guide/alibaba-cloud-proprietary-cryptography">alibaba cloud video encryption encryption (private)</a>
     */
    private static final String ENCRYPTED_EPISODE_URL = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/player/aui_episode_encrypt.json";
    private static final String ENCRYPTED_EPISODE_URL_EN = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/player/aui_episode_encrypt_en.json";

    /**
     * 普通视频源
     */
    /****
     * Normal video source
     */
    private static final String CUSTOM_EPISODE_URL = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/player/aui_episode.json";
    private static final String CUSTOM_EPISODE_URL_EN = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/player/aui_episode_en.json";

    /****
     * Short drama json text link
     *
     * @attention This variable is used to control the episode address of the episode page!!!
     * @note Currently using `Private encrypted video source`, due to the MP4 private encryption characteristic, integrated into your project will play invalid
     * @note If you want to experience the integrated effect, please note to replace the video source address with `Normal video source`
     */
    public static final String EPISODE_JSON_URL = ENCRYPTED_EPISODE_URL;

    /**
     * 短剧剧集json文本链接
     *
     * @param encrypted 是否使用私有加密视频源
     * @return 剧集地址
     * @note 如果您当前使用`私有加密视频源`，由于MP4私有加密特性，集成到您项目工程中将会播放失效
     * @note 如果您想体验集成后的效果，请注意替换视频源地址为`普通视频源`
     */
    public static String getLocalizedEpisodeUrl(boolean encrypted) {
        Locale current = Locale.getDefault();
        String language = current.getLanguage();

        if (language.equals("en")) {
            return encrypted ? ENCRYPTED_EPISODE_URL_EN : CUSTOM_EPISODE_URL_EN;
            // add more cases for different languages
        }

        // use zh as default locale
        return encrypted ? ENCRYPTED_EPISODE_URL : CUSTOM_EPISODE_URL;
    }
}
