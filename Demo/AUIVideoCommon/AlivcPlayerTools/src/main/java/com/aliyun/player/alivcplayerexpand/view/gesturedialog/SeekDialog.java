package com.aliyun.player.alivcplayerexpand.view.gesturedialog;

import android.app.Activity;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.util.TimeFormater;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势滑动的seek提示框。
 */
/**
 * Gesture sliding seek prompt box.
 */
public class SeekDialog extends BaseGestureDialog {

    private int mInitPosition = 0;
    private int mFinalPosition = 0;

    public SeekDialog(Activity activity, int position) {
        super(activity);
        mInitPosition = position;
        updatePosition(mInitPosition);
    }

    public void updatePosition(int position) {
        //这里更新了网签和往后seek的图片
        //Update the image of the net sign and the forward seek
        if (position >= mInitPosition) {
            mImageView.setImageResource(R.drawable.alivc_seek_forward);
        } else {
            mImageView.setImageResource(R.drawable.alivc_seek_rewind);
        }
        mTextView.setText(TimeFormater.formatMs(position));
    }

    /**
     * 目标位置计算算法
     *
     * @param duration        视频总时长
     * @param currentPosition 当前播放位置
     * @param deltaPosition 与当前位置相差的时长
     * @return
     */
    /****
     * Target position calculation algorithm
     *
     * @param duration        Video total duration
     * @param currentPosition Current playback position
     * @param deltaPosition   The difference between the current position
     * @return
     */
    public  int getTargetPosition(long duration, long currentPosition, long deltaPosition) {
        // seek步长
        // Seek step
        long finalDeltaPosition;
        // 根据视频时长，决定seek步长
        // According to the video duration, decide the seek step
        long totalMinutes = duration / 1000 / 60;
        int hours = (int) (totalMinutes / 60);
        int minutes = (int) (totalMinutes % 60);

        // 视频时长为1小时以上，小屏和全屏的手势滑动最长为视频时长的十分之一
        // When the length of the video is more than 1 hour, the gesture slide for small screen and full screen is up to one-tenth of the length of the video.
        if (hours >= 1) {
            finalDeltaPosition = deltaPosition / 10;
        }// 视频时长为31分钟－60分钟时，小屏和全屏的手势滑动最长为视频时长五分之一
        // When the length of the video is 31 minutes - 60 minutes, the gesture slide for small screen and full screen is up to one-fifth of the length of the video.
        else if (minutes > 30) {
            finalDeltaPosition = deltaPosition / 5;
        }// 视频时长为11分钟－30分钟时，小屏和全屏的手势滑动最长为视频时长三分之一
        // When the length of the video is 11 minutes - 30 minutes, the gesture slide for small screen and full screen is up to one-third of the length of the video.
        else if (minutes > 10) {
            finalDeltaPosition = deltaPosition / 3;
        }// 视频时长为4-10分钟时，小屏和全屏的手势滑动最长为视频时长二分之一
        // When the length of the video is 4 - 10 minutes, the gesture slide for small screen and full screen is up to one-half of the length of the video.
        else if (minutes > 3) {
            finalDeltaPosition = deltaPosition / 2;
        }// 视频时长为1秒钟至3分钟时，小屏和全屏的手势滑动最长为视频结束
        // When the length of the video is 1 second to 3 minutes, the gesture slide for small screen and full screen is up to the end of the video.
        else {
            finalDeltaPosition = deltaPosition;
        }

        long targetPosition = finalDeltaPosition + currentPosition;
        if (targetPosition < 0) {
            targetPosition = 0;
        }
        if (targetPosition > duration) {
            targetPosition = duration;
        }
        mFinalPosition = (int) targetPosition;
        return mFinalPosition;
    }

    /**
     * 获取最终的位置
     * @return
     */
    /****
     * Get the final position
     * @return
     */
    public int getFinalPosition() {
        return mFinalPosition;
    }
}
