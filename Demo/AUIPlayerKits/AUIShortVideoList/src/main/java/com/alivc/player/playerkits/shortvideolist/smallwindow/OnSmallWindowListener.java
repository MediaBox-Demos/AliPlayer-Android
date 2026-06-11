package com.alivc.player.playerkits.shortvideolist.smallwindow;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.aliyun.player.bean.ErrorInfo;

/**
 * @author keria
 * @date 2025/8/12
 * @brief 小窗与控制器之间的事件回调接口
 * <p>
 * SmallWindowView -> ListController
 * 用于通知控制器小窗的各种状态与用户操作
 */
public interface OnSmallWindowListener {
    /**
     * 小窗已准备好播放指定剧集
     *
     * @param index      剧集索引
     * @param durationMs 视频总时长（毫秒）
     */
    void onSmallWindowPrepared(int index, long durationMs);

    /**
     * 小窗播放完成
     *
     * @param index 当前播放的剧集索引
     */
    void onSmallWindowCompleted(int index);

    /**
     * 小窗请求切换到新的剧集
     *
     * @param newIndex 新的剧集索引
     */
    VideoInfo onSmallWindowRequestSwitch(int newIndex);

    /**
     * 小窗被用户关闭
     *
     * @param index        当前播放的剧集索引
     * @param playPosition 当前播放的位置
     */
    void onSmallWindowClosed(int index, long playPosition);

    /**
     * 小窗播放出现错误
     *
     * @param index     当前播放的剧集索引
     * @param errorInfo 错误信息
     */
    void onSmallWindowError(int index, ErrorInfo errorInfo);

    /**
     * 小窗播放返回主界面
     *
     * @param index        当前播放的剧集索引
     * @param playPosition 当前播放的时间位置
     */
    void onSmallWindowReturn(int index, long playPosition);


    /**
     * 小窗播放返回主界面
     *
     * @param index        当前播放的剧集索引
     * @param playPosition 当前播放的时间位置
     */
    void onSmallWindowHide(int index, long playPosition);
}
