package com.aliyun.player.alivcplayerexpand.view.tips;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aliyun.player.alivcplayerexpand.view.tips.tipsview.CustomTipsView;
import com.aliyun.player.alivcplayerexpand.view.tips.tipsview.ErrorView;
import com.aliyun.player.alivcplayerexpand.view.tips.tipsview.LoadingView;
import com.aliyun.player.alivcplayerexpand.view.tips.tipsview.NetChangeView;
import com.aliyun.player.alivcplayerexpand.view.tips.OnTipsViewBackClickListener;
import com.aliyun.player.alivcplayerexpand.view.tips.tipsview.ReplayView;
import com.aliyun.player.bean.ErrorCode;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 提示对话框的管理器。
 * 用于管理{@link ErrorView} ，{@link LoadingView} ，{@link NetChangeView} , {@link ReplayView}等view的显示/隐藏等。
 */
/**
 * The manager of the prompt dialog box.
 * Used to manage the show/hide of {@link ErrorView} ，{@link LoadingView} ，{@link NetChangeView} , {@link ReplayView} and other views.
 */

public class TipsView extends RelativeLayout {

    private static final String TAG = TipsView.class.getSimpleName();
    //错误码
    //error code
    private int mErrorCode;
    //错误提示
    //error prompt
    private ErrorView mErrorView = null;
    //重试提示
    //retry prompt
    private ReplayView mReplayView = null;
    //缓冲加载提示
    //buffer loading prompt
    private LoadingView mNetLoadingView = null;
    //网络变化提示
    //network change prompt
    private NetChangeView mNetChangeView = null;
    //网络请求加载提示
    //network request loading prompt
    private LoadingView mBufferLoadingView = null;
    private CustomTipsView mCustomTipsView = null;
    //提示点击事件
    //tip click event
    private OnTipClickListener mOnTipClickListener = null;
    //返回点击事件
    //return click event
    private OnTipsViewBackClickListener mOnTipsViewBackClickListener = null;
    //当前的主题
    //current theme
//    private Theme mCurrentTheme;

    //网络变化监听事件。
    //network change listener
    private NetChangeView.OnNetChangeClickListener onNetChangeClickListener = new NetChangeView.OnNetChangeClickListener() {
        @Override
        public void onContinuePlay() {
            if (mOnTipClickListener != null) {
                mOnTipClickListener.onContinuePlay();
            }
        }

        @Override
        public void onStopPlay() {
            if (mOnTipClickListener != null) {
                mOnTipClickListener.onStopPlay();
            }
        }
    };
    //错误提示的重试点击事件
    //error prompt retry click event
    private ErrorView.OnRetryClickListener onRetryClickListener = new ErrorView.OnRetryClickListener() {
        @Override
        public void onRetryClick() {
            if (mOnTipClickListener != null) {
                //鉴权过期
                //expired authentication
                if (mErrorCode == ErrorCode.ERROR_SERVER_POP_UNKNOWN.getValue()) {
                    mOnTipClickListener.onRefreshSts();
                } else {
                    mOnTipClickListener.onRetryPlay(mErrorCode);
                }

            }
        }
    };

    //重播点击事件
    //replay click event
    private ReplayView.OnReplayClickListener onReplayClickListener = new ReplayView.OnReplayClickListener() {
        @Override
        public void onReplay() {
            if (mOnTipClickListener != null) {
                mOnTipClickListener.onReplay();
            }
            mReplayView = null;
        }
    };

    private OnTipsViewBackClickListener onTipsViewBackClickListener = new OnTipsViewBackClickListener() {
        @Override
        public void onBackClick() {
            if (mOnTipsViewBackClickListener != null) {
                mOnTipsViewBackClickListener.onBackClick();
            }
        }
    };

    private CustomTipsView.OnTipsViewClickListener mOnTipsViewClickListener = new CustomTipsView.OnTipsViewClickListener() {
        @Override
        public void onWait() {
            if (mOnTipClickListener != null) {
                mOnTipClickListener.onWait();
            }
        }

        @Override
        public void onExit() {
            if (mOnTipClickListener != null) {
                mOnTipClickListener.onExit();
            }
        }
    };

    public TipsView(Context context) {
        super(context);
    }

    public TipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 根据参数展示提示View
     *
     * @param tipsText    提示内容
     * @param confirmText 确定按钮提示内容
     * @param cancelText  取消按钮提示内容
     */
    /****
     * Show the prompt view according to the parameters
     *
     * @param tipsText    prompt content
     * @param confirmText confirm button prompt content
     * @param cancelText  cancel button prompt content
     */
    public void showCustomTipView(String tipsText, String confirmText, String cancelText) {
        if (mCustomTipsView == null) {
            mCustomTipsView = new CustomTipsView(getContext());
            mCustomTipsView.setOnTipsViewClickListener(mOnTipsViewClickListener);
            mCustomTipsView.setOnBackClickListener(onTipsViewBackClickListener);
            mCustomTipsView.setTipsText(tipsText);
            mCustomTipsView.setConfirmText(confirmText);
            mCustomTipsView.setCancelText(cancelText);
            addSubView(mCustomTipsView);
        }
        if (mErrorView != null && mErrorView.getVisibility() == VISIBLE) {

        } else {
            mCustomTipsView.setVisibility(VISIBLE);
        }
    }

