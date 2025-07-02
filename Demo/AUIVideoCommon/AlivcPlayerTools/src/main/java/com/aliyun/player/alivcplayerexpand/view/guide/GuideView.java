package com.aliyun.player.alivcplayerexpand.view.guide;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.theme.ITheme;
import com.aliyun.player.alivcplayerexpand.theme.Theme;
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势指导页面。
 * 主要在{@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView} 使用。
 */
/****
 * Guide page for gestures.
 * Mainly used by {@link com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView}.
 */

public class GuideView extends LinearLayout implements ITheme {

    //三个文字显示
    //Three text
    private TextView mBrightText, mProgressText, mVolumeText;

    public GuideView(Context context) {
        super(context);
        init();
    }

    public GuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //设置页面布局
        //Set page layout
        setBackgroundColor(Color.parseColor("#88000000"));
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_view_guide, this, true);

        //这几个文字有颜色的变化
        //These three text have color changes
        mBrightText = (TextView) findViewById(R.id.bright_text);
        mProgressText = (TextView) findViewById(R.id.progress_text);
        mVolumeText = (TextView) findViewById(R.id.volume_text);

        //默认是隐藏的
        //By default it is hidden
        hide();
    }

    /**
     * 设置当前屏幕的模式
     *
     * @param mode 全屏，小屏
     */
    /****
     * Set the current screen mode
     *
     * @param mode full screen, small screen
     */
    public void setScreenMode(AliyunScreenMode mode) {
        if (mode == AliyunScreenMode.Small) {
            //小屏时隐藏
            //Small screen hide
            hide();
            return;
        }
//只有第一次进入全屏的时候显示。通过SharedPreferences记录这个值。
        //Only the first time entering full screen is displayed. Record this value by SharedPreferences
        SharedPreferences spf = getContext().getSharedPreferences("alivc_guide_record", Context.MODE_PRIVATE);
        boolean hasShown = spf.getBoolean("has_shown", false);
        //如果已经显示过了，就不接着走了
        //If it has been displayed, it will not go
        if (hasShown) {
            return;
        } else {
            setVisibility(VISIBLE);
        }
        //记录下来
        //Record
        SharedPreferences.Editor editor = spf.edit();
        editor.putBoolean("has_shown", true);
        editor.apply();
    }

    /**
     * 隐藏不显示
     */
    /****
     * Hide and do not display
     */
    public void hide() {
        setVisibility(GONE);
    }

    /**
     * 手势点击到了就隐藏
     *
     * @param event 触摸事件
     * @return
     */
    /****
     * The gesture clicked to hide
     *
     * @param event touch event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hide();
        return true;
    }

    /**
     * 设置主题色
     *
     * @param theme 支持的主题
     */
    /****
     * Set the theme color
     *
     * @param theme Supported theme
     */
    @Override
    public void setTheme(Theme theme) {
        int colorRes = R.color.alivc_blue;

        if (theme == Theme.Blue) {
            colorRes = R.color.alivc_blue;
        } else if (theme == Theme.Green) {
            colorRes = R.color.alivc_green;
        } else if (theme == Theme.Orange) {
            colorRes = R.color.alivc_orange;
        } else if (theme == Theme.Red) {
            colorRes = R.color.alivc_red;
        }

        int color = ContextCompat.getColor(getContext(), colorRes);
//这三个text的颜色会改变
        //These three text colors will change
        mBrightText.setTextColor(color);
        mProgressText.setTextColor(color);
        mVolumeText.setTextColor(color);
    }
}
