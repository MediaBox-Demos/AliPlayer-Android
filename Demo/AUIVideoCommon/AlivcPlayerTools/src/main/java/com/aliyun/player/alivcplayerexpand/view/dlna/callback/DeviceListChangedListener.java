package com.aliyun.player.alivcplayerexpand.view.dlna.callback;


import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;

/**
 * 设备状态改变监听接口
 */
/****
 * Device state change listening interface
 */
public interface DeviceListChangedListener {

    /**
     * 某设备被发现之后回调该方法
     * @param device    被发现的设备
     */
    /****
     * After the device is discovered, the callback method is called
     * @param device    Discovered device
     */
    void onDeviceAdded(IDevice device);

    /**
     * 在已发现设备中 移除了某设备之后回调该接口
     * @param device    被移除的设备
     */
    /****
     * In the discovered device, after removing a device, the callback interface is called
     * @param device    Removed device
     */
    void onDeviceRemoved(IDevice device);

    /**
     * 搜索设备超时
     */
    /****
     * Device search timeout
     */
    void onDeviceSearchTimeout();

    /**
     * 开始搜索设备
     */
    /****
     * Start searching for devices
     */
    void onDeviceSearchStart();

    /**
     * 设备连接成功
     */
    /****
     * Device connection success
     */
    void onDeviceConnected();

    /**
     * 设备连接超时
     */
    /****
     * Device connection timeout
     */
    void onDeviceConnectTimeout();
}