    /**
     * 显示网络变化提示
     */
    /****
     * Show the network change prompt
     */
    public void showNetChangeTipView() {
        if (mNetChangeView == null) {
            mNetChangeView = new NetChangeView(getContext());
            mNetChangeView.setOnNetChangeClickListener(onNetChangeClickListener);
            mNetChangeView.setOnBackClickListener(onTipsViewBackClickListener);
            addSubView(mNetChangeView);
        }

        if (mErrorView != null && mErrorView.getVisibility() == VISIBLE) {
            //显示错误对话框了，那么网络切换的对话框就不显示了。
            //The error dialog box is displayed, then the dialog box for network switching is not displayed.
            //都出错了，还显示网络切换，没有意义
            //Both are wrong, but still show the network change, which has no meaning
        } else {
            mNetChangeView.setVisibility(VISIBLE);
        }

    }

    /**
     * 显示错误提示
     *
     * @param errorCode  错误码
     * @param errorEvent 错误事件
     * @param errorMsg   错误消息
     */
    /****
     * Show the error prompt
     *
     * @param errorCode  error code
     * @param errorEvent error event
     * @param errorMsg   error message
     */
    public void showErrorTipView(int errorCode, String errorEvent, String errorMsg) {
        if (mErrorView == null) {
            mErrorView = new ErrorView(getContext());
            mErrorView.setOnRetryClickListener(onRetryClickListener);
            mErrorView.setOnBackClickListener(onTipsViewBackClickListener);
            addSubView(mErrorView);
        }

        //出现错误了，先把网络的对话框关闭掉。防止同时显示多个对话框。
        //If there is an error, first close the network dialog box.
        //都出错了，还显示网络切换，没有意义
        //Both are wrong, but still show the network change, which has no meaning
        hideNetChangeTipView();

        mErrorCode = errorCode;
        mErrorView.updateTips(errorCode, errorEvent, errorMsg);
        mErrorView.setVisibility(VISIBLE);


        Log.d(TAG, " errorCode = " + mErrorCode);
    }

    /**
     * 显示错误提示,不显示错误码
     *
     * @param msg 错误信息
     */
    /****
     * Show the error prompt, without showing the error code
     *
     * @param msg error message
     */
    public void showErrorTipViewWithoutCode(String msg) {
        if (mErrorView == null) {
            mErrorView = new ErrorView(getContext());
            mErrorView.updateTipsWithoutCode(msg);
            mErrorView.setOnBackClickListener(onTipsViewBackClickListener);
            mErrorView.setOnRetryClickListener(onRetryClickListener);
            addSubView(mErrorView);
        }

        if (mErrorView.getVisibility() != VISIBLE) {
            mErrorView.setVisibility(VISIBLE);
        }
    }

    /**
     * 显示重播view
     */
    /****
     * Show the replay view
     */
    public void showReplayTipView(String coverUrl) {
        if (mReplayView == null) {
            mReplayView = new ReplayView(getContext());
            mReplayView.setOnBackClickListener(onTipsViewBackClickListener);
            mReplayView.setOnReplayClickListener(onReplayClickListener);
            addSubView(mReplayView);
        }

        if (mReplayView.getVisibility() != VISIBLE) {
            mReplayView.setVisibility(VISIBLE);
        }
        mReplayView.setData(coverUrl);
    }


    /**
     * 显示缓冲加载view
     */
    /****
     * Show the buffer loading view
     */
    public void showBufferLoadingTipView() {
        if (mBufferLoadingView == null) {
            mBufferLoadingView = new LoadingView(getContext());
            addSubView(mBufferLoadingView);
        }
        if (mBufferLoadingView.getVisibility() != VISIBLE) {
            mBufferLoadingView.setVisibility(VISIBLE);
        }
    }

    /**
     * 更新缓冲加载的进度
     *
     * @param percent 进度百分比
     */
    /****
     * Update the progress of the buffer loading
     *
     * @param percent progress percentage
     */
    public void updateLoadingPercent(int percent) {
        showBufferLoadingTipView();
        mBufferLoadingView.updateLoadingPercent(percent);
    }

    /**
     * 显示网络加载view
     */
    /****
     * Show the network loading view
     */
    public void showNetLoadingTipView() {
        if (mNetLoadingView == null) {
            mNetLoadingView = new LoadingView(getContext());
            mNetLoadingView.setOnlyLoading();
            addSubView(mNetLoadingView);
        }

        if (mNetLoadingView.getVisibility() != VISIBLE) {
            mNetLoadingView.setVisibility(VISIBLE);
        }
    }


