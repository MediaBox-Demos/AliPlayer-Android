package com.alivc.player.playerkits.shortvideolist.listener;

import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 播放器事件回调监听
 */
public interface OnPlayerEventListener {
    void onPrepared(int position);

    void onInfo(int position, InfoBean infoBean);

    void onPlayStateChanged(int position, boolean isPaused);

    void onRenderingStart(int position, long duration);

    void onCompletion(int position);

    void onError(ErrorInfo errorInfo);
}
