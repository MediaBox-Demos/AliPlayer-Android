package com.alivc.player.playerkits.shortvideolist.business.playspeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.listener.OnSpeedSettingListener;

import java.util.Arrays;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 倍速播放面板view
 */
public class AUIVideoPlaySpeedPanelView extends FrameLayout {
    private static final Float[] speedTimes = {0.5f, 0.75f, 1.0f, 1.5f, 2.0f, 3.0f};

    private AUIVideoPlaySpeedAdapter mAdapter;

    public AUIVideoPlaySpeedPanelView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AUIVideoPlaySpeedPanelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AUIVideoPlaySpeedPanelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @SuppressLint("MissingInflatedId")
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_short_video_list_player_speed_panel, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_speed);
        mAdapter = new AUIVideoPlaySpeedAdapter(Arrays.asList(speedTimes));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        addView(view);
    }

    public void setOnSpeedSettingListener(OnSpeedSettingListener onSpeedSettingListener) {
        if (mAdapter != null) {
            mAdapter.setOnSpeedSettingListener(onSpeedSettingListener);
        }
    }
}