    /**
     * 把新增的view添加进来，居中添加
     *
     * @param subView 子view
     */
    /****
     * Add the new view to the list, centered
     *
     * @param subView subview
     */
    public void addSubView(View subView) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(subView, params);

        //同时需要更新新加的view的主题
//        if (subView instanceof ITheme) {
//            ((ITheme) subView).setTheme(mCurrentTheme);
//        }
    }


    /**
     * 隐藏所有的tip
     */
    /****
     * Hide all tips
     */
    public void hideAll() {
        hideCustomTipView();
        hideNetChangeTipView();
        hideErrorTipView();
        hideReplayTipView();
        hideBufferLoadingTipView();
        hideNetLoadingTipView();
    }

    /**
     * 隐藏缓冲加载的tip
     */
    /****
     * Hide the buffer loading tip
     */
    public void hideBufferLoadingTipView() {
        if (mBufferLoadingView != null && mBufferLoadingView.getVisibility() == VISIBLE) {
            /*
                隐藏loading时,重置百分比,避免loading再次展示时,loading进度不是从0开始
             */
            /*
                Hide loading when resetting the percentage, so that the progress of the loading is not restarted again
             */
            mBufferLoadingView.updateLoadingPercent(0);

            mBufferLoadingView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 隐藏网络加载的tip
     */
    /****
     * Hide the network loading tip
     */
    public void hideNetLoadingTipView() {
        if (mNetLoadingView != null && mNetLoadingView.getVisibility() == VISIBLE) {
            mNetLoadingView.setVisibility(INVISIBLE);
        }
    }


    /**
     * 隐藏重播的tip
     */
    /****
     * Hide the replay tip
     */
    public void hideReplayTipView() {
        if (mReplayView != null && mReplayView.getVisibility() == VISIBLE) {
            mReplayView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 隐藏网络变化的tip
     */
    /****
     * Hide the network change tip
     */
    public void hideNetChangeTipView() {
        if (mNetChangeView != null && mNetChangeView.getVisibility() == VISIBLE) {
            mNetChangeView.setVisibility(INVISIBLE);
        }
    }

    public void hideCustomTipView() {
        if (mCustomTipsView != null && mCustomTipsView.getVisibility() == VISIBLE) {
            mCustomTipsView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 隐藏错误的tip
     */
    /****
     * Hide the error tip
     */
    public void hideErrorTipView() {
        if (mErrorView != null && mErrorView.getVisibility() == VISIBLE) {
            mErrorView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 错误的tip是否在显示，如果在显示的话，其他的tip就不提示了。
     *
     * @return true：是
     */
    /****
     * Is the error tip displayed? If it is displayed, other tips will not be prompted.
     *
     * @return true: yes
     */
    public boolean isErrorShow() {
        if (mErrorView != null) {
            return mErrorView.getVisibility() == VISIBLE;
        } else {
            return false;
        }
    }

    /**
     * 隐藏网络错误tip
     */
    /****
     * Hide the network error tip
     */
    public void hideNetErrorTipView() {
        //TODO
//        if (mErrorView != null && mErrorView.getVisibility() == VISIBLE
//                && mErrorCode == AliyunErrorCode.ALIVC_ERROR_LOADING_TIMEOUT.getCode()) {
//            mErrorView.setVisibility(INVISIBLE);
//        }
    }


//    @Override
//    public void setTheme(Theme theme) {
//
//        mCurrentTheme = theme;
//        //判断子view是不是实现了ITheme的接口，从而达到更新主题的目的
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            if (child instanceof ITheme) {
//                ((ITheme) child).setTheme(theme);
//            }
//        }
//    }

    /**
     * 提示view中的点击操作
     */
    /****
     * Click operations in the tip view
     */
    public interface OnTipClickListener {
        /**
         * 继续播放
         */
        /****
         * Continue playing
         */
        void onContinuePlay();

        /**
         * 停止播放
         */
        /****
         * Stop playing
         */
        void onStopPlay();

        /**
         * 重试播放
         */
        /****
         * Retry playing
         */
        void onRetryPlay(int errorCode);

        /**
         * 重播
         */
        /****
         * Replay
         */
        void onReplay();

        /**
         * 刷新sts
         */
        /****
         * Refresh sts
         */
        void onRefreshSts();

        /**
         * 等待
         */
        /****
         * Wait
         */
        void onWait();

        /**
         * 退出
         */
        /****
         * Exit
         */
        void onExit();
    }

    /**
     * 设置提示view中的点击操作 监听
     *
     * @param l 监听事件
     */
    /****
     * Set the click operation of the tip view to listen
     *
     * @param l listener event
     */
    public void setOnTipClickListener(OnTipClickListener l) {
        mOnTipClickListener = l;
    }

    public void setOnTipsViewBackClickListener(OnTipsViewBackClickListener listener) {
        this.onTipsViewBackClickListener = listener;
    }
}
