package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

/**
 * 设备控制 返回结果
 */
/****
 * Device control, return result
 */

public interface IResponse<T> {

    T getResponse();

    void setResponse(T response);
}
