package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

/**
 * 控制点
 */
/****
 * Control point
 */

public interface IControlPoint<T> {

    /**
     * @return  返回控制点
     */
    /****
     * Return control point
     * @return
     */
    T getControlPoint();

    /**
     * 设置控制点
     * @param controlPoint  控制点
     */
    /****
     * Set control point
     * @param controlPoint
     */
    void setControlPoint(T controlPoint);

    /**
     * 销毁 清空缓存
     */
    /****
     * Destroy clear cache
     */
    void destroy();
}
