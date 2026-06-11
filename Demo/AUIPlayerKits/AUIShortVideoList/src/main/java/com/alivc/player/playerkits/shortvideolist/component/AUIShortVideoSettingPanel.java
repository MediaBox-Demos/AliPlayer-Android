package com.alivc.player.playerkits.shortvideolist.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.player.playerkits.shortvideolist.business.playspeed.AUIVideoPlaySpeedPanelView;
import com.alivc.player.playerkits.shortvideolist.business.trackinfo.AUIVideoTrackInfoPanelView;
import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.listener.OnSpeedSettingListener;
import com.aliyun.player.nativeclass.TrackInfo;

import java.util.List;

/**
 * @author keria
 * @date 2023/8/29
 * @brief 短视频列表播放-设置面板
 */
public class AUIShortVideoSettingPanel extends FrameLayout {
    private AUIVideoPlaySpeedPanelView mAUIVideoPlaySpeedPanelView;
    private AUIVideoTrackInfoPanelView mAUIVideoTrackInfoPanelView;

    public AUIShortVideoSettingPanel(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIShortVideoSettingPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIShortVideoSettingPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("MissingInflatedId")
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_short_video_list_setting_panel, null);
        mAUIVideoPlaySpeedPanelView = view.findViewById(R.id.aui_speed_panel);
        mAUIVideoTrackInfoPanelView = view.findViewById(R.id.aui_track_info_panel);
        addView(view);
    }

    public void setOnSpeedSettingListener(OnSpeedSettingListener onSpeedSettingListener) {
        mAUIVideoPlaySpeedPanelView.setOnSpeedSettingListener(onSpeedSettingListener);
    }

    public void setOnTrackInfoListener(AUIVideoTrackInfoPanelView.OnTrackInfoListener listener) {
        mAUIVideoTrackInfoPanelView.setOnTrackInfoListener(listener);
    }

    public void setTrackInfoList(List<TrackInfo> trackInfoList) {
        mAUIVideoTrackInfoPanelView.setTrackInfoList(trackInfoList);
    }
}
