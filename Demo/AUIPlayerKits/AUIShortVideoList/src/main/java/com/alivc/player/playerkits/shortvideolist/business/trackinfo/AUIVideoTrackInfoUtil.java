package com.alivc.player.playerkits.shortvideolist.business.trackinfo;

import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author keria
 * @date 2024/9/12
 * @brief 多视频轨播放（可变清晰度）工具类
 */
public class AUIVideoTrackInfoUtil {
    private AUIVideoTrackInfoUtil() {
    }

    /**
     * 定义未知分辨率
     */
    private static final String UNKNOWN_RESOLUTION = "Unknown";

    /**
     * 定义多清晰度可支持的类型
     */
    private static final Set<TrackInfo.Type> VALID_TRACK_TYPES = Set.of(TrackInfo.Type.TYPE_VIDEO, TrackInfo.Type.TYPE_VOD);

    // 定义分辨率与对应宽高的映射
    private static final Map<String, int[]> trackInfoResolutions = new HashMap<>();

    static {
        trackInfoResolutions.put("144P", new int[]{144, 256});
        trackInfoResolutions.put("240P", new int[]{240, 426});
        trackInfoResolutions.put("360P", new int[]{360, 640});
        trackInfoResolutions.put("480P", new int[]{480, 854});
        trackInfoResolutions.put("540P", new int[]{540, 960});
        trackInfoResolutions.put("720P", new int[]{720, 1280});
        trackInfoResolutions.put("1080P", new int[]{1080, 1920});
        trackInfoResolutions.put("1440P", new int[]{1440, 2560});
        trackInfoResolutions.put("2160P", new int[]{2160, 3840});
        trackInfoResolutions.put("4320P", new int[]{4320, 7680});
    }

    /**
     * 过滤视频清晰度轨
     *
     * @param trackInfoList 音视频轨
     * @return 视频清晰度轨
     */
    public static List<TrackInfo> filterVideoTrackInfoList(List<TrackInfo> trackInfoList) {
        List<TrackInfo> videoTrackInfoList = new ArrayList<>(4);
        if (trackInfoList == null || trackInfoList.isEmpty()) {
            return videoTrackInfoList;
        }
        for (TrackInfo trackInfo : trackInfoList) {
            int width = trackInfo.getVideoWidth();
            int height = trackInfo.getVideoHeight();
            if (width <= 0 || height <= 0) {
                continue;
            }
            // 过滤掉非视频轨
            if (!VALID_TRACK_TYPES.contains(trackInfo.getType())) {
                continue;
            }
            videoTrackInfoList.add(trackInfo);
        }
        return videoTrackInfoList;
    }

    public static String getQuality(TrackInfo trackInfo) {
        return trackInfo != null ? findResolution(trackInfo) : "AUTO";
    }

    /**
     * 遍历最接近的清晰度
     *
     * @param trackInfo 视频轨
     * @return 清晰度
     */
    private static String findResolution(TrackInfo trackInfo) {
        // 如果不是视频轨，则返回默认值
        if (trackInfo == null || !VALID_TRACK_TYPES.contains(trackInfo.getType())) {
            return UNKNOWN_RESOLUTION;
        }

        int width = trackInfo.getVideoWidth();
        int height = trackInfo.getVideoHeight();
        if (width <= 0 || height <= 0) {
            return UNKNOWN_RESOLUTION;
        }

        String nearestResolution = UNKNOWN_RESOLUTION;
        int minDifference = Integer.MAX_VALUE;
        for (Map.Entry<String, int[]> entry : trackInfoResolutions.entrySet()) {
            int resWidth = entry.getValue()[0];
            int resHeight = entry.getValue()[1];

            // 计算曼哈顿距离（宽度差 + 高度差）
            int d1 = Math.abs(resWidth - width) + Math.abs(resHeight - height);
            int d2 = Math.abs(resWidth - height) + Math.abs(resHeight - width);
            int difference = Math.min(d1, d2);

            if (difference < minDifference) {
                minDifference = difference;
                nearestResolution = entry.getKey();
            }
        }
        return nearestResolution;
    }
}
