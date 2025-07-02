package com.aliyun.player.alivcplayerexpand.view.gesture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.aliyun.player.alivcplayerexpand.view.interfaces.ViewAction;
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;
/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势滑动的view。用于UI中处理手势的滑动事件，从而去实现手势改变亮度，音量，seek等操作。
 * 此view主要被{@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView} 使用。
 */
/**
 * View for handling the gesture sliding events.
 * Used to implement the gesture change of brightness, volume, seek etc. operations in the UI.
 * This view is mainly used by {@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView}.
 */
public class GestureView extends View implements ViewAction {

    private static final String TAG = GestureView.class.getSimpleName();

    //gesture control
    //
    protected GestureControl mGestureControl;
    //监听器
    //Listener
    private GestureListener mOutGestureListener = null;

    //隐藏原因
    //Hide reason
    private HideType mHideType = null;
    //是否锁定屏幕
    //Is screen locked
    private boolean mIsFullScreenLocked = false;
    //是否处于分屏模式
    //Is in multi window or not
    private boolean mIsInMultiWindow;

    public GestureView(Context context) {
        super(context);
        init();
    }

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //创建手势控制
        //Create gesture control
        mGestureControl = new GestureControl(getContext(), this);
        mGestureControl.setMultiWindow(mIsInMultiWindow);
        mGestureControl.setView(this);
        //设置监听
        //Set listener
        mGestureControl.setOnGestureControlListener(new GestureListener() {

            @Override
            public void onHorizontalDistance(float downX, float nowX) {
                //其他手势如果锁住了就不回调了。
                //Other gestures if locked, it will not be called
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onHorizontalDistance(downX, nowX);
                }
            }

            @Override
            public void onLeftVerticalDistance(float downY, float nowY) {
                //其他手势如果锁住了就不回调了。
                //Other gestures if locked, it will not be called
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onLeftVerticalDistance(downY, nowY);
                }
            }

            @Override
            public void onRightVerticalDistance(float downY, float nowY) {
                //其他手势如果锁住了就不回调了。
                //Other gestures if locked, it will not be called
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onRightVerticalDistance(downY, nowY);
                }
            }

            @Override
            public void onGestureEnd() {
                //其他手势如果锁住了就不回调了。
                //Other gestures if locked, it will not be called
                if (mIsFullScreenLocked) {
                    return;
                }
                if (mOutGestureListener != null) {
                    mOutGestureListener.onGestureEnd();
                }
            }

            @Override
            public void onSingleTap() {
                //锁屏的时候，单击还是有用的。。不然没法显示锁的按钮了
                //Lock screen, single click is still useful...otherwise, you can't display the lock button
                if (mOutGestureListener != null) {
                    mOutGestureListener.onSingleTap();
                }
            }

            @Override
            public void onDoubleTap() {
                //其他手势如果锁住了就不回调了。
                //Other gestures if locked, it will not be called
                if (mIsFullScreenLocked) {
                    return;
                }

                if (mOutGestureListener != null) {
                    mOutGestureListener.onDoubleTap();
                }
            }

            @Override
            public void onLongPress() {
                if (mIsFullScreenLocked) {
                    return;
                }

                if (mOutGestureListener != null) {
                    mOutGestureListener.onLongPress();
                }
            }
        });
    }

    /**
     * 设置是否锁定全屏了。锁定全屏的话，除了单击手势有响应，其他都不会有响应。
     *
     * @param locked true：锁定。
     */
    /****
     * Set whether the full screen is locked.
     * If the full screen is locked, only the single click gesture has a response, and other gestures will not have a response.
     *
     * @param locked true: locked
     */
    public void setScreenLockStatus(boolean locked) {
        mIsFullScreenLocked = locked;
    }

    public void setHideType(HideType hideType) {
        this.mHideType = hideType;
    }

    /**
     * 设置是否处于分屏模式
     *
     * @param isInMultiWindow true,分屏模式,false不是分屏模式
     */
    /****
     * Set whether it is in multi-window mode
     *
     * @param isInMultiWindow true: multi-window mode, false: not multi-window mode
     */
    public void setMultiWindow(boolean isInMultiWindow) {
        this.mIsInMultiWindow = isInMultiWindow;
        if (mGestureControl != null) {
            mGestureControl.setMultiWindow(mIsInMultiWindow);
        }
    }

    public interface GestureListener {
        /**
         * 水平滑动距离
         *
         * @param downX 按下位置
         * @param nowX  当前位置
         */
        /****
         * Horizontal sliding distance
         *
         * @param downX Down position
         * @param nowX  Current position
         */
        void onHorizontalDistance(float downX, float nowX);

        /**
         * 左边垂直滑动距离
         *
         * @param downY 按下位置
         * @param nowY  当前位置
         */
        /****
         * Left vertical sliding distance
         *
         * @param downY Down position
         * @param nowY  Current position
         */
        void onLeftVerticalDistance(float downY, float nowY);

        /**
         * 右边垂直滑动距离
         *
         * @param downY 按下位置
         * @param nowY  当前位置
         */
        /****
         * Right vertical sliding distance
         *
         * @param downY Down position
         * @param nowY  Current position
         */
        void onRightVerticalDistance(float downY, float nowY);

        /**
         * 手势结束
         */
        /****
         * Gesture end
         */
        void onGestureEnd();

        /**
         * 单击事件
         */
        /****
         * Single click event
         */
        void onSingleTap();

        /**
         * 双击事件
         */
        /****
         * Double click event
         */
        void onDoubleTap();

        void onLongPress();
    }

    /**
     * 设置手势监听事件
     *
     * @param gestureListener 手势监听事件
     */
    /****
     * Set gesture listener event
     *
     * @param gestureListener Gesture listener event
     */
    public void setOnGestureListener(GestureListener gestureListener) {
        mOutGestureListener = gestureListener;
    }

    @Override
    public void reset() {
        mHideType = null;
    }

    @Override
    public void show() {
        if (mHideType == HideType.End) {
            //如果是由于错误引起的隐藏，那就不能再展现了
            //If it is caused by an error, it can no longer be displayed
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void show(ShowType showType) {
    }

    @Override
    public void hide(HideType hideType) {
        if (mHideType != HideType.End) {
            mHideType = hideType;
        }
        setVisibility(GONE);
    }

    @Override
    public void setScreenModeStatus(AliyunScreenMode mode) {

    }

}
