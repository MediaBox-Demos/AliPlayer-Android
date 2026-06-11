package com.alivc.player.playerkits.shortvideolist.business.trackinfo;

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
import com.aliyun.player.nativeclass.TrackInfo;

import java.util.List;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 多视频轨播放（可变清晰度）面板
 */
public class AUIVideoTrackInfoPanelView extends FrameLayout {
    private RecyclerView mTrackInfoListRv;
    private OnTrackInfoListener mOnTrackInfoListener;

    public AUIVideoTrackInfoPanelView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoTrackInfoPanelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AUIVideoTrackInfoPanelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("MissingInflatedId")
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ilr_view_player_track_info_panel, null);
        mTrackInfoListRv = view.findViewById(R.id.rv_track_info);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTrackInfoListRv.setLayoutManager(linearLayoutManager);
        addView(view);
    }

    public void setOnTrackInfoListener(OnTrackInfoListener listener) {
        mOnTrackInfoListener = listener;
    }

    public void setTrackInfoList(List<TrackInfo> trackInfoList) {
        AUIVideoTrackInfoAdapter adapter = new AUIVideoTrackInfoAdapter(trackInfoList);
        adapter.setOnTrackInfoListener(mOnTrackInfoListener);
        mTrackInfoListRv.setAdapter(adapter);
    }

    public interface OnTrackInfoListener {
        void onTrackInfoListUpdated(List<TrackInfo> trackInfoList);

        void onTrackInfoSelected(TrackInfo trackInfo);
    }
}
