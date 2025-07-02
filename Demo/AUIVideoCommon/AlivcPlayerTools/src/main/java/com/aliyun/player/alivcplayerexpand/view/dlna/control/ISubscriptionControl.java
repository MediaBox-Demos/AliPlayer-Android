package com.aliyun.player.alivcplayerexpand.view.dlna.control;

import android.content.Context;

import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;


/**
 * tv端回调
 */
/****
 * The TV side callback
 */

public interface ISubscriptionControl<T> {

    /**
     * 监听投屏端 AVTransport 回调
     */
    /****
     * Listen to the AVTransport callback of the screen casting end
     */
    void registerAVTransport(IDevice<T> device, Context context);

    /**
     * 监听投屏端 RenderingControl 回调
     */
    /****
     * Listen to the RenderingControl callback of the screen casting end
     */
    void registerRenderingControl(IDevice<T> device, Context context);

    /**
     * 销毁: 释放资源
     */
    /****
     * Destroy: release resources
     */
    void destroy();
}
