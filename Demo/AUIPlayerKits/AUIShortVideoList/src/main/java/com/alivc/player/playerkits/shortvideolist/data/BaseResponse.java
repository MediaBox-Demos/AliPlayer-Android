package com.alivc.player.playerkits.shortvideolist.data;

import java.io.Serializable;

// Copyright © 2025 Alibaba Cloud. All rights reserved.
//
// Author: junHuiYe
// Date: 2025/11/20
// Brief: 网络请求实体类
public class BaseResponse<T> implements Serializable {
    /**
     * @brief 业务状态码
     * 字符串形式，如 "200"、"500"，可根据状态码判定请求状态。
     */
    public int code;

    /**
     * @brief HTTP状态码
     * HTTP状态码，可能为null
     */
    public String httpCode;

    /**
     * @brief 请求成功
     * 返回值为boolean类型
     */
    public boolean success;

    /**
     * @brief 请求返回信息
     * 请求信息或错误信息
     */
    public String message;

    /**
     * @brief 请求唯一标识 ID
     * 可排查追踪本地请求的问题
     */
    public String requestId;

    /**
     * @brief 业务数据对象
     * 具体的业务信息
     */
    public T data;
}
