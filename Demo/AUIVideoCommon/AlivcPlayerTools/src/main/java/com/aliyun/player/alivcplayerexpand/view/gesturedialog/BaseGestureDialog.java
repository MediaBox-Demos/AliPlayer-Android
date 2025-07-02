package com.aliyun.player.alivcplayerexpand.view.gesturedialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;



/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 手势滑动的手势提示框。
 * 其子类有：{@link BrightnessDialog} , {@link SeekDialog} , {@link VolumeDialog}
 */
/**
 * Gesture sliding gesture prompt box.
 * Subclasses: {@link BrightnessDialog}, {@link SeekDialog}, {@link VolumeDialog}
 */
public class BaseGestureDialog extends PopupWindow {
    //手势文字
    //Gesture text
    TextView mTextView;
    //手势图片
    //Gesture image
    ImageView mImageView;
    //对话框的宽高
    //Dialog width and height
    private int mDialogWidthAndHeight;
    //当前屏幕模式
    //Current screen mode
    private AliyunScreenMode mCurrentScreenMode = AliyunScreenMode.Small;

    public BaseGestureDialog(Context context) {
        //使用同一个布局
        //Use the same layout
        LayoutInflater mInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.alivc_dialog_gesture, null);
        view.measure(0, 0);
        setContentView(view);

        //找出view
        //Find out the view
        mTextView = (TextView) view.findViewById(R.id.gesture_text);
        mImageView = (ImageView) view.findViewById(R.id.gesture_image);

        //设置对话框宽高
        //Set the dialog width and height
        mDialogWidthAndHeight = context.getResources().getDimensionPixelSize(R.dimen.alivc_player_gesture_dialog_size);
        setWidth(mDialogWidthAndHeight);
        setHeight(mDialogWidthAndHeight);
    }

    /**
     * 居中显示对话框
     * @param parent 所属的父界面
     */
    /****
     * Center display dialog
     * @param parent The parent interface
     */
    public void show(View parent) {

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        //保证显示居中
        //Ensure display centered
        int x = location[0] + (parent.getRight() - parent.getLeft() - mDialogWidthAndHeight) / 2;
        int y = location[1] + (parent.getBottom() - parent.getTop() - mDialogWidthAndHeight) / 2;

        if(mCurrentScreenMode == AliyunScreenMode.Small){
            showAtLocation(parent, Gravity.TOP | Gravity.LEFT, x, y);
        }else{
            //当全屏模式下,用CENTER,解决在分屏模式下,部分手机展示不居中问题
            //When full screen mode, use CENTER, solve the problem of not centered on some phones in split screen mode
            showAtLocation(parent,Gravity.CENTER,0,0);
        }
    }

    public void setScreenMode(AliyunScreenMode currentScreenMode){
        this.mCurrentScreenMode = currentScreenMode;
    }


}
