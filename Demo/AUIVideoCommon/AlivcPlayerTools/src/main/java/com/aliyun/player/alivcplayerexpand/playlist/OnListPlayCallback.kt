package com.aliyun.player.alivcplayerexpand.playlist

interface OnListPlayCallback {
    /**
     * UI显示 loading
     */
    /****
     * UI display loading
     */
    fun onPrepare()

    /**
     * loading 消失
     */
    /****
     * loading disappear
     */
    fun onPlaying()

    /**
     * 显示暂停
     */
    /****
     * display pause
     */
    fun onPause()

    fun onPlayComplete()

    /**
     * 播放进度更新 0- 1
     */
    /****
     * play progress update 0- 1
     */
    fun onPlayProgress(
        playProgress: Float,
        currentPlayMillis: Int,
        durationMillis: Int
    )

    fun onPlayError(errorCode: Int, msg: String)

    /**
     * 如果是续播，则会回调此接口告诉上层
     */
    /****
     * if it is resuming, it will callback this interface to tell the upper layer
     */
    fun onContrastPlay(durationMillis: Int)
}