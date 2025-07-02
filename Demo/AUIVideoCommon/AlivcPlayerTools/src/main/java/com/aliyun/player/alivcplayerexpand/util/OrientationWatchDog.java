package com.aliyun.player.alivcplayerexpand.util;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;


/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 屏幕方向监听类
 */
/**
 * Screen orientation listener class
 */
public class OrientationWatchDog {
    private static final String TAG = OrientationWatchDog.class.getSimpleName();

    private Context mContext;
    //系统的屏幕方向改变监听
    //Listener for system screen orientation change
    private OrientationEventListener mLandOrientationListener;
    //对外的设置的监听
    //Listener for external settings
    private OnOrientationListener mOrientationListener;
    //上次屏幕的方向
    //Last screen orientation
    private Orientation mLastOrientation = Orientation.Port;

    /**
     * 屏幕方向
     */
    /****
     * Screen orientation
     */
    private enum Orientation {
        /**
         * 竖屏
         */
        /****
         * Portrait
         */
        Port,
        /**
         * 横屏,正向
         */
        /****
         * Landscape, forward
         */
        Land_Forward,
        /**
         * 横屏,反向
         */
        /****
         * Landscape, reverse
         */
        Land_Reverse;
    }


    public OrientationWatchDog(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 开始监听
     */
    /****
     * Start listening
     */
    public void startWatch() {
        if (mLandOrientationListener == null) {
            mLandOrientationListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int orientation) {
                    if (orientation == -1) {
                        return;
                    }
//                    Log.i(TAG, "onOrientationChanged orientation:" + orientation);
                    //这里的|| 和&& 不能弄错！！
                    // The || and && can't be wrong here!
                    //根据手机的方向角度计算。在90和180度上下10度的时候认为横屏了。
                    //Calculated based on the orientation angle of the phone. It's considered landscape at 90 and 180 degrees up and down by 10 degrees.
                    //竖屏类似。
                    //portrait is similar.
                    boolean isLand = (orientation < 100 && orientation > 80)
                            || (orientation < 280 && orientation > 260);

                    boolean isPort = (orientation < 10 || orientation > 350)
                            || (orientation < 190 && orientation > 170);

                    if (isLand) {
                        if (mOrientationListener != null && (orientation < 100 && orientation > 80)) {
                            if (mLastOrientation != Orientation.Land_Reverse) {
                                mOrientationListener.changedToLandReverseScape(mLastOrientation == Orientation.Port
                                        || mLastOrientation == Orientation.Land_Forward);
                                mLastOrientation = Orientation.Land_Reverse;
                            }
                        } else if (mOrientationListener != null && (orientation < 280 && orientation > 260)) {
                            if (mLastOrientation != Orientation.Land_Forward) {
                                mOrientationListener.changedToLandForwardScape(mLastOrientation == Orientation.Port
                                        || mLastOrientation == Orientation.Land_Reverse);
                                mLastOrientation = Orientation.Land_Forward;
                            }
                        }
                    } else if (isPort) {
                        if (mLastOrientation != Orientation.Port) {
                            if (mOrientationListener != null) {
                                mOrientationListener.changedToPortrait(mLastOrientation == Orientation.Land_Reverse
                                        || mLastOrientation == Orientation.Land_Forward);
                            }
                            mLastOrientation = Orientation.Port;
                        }
                    }

                }
            };
        }

        mLandOrientationListener.enable();
    }

    /**
     * 结束监听
     */
    /****
     * End listening
     */
    public void stopWatch() {
        if (mLandOrientationListener != null) {
            mLandOrientationListener.disable();
        }
    }

    /**
     * 销毁监听
     */
    /****
     * Destroy listener
     */
    public void destroy() {
        stopWatch();
        mLandOrientationListener = null;
    }

    /**
     * 屏幕方向变化事件
     */
    /****
     * Screen orientation change event
     */
    public interface OnOrientationListener {
        /**
         * 变为Land_Forward
         *
         * @param fromPort 是否是从竖屏变过来的
         */
        /****
         * Change to Land_Forward
         *
         * @param fromPort Whether it was changed from portrait
         */
        void changedToLandForwardScape(boolean fromPort);

        /**
         * 变为Land_Reverse
         *
         * @param fromPort 是否是从竖屏变过来的
         */
        /****
         * Change to Land_Reverse
         *
         * @param fromPort Whether it was changed from portrait
         */
        void changedToLandReverseScape(boolean fromPort);

        /**
         * 变为Port
         *
         * @param fromLand 是否是从横屏变过来的
         */
        /****
         * Change to Port
         *
         * @param fromLand Whether it was changed from landscape
         */
        void changedToPortrait(boolean fromLand);
    }

    /**
     * 设置屏幕方向变化事件
     *
     * @param l 事件监听
     */
    /****
     * Set screen orientation change event
     *
     * @param l Event listener
     */
    public void setOnOrientationListener(OnOrientationListener l) {
        mOrientationListener = l;
    }

}
