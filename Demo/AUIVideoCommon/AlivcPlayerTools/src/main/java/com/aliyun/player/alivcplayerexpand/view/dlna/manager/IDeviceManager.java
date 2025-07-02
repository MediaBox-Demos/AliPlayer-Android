package com.aliyun.player.alivcplayerexpand.view.dlna.manager;

import android.content.Context;

import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;


/**
 * 设备管理类
 */
/****
 * DeviceManager implementation class
 */

public interface IDeviceManager {

    /**
     * 获取选中设备
     */
    /****
     * Get selected device
     */
    IDevice getSelectedDevice();

    /**
     * 设置选中设备
     */
    /****
     * Set selected device
     */
    void setSelectedDevice(IDevice selectedDevice);

    /**
     * 取消选中设备
     */
    /****
     * Cancel selected device
     */
    void cleanSelectedDevice();

    /**
     * 设备连接成功
     */
    /****
     * Device connection success
     */
    void selectedDeviceConnected();

    /**
     * 监听投屏端 AVTransport 回调
     */
    /****
     * Listen to the AVTransport callback of the screen casting side
     */
    void registerAVTransport(Context context);

    /**
     * 监听投屏端 RenderingControl 回调
     */
    /****
     * Listen to the RenderingControl callback of the screen casting side
     */
    void registerRenderingControl(Context context);

    /**
     * 销毁
     */
    /****
     * Destroy
     */
    void destroy();
}
