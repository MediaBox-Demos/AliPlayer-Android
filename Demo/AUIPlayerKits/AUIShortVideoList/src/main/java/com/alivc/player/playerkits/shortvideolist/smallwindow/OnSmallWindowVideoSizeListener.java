package com.alivc.player.playerkits.shortvideolist.smallwindow;

public interface OnSmallWindowVideoSizeListener {
    /**
     * 视频尺寸获取完成
     * @param width 视频宽度
     * @param height 视频高度
     */
    void onVideoSizeChanged(int width, int height);

    /**
     * 视频信息获取失败
     * @param error 错误信息
     */
    void onVideoInfoError(String error);
}
