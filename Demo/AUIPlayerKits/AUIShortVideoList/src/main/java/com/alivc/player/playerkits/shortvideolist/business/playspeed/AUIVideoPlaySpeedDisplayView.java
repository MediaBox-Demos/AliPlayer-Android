package com.alivc.player.playerkits.shortvideolist.business.playspeed;

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
 * @brief 倍速播放显示view
 */
public class AUIVideoPlaySpeedDisplayView extends FrameLayout {
    private TextView mPlaySpeedContentTv;

    public AUIVideoPlaySpeedDisplayView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AUIVideoPlaySpeedDisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AUIVideoPlaySpeedDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_short_video_play_speed_display, null);
        mPlaySpeedContentTv = view.findViewById(R.id.tv_play_speed_display_content);
        addView(view);
    }

    public void setPlaySpeed(float speed) {
        String content = getResources().getString(R.string.play_speed_content);
        mPlaySpeedContentTv.setText(String.format(Locale.getDefault(), "%s%s", speed, content));
    }
}
