package com.aliyun.video.common.ui.play

interface IContinuePlayFragment {
    /**
     * 其它 fragment 会盖在上面
     */
    /****
     * Other fragments will be stamped on top
     */
    fun onBeforeHidden()

    /**
     * fragment 恢复到前台
     */
    /****
     * Fragment returns to the foreground
     */
    fun onReShow()
}