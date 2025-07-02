package com.aliyun.player.alivcplayerexpand.view.interfaces;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;

/**
 * 定义UI界面通用的操作。
 * 主要实现类有{@link com.aliyun.player.alivcplayerexpand.view.control.ControlView} ,
 * {@link com.aliyun.player.alivcplayerexpand.view.gesture.GestureView}
 */
/****
 * UI interface common operation.
 * Main implementation class is {@link com.aliyun.player.alivcplayerexpand.view.control.ControlView} ,
 * {@link com.aliyun.player.alivcplayerexpand.view.gesture.GestureView}
 */

public interface ViewAction {

    /**
     * 隐藏类型
     */
    /****
     * Hide type
     */
    public enum HideType {
        /**
         * 正常情况下的隐藏
         */
        /****
         * Normal hide
         */
        Normal,
        /**
         * 播放结束的隐藏，比如出错了
         */
        /****
         * End hide when playing ends such as error
         */
        End
    }

    enum ShowType {
        /**
         * 正常情况下的显示
         */
        /****
         * Normal show
         */
        Normal,
        /**
         * 音频模式下常驻显示
         */
        /****
         * Audio mode show
         */
        AudioMode,
        /**
         * 投屏模式下
         */
        /****
         * Screen cast mode
         */
        ScreenCast
    }

    /**
     * 重置
     */
    /****
     * Reset
     */
    public void reset();

    /**
     * 显示
     */
    /****
     * Show
     */
    public void show();

    public void show(ShowType showType);

    /**
     * 隐藏
     *
     * @param hideType 隐藏类型
     */
    /****
     * Hide
     *
     * @param hideType Hide type
     */
    public void hide(HideType hideType);

    /**
     * 设置屏幕全屏情况
     *
     * @param mode {@link AliyunScreenMode#Small}：小屏. {@link AliyunScreenMode#Full}:全屏
     */
    /****
     * Set screen full status
     *
     * @param mode {@link AliyunScreenMode#Small}: small screen. {@link AliyunScreenMode#Full}: full screen
     */
    public void setScreenModeStatus(AliyunScreenMode mode);

}
