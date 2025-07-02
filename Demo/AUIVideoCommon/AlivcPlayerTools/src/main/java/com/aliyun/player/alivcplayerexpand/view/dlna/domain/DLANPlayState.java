package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 连接设备状态
 */
/****
 * Connected device status
 */

public class DLANPlayState {
    /** 播放状态 */
    /** Playback status */
    public static final int PLAY = 1;
    /** 暂停状态 */
    /** Pause status */
    public static final int PAUSE = 2;
    /** 停止状态 */
    /** Stop status */
    public static final int STOP = 3;
    /** 转菊花状态 */
    /** Buffer status */
    public static final int BUFFER = 4;
    /** 投放失败 */
    /** Playback failure */
    public static final int ERROR = 5;

    /** 设备状态 */
    /** Device status */
    @IntDef({PLAY, PAUSE,STOP, BUFFER, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DLANPlayStates {}


    // 以下不算设备状态, 只是常量
    // The following are not device states, just constants
    /** 主动轮询获取播放进度(在远程设备不支持播放进度回传时使用) */
    /** Polling for remote device playback progress (used when remote device does not support playback progress reporting) */
    public static final int GET_POSITION_POLING = 6;
    /** 远程设备播放进度回传 */
    /** Remote device playback progress reporting */
    public static final int POSITION_CALLBACK = 7;
    /** 投屏端播放完成 */
    /** Casting device playback completion */
    public static final int PLAY_COMPLETE = 8;
}
