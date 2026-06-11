package com.alivc.player.playerkits.shortvideolist.business.floatinglayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.player.playerkits.shortvideolist.R;

import java.util.Locale;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 拖拽seek时显示的时间浮动层
 */
public class AUIVideoTimeFloatingLayer extends FrameLayout {
    private TextView mTimeFloatLayerTv;

    public AUIVideoTimeFloatingLayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoTimeFloatingLayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIVideoTimeFloatingLayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_video_time_float_layer, null);
        mTimeFloatLayerTv = view.findViewById(R.id.time_float_layer);
        addView(view);
    }

    public void setTimeFloatLayerContent(String currentTime, String totalTime) {
        mTimeFloatLayerTv.setText(String.format(Locale.getDefault(), "%s/%s", currentTime, totalTime));
    }
}
