package com.aliyun.player.alivcplayerexpand.view.download;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;


/**
 * 离线视频tab, 编辑 --> 删除dialog
 * @author Mulberry
 *         create on 2018/4/19.
 */
/****
 * Offline video tab, edit --> delete dialog
 * @author Mulberry
 * create on 2018/4/19.
 */

public class AlivcDialog extends Dialog {

    //确定按钮
    //Confirm button
    private Button btnConfirm;
    //取消按钮
    //Cancel button
    private Button btnCancel;
    //消息标题文本
    //Message title text
    private ImageView ivDialogIcon;
    //消息提示文本
    //Message prompt text
    private TextView tvMessage;
    //从外界设置的title文本
    //From the outside set the title text
    private int  dialogIcon;
    //从外界设置的消息文本
    //From the outside set the message text
    private String messageStr;
    //确定文本和取消文本的显示内容
    //Confirm text and cancel text display content
    private String confirmStr, cancelStr;
    //取消按钮被点击了的监听器
    //the listener of Cancel button clicked
    private onCancelOnclickListener onCancelOnclickListener;
    //确定按钮被点击了的监听器
    //the listener of Confirm button clicked
    private onConfirmClickListener onConfirmClickListener;


    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onCancelOnclickListener
     */
    /****
     * Setting up the display of the cancel button and listening to it.
     *
     * @param str
     * @param onCancelOnclickListener
     */
    public void setOnCancelOnclickListener(String str, onCancelOnclickListener onCancelOnclickListener) {
        if (str != null) {
            cancelStr = str;
        }
        this.onCancelOnclickListener = onCancelOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    /****
     * Setting up the display of the confirm button and listening to it.
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setOnConfirmclickListener(String str, onConfirmClickListener onYesOnclickListener) {
        if (str != null) {
            confirmStr = str;
        }
        this.onConfirmClickListener = onYesOnclickListener;
    }


    public AlivcDialog(@NonNull Context context) {
        super(context);
    }

    public AlivcDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public AlivcDialog(@NonNull Context context, boolean cancelable,
                       @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_dialog_delete);
        //按空白处不能取消动画
        //Can not cancel the animation by clicking on the blank
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        //Initialize the interface controls
        initView();
        //初始化界面数据
        //Initialize the interface data
        initData();
        //初始化界面控件的事件
        //Initialize the interface control events
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    /****
     * Initialize the interface listeners.
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        //Set the confirm button to be clicked after providing a listener
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onConfirm();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        //Set the cancel button to be clicked after providing a listener
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelOnclickListener != null) {
                    onCancelOnclickListener.onCancel();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    /****
     * Initialize the interface control display data.
     */
    private void initData() {
        //如果用户自定了title和message
        //If the user customized the title and message
        ivDialogIcon.setBackgroundResource(dialogIcon);
        if (messageStr != null) {
            tvMessage.setText(messageStr);
        }
        //如果设置按钮的文字
        //If the set button text
        if (confirmStr != null) {
            btnConfirm.setText(confirmStr);
        }
        if (cancelStr != null) {
            btnCancel.setText(cancelStr);
        }
    }

    /**
     * 初始化界面控件
     */
    /****
     * Initialize the interface controls.
     */
    private void initView() {
        btnConfirm = (Button) findViewById(R.id.yes);
        btnCancel = (Button) findViewById(R.id.no);
        ivDialogIcon = (ImageView) findViewById(R.id.iv_dialog_icon);
        tvMessage = (TextView) findViewById(R.id.tv_message);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param icon
     */
    /****
     * From the outside Activity to set the title of the dialog.
     *
     * @param icon
     */
    public void setDialogIcon(int icon) {
        dialogIcon = icon;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    /****
     * From the outside Activity to set the dialog message.
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    /****
     * Set the interface of the confirm button and cancel button clicked.
     */
    public interface onConfirmClickListener {
        void onConfirm();
    }

    public interface onCancelOnclickListener {
        void onCancel();
    }
}
