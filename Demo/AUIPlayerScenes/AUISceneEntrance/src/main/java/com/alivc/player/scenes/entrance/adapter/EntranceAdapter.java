package com.alivc.player.scenes.entrance.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.scenes.entrance.R;
import com.alivc.player.scenes.entrance.data.EntranceData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2024/9/25
 * @brief 入口页适配器
 */
public class EntranceAdapter extends RecyclerView.Adapter<EntranceAdapter.ViewHolder> {
    private List<EntranceData> mEntranceData = new ArrayList<>();

    @NonNull
    @Override
    public EntranceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.aui_scene_entrance_item, parent, false);
        return new ViewHolder(view);
    }

    public void setData(List<EntranceData> mEntranceData) {
        if (mEntranceData.isEmpty()) {
            return;
        }
        this.mEntranceData.clear();
        this.mEntranceData.addAll(mEntranceData);
    }

    @Override
    public void onBindViewHolder(@NonNull EntranceAdapter.ViewHolder holder, int position) {
        EntranceData mData = mEntranceData.get(position);
        holder.onBind(mData);
    }

    @Override
    public int getItemCount() {
        return mEntranceData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAUISceneIcon;
        private TextView mAUISceneTitle;
        private TextView mAUISceneDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAUISceneIcon = itemView.findViewById(R.id.aui_scene_list_item_image);
            mAUISceneTitle = itemView.findViewById(R.id.aui_scene_list_item_title);
            mAUISceneDesc = itemView.findViewById(R.id.aui_scene_item_desc);
        }

        private void onBind(EntranceData mData) {
            mAUISceneTitle.setText(mData.title);
            mAUISceneDesc.setText(mData.desc);
            mAUISceneIcon.setImageResource(mData.icon);
        }
    }
}
