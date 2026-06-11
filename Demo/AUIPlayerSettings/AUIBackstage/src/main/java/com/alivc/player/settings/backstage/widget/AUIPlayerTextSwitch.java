package com.alivc.player.settings.backstage.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * @author keria
 * @date 2024/5/23
 * @brief 带文字的按钮开关
 */
public class AUIPlayerTextSwitch extends LinearLayout {

    private TextView textView;
    private Switch switchView;

    /**
     * 定义回调接口，用于监听 Switch 状态变化
     */
    public interface OnSwitchToggleListener {
        void onSwitchToggled(boolean isChecked);
    }

    private OnSwitchToggleListener onSwitchToggleListener;

    public AUIPlayerTextSwitch(Context context) {
        super(context);
        init(context);
    }

    public AUIPlayerTextSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIPlayerTextSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 设置横向布局
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        textView = new TextView(context);
        textView.setTextColor(Color.WHITE);
        textView.setLines(1);
        textView.setTextSize(12);
        textView.setPadding(0, 0, 12, 0);

        switchView = new Switch(context);
        switchView.setChecked(false);

        // 设置 TextView 的布局参数，使其宽度包裹内容并且靠左对齐
        LayoutParams textViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;

        // 设置 Switch 的布局参数，使其宽度包裹内容并且靠右对齐
        LayoutParams switchLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        switchLayoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        textView.setLayoutParams(textViewLayoutParams);
        switchView.setLayoutParams(switchLayoutParams);

        // 将 TextView 和 Switch 添加到布局中
        addView(textView);
        addView(switchView);

        // 设置 Switch 的监听事件
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onSwitchToggleListener != null) {
                onSwitchToggleListener.onSwitchToggled(isChecked);
            }
        });

        setPadding(0, 3, 3, 3);
    }

    /**
     * 用于设置 TextView 的文本
     */
    public void setTextViewText(String text) {
        textView.setText(text);
    }

    /**
     * 用于设置 Switch 的初始状态
     */
    public void setSwitchChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    /**
     * 用于设置回调监听器
     */
    public void setOnSwitchToggleListener(OnSwitchToggleListener listener) {
        this.onSwitchToggleListener = listener;
    }
}
