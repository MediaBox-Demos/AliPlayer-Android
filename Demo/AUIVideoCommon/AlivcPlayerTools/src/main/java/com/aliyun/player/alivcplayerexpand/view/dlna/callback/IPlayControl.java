package com.aliyun.player.alivcplayerexpand.view.dlna.callback;

import androidx.annotation.Nullable;

/**
 * 对视频的控制操作定义
 */
/****
 * The control operation definition of the video.
 */
public interface IPlayControl {

    /**
     * 播放一个新片源
     *
     * @param url   片源地址
     */
    /****
     * Play from a new source
     *
     * @param url   Source address
     */
    void playNew(String url, @Nullable ControlCallback callback);

    /**
     * 播放
     */
    /****
     * Play
     */
    void play(@Nullable ControlCallback callback);

    /**
     * 暂停
     */
    /****
     * Pause
     */
    void pause(@Nullable ControlCallback callback);

    /**
     * 停止
     */
    /****
     * Stop
     */
    void stop(@Nullable ControlCallback callback);

    /**
     * 视频 seek
     *
     * @param pos   seek到的位置(单位:毫秒)
     */
    /****
     * Video seek
     *
     * @param pos   Seek to this position (unit: milliseconds)
     */
    void seek(int pos, @Nullable ControlCallback callback);

    /**
     * 设置音量
     *
     * @param pos   音量值，最大为 100，最小为 0
     */
    /****
     * Set volume
     *
     * @param pos   Volume value, the maximum is 100, the minimum is 0
     */
    void setVolume(int pos, @Nullable ControlCallback callback);

    /**
     * 设置静音
     *
     * @param desiredMute   是否静音
     */
    /****
     * Set mute
     *
     * @param desiredMute   Whether to mute
     */
    void setMute(boolean desiredMute, @Nullable ControlCallback callback);

    /**
     * 获取tv进度
     */
    /****
     * Get tv progress
     */
    void getPositionInfo(@Nullable ControlReceiveCallback callback);

    /**
     * 获取音量
     */
    /****
     * Get volume
     */
    void getVolume(@Nullable ControlReceiveCallback callback);

    /**
     * 获取播放状态
     */
    /****
     * Get playback status
     */
    void getTransportInfo(@Nullable ControlReceiveCallback callback);
}
