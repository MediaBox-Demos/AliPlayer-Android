package com.alivc.auiplayer.videoepisode.data;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

import java.util.Formatter;
import java.util.Locale;

/**
 * 短剧单集视频数据
 */
/****
 * single episode video data
 */

public class AUIEpisodeVideoInfo extends VideoInfo {

    /**
     * 视频唯一id
     */
    /****
     * video unique id
     */
    public int videoId;

    /// --------视频详情信息--------

    /**
     * 视频封面图
     */
    /****
     * video cover url
     */
    public String coverUrl;

    /// --------视频播放信息--------

    /**
     * 视频播放时长
     */
    /****
     * video duration
     */
    public int videoDuration;

    /**
     * 视频播放
     */
    /****
     * video play count
     */
    public int videoPlayCount;

    /// --------视频互动信息--------

    /**
     * 点赞状态
     */
    /****
     * like status
     */
    public boolean isLiked;

    /**
     * 点赞数量
     */
    /****
     * like count
     */
    public int likeCount;

    /**
     * 评论数量
     */
    /****
     * comment count
     */
    public int commentCount;

    /**
     * 分享数量
     */
    /****
     * share count
     */
    public int shareCount;

    /**
     * 格式化显示数字
     *
     * @param number 原始数字
     * @return 字符串
     */
    /****
     * format number
     *
     * @param number original number
     * @return string
     */
    public static String formatNumber(int number) {
        if (number < 10000) {
            return String.valueOf(number);
        }
        // TODO: Only Chinese
        return String.format("%.1f万", ((float) number / 10000));
    }

    public static String formatTimeDuration(int videoDuration) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

        int totalSeconds = videoDuration / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;

        formatBuilder.setLength(0);
        return formatter.format("%02d:%02d", minutes, seconds).toString();
    }
}
