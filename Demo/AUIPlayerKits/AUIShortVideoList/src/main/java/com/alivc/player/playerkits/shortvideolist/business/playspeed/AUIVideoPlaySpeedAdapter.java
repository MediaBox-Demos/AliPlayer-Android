package com.alivc.player.playerkits.shortvideolist.business.playspeed;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.listener.OnSpeedSettingListener;

import java.util.List;
import java.util.Locale;

/**
 * @author junhuiYe
 * @date 2024/9/11
 * @brief 倍速播放适配器
 */
public class AUIVideoPlaySpeedAdapter extends RecyclerView.Adapter<AUIVideoPlaySpeedAdapter.ViewHolder> {
    private final List<Float> speedTimeList;
    private OnSpeedSettingListener mOnSpeedSettingListener;

    public AUIVideoPlaySpeedAdapter(List<Float> speedTimeList) {
        this.speedTimeList = speedTimeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_short_video_play_speed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        float speedTimes = speedTimeList.get(position);
        holder.onBind(speedTimes);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSpeedSettingListener != null) {
                    mOnSpeedSettingListener.onClickSpeed(speedTimeList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return speedTimeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mSpeedTimeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSpeedTimeTv = itemView.findViewById(R.id.tv_track_info);
        }

        private void onBind(float time) {
            mSpeedTimeTv.setText(String.format(Locale.getDefault(), "%.1fx", time));
        }
    }

    public void setOnSpeedSettingListener(OnSpeedSettingListener onSpeedSettingListener) {
        mOnSpeedSettingListener = onSpeedSettingListener;
    }
}
