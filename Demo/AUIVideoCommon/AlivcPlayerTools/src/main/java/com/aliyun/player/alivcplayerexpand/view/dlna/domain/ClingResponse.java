package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;

/**
 * Cling 库中，控制操作返回结果
 */
/****
 * In the Cling library, the control operation returns the result.
 */

public class ClingResponse implements IResponse<ActionInvocation> {

    private ActionInvocation mActionInvocation;
    private UpnpResponse operation;
    private String defaultMsg;

    /**
     * 控制操作成功 构造器
     *
     * @param actionInvocation  cling action 调用
     */
    /****
     * Control operation success constructor
     *
     * @param actionInvocation  cling action invocation
     */
    public ClingResponse(ActionInvocation actionInvocation) {
        mActionInvocation = actionInvocation;
    }

    /**
     * 控制操作失败 构造器
     *
     * @param actionInvocation  cling action 调用
     * @param operation     执行状态
     * @param defaultMsg    错误信息
     */
    /****
     * Control operation failure constructor
     *
     * @param actionInvocation  cling action invocation
     * @param operation     execution status
     * @param defaultMsg    error message
     */
    public ClingResponse(ActionInvocation actionInvocation, UpnpResponse operation, String defaultMsg) {
        mActionInvocation = actionInvocation;
        this.operation = operation;
        this.defaultMsg = defaultMsg;
    }

    @Override
    public ActionInvocation getResponse() {
        return null;
    }

    @Override
    public void setResponse(ActionInvocation response) {
        mActionInvocation = response;
    }
}
