package com.aliyun.player.multiresolution;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.nativeclass.TrackInfo;

import java.util.List;

/**
 * 清晰度 列表适配器
 */
public class MultiResolutionAdapter extends RecyclerView.Adapter<MultiResolutionViewHolder> {
    // 清晰度数据
    private List<TrackInfo> dataList;

    // 当前选中的清晰度
    private int selectTrackInfoIndex = 0;

    // 每个item 的 点击监听
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 监听
    private OnItemClickListener mOnItemClickListener;

    /**
     * 绑定视图
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return MultiResolutionViewHolder 清晰度文本视图
     */
    @NonNull
    @Override
    public MultiResolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resolution, parent, false);
        return new MultiResolutionViewHolder(view);
    }

    /**
     * 绑定视图，初始化UI && 设置监听
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MultiResolutionViewHolder holder, int position) {
        holder.bindView(dataList.get(position), position, selectTrackInfoIndex);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
            }
        });
    }

    /**
     * 获取 清晰度数据列表 计数
     *
     * @return multi-resolution data size
     */
    @Override
    public int getItemCount() {
        return (null != dataList && !dataList.isEmpty()) ? dataList.size() : 0;
    }

    /**
     * 返回 清晰度数据列表
     *
     * @return multi-resolution list
     */
    public List<TrackInfo> getDataList() {
        return dataList;
    }

    /**
     * 设置 清晰度数据
     *
     * @param dataList multi-resolution list
     */
    public void setDataList(List<TrackInfo> dataList) {
        this.dataList = dataList;
    }

    /**
     * 更新选中的下标，并更新UI
     * Update the selected subscript && Update the UI
     *
     * @param index selectIndex
     */
    public void selectTrackInfo(int index) {
        int lastSelectTrackInfoIndex = selectTrackInfoIndex;
        selectTrackInfoIndex = index;
        Log.e("MultiResolutionActivity", "lastIndex" + lastSelectTrackInfoIndex + "/ currentIndex" + selectTrackInfoIndex);

        notifyItemChanged(lastSelectTrackInfoIndex);
        notifyItemChanged(selectTrackInfoIndex);
    }

    /**
     * 获取列表中选中的下标值
     *
     * @return selectIndex in recycleView
     */
    public int getSelectTrackInfoIndex() {
        return selectTrackInfoIndex;
    }

    /**
     * 设置 每个ViewHolder 的click 监听
     *
     * @param mOnItemClickListener clickListener
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}



