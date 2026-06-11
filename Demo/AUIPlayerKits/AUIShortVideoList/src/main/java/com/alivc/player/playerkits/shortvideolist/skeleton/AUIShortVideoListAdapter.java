package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.R;
import com.alivc.player.playerkits.shortvideolist.component.AUIShortVideoSelectionPanel;
import com.alivc.player.playerkits.shortvideolist.controller.player.AliPlayerPool;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.listener.OnEnterNextPageListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnPanelEventListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnViewHolderCreateListener;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放ListAdapter适配器
 */
public class AUIShortVideoListAdapter extends AUIVideoListAdapter {
    private OnViewHolderCreateListener mOnViewHolderCreateListener;
    private WeakReference<RecyclerView> mRecyclerView;
    private AUIShortVideoSelectionPanel mPanelComponent;

    private HashSet<Integer> currentPositonSet = new HashSet<>();

    private int selectedPosition = RecyclerView.NO_POSITION;

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public AUIShortVideoListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public AUIVideoListViewHolder customCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_short_video_list_video_item, parent, false);
        if (getStartPlayPosition() != 0) {
            AUIShortVideoListViewHolder auiShortVideoListViewHolder = new AUIShortVideoListViewHolder(inflate, getStartPlayPosition());
            setStartPlayPosition(0);
            return auiShortVideoListViewHolder;
        }
        return new AUIShortVideoListViewHolder(inflate, 0);
    }

    @Override
    public AUIShortVideoAdvertisementViewHolder advertisementCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_short_video_list_advertisement_item, parent, false);
        return new AUIShortVideoAdvertisementViewHolder(inflate);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        SLog.i(this, "ADAPTER-ATTACH");
        mRecyclerView = new WeakReference<>(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        SLog.i(this, "ADAPTER-DETACH");
        if (mRecyclerView != null) {
            mRecyclerView.clear();
            mRecyclerView = null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof AUIShortVideoListViewHolder) {
            SLog.i(this, "VH-BIND", holder.getAdapterPosition(), holder, currentPositonSet);
            mPanelComponent = holder.itemView.findViewById(R.id.v_panel);
            mPanelComponent.setOnClickListener(new OnPanelEventListener() {
                @Override
                public void onClickRetract() {

                }

                @Override
                public void onItemClicked(VideoInfo selectedVideoInfo) {
                    List<VideoInfo> currentList = getCurrentList();
                    int scrollPosition = currentList.indexOf(selectedVideoInfo); // 查询索引

                    if (scrollPosition != -1) {
                        mRecyclerView.get().scrollToPosition(scrollPosition);
                        mRecyclerView.get().post(() -> mOnViewPagerListener.onPageSelected(scrollPosition, false));
                        mRecyclerView.get().getAdapter().notifyDataSetChanged();
                    }
                }
            });

            mPanelComponent.setVideoInfoList(getCurrentList());

            ((AUIShortVideoListViewHolder) holder).setOnEnterNextPageListener(new OnEnterNextPageListener() {
                @Override
                public void onEnterNextPage(int position) {
                    if (position == getCurrentList().size()) {
                        return;
                    }
                    mRecyclerView.get().smoothScrollToPosition(position);
                }
            });
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        SLog.i(this, "VIEW-RECYCLE", holder.getAdapterPosition(), holder, currentPositonSet);
//        unbindVideoPlayer(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof AUIShortVideoListViewHolder) {

            if (mOnViewHolderCreateListener != null) {
                mOnViewHolderCreateListener.onViewHolderCreate();
            }

            currentPositonSet.add(holder.getAdapterPosition());
            if (currentPositonSet.size() > AliPlayerPool.INITIAL_CAPACITY) {
                if (mRecyclerView.get().findViewHolderForAdapterPosition(selectedPosition) instanceof AUIShortVideoListViewHolder) {
                    AUIShortVideoListViewHolder currentViewHolder = (AUIShortVideoListViewHolder) mRecyclerView.get().findViewHolderForAdapterPosition(selectedPosition);
                    bindVideoPlayer(currentViewHolder);
                }
            }
            SLog.i(this, "VIEW-ATTACH", holder.getAdapterPosition(), holder, currentPositonSet);
            bindVideoPlayer((AUIShortVideoListViewHolder) holder);

            if (holder.getAdapterPosition() == getCurrentList().size() - 1) {
                setLoop((AUIVideoListViewHolder) holder, true);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof AUIShortVideoListViewHolder) {
            currentPositonSet.remove(holder.getAdapterPosition());
            SLog.i(this, "VIEW-DETACH", holder.getAdapterPosition(), holder, currentPositonSet);
            unbindVideoPlayer((AUIShortVideoListViewHolder) holder);
        }
    }

    public void setLoop(AUIVideoListViewHolder holder, boolean looperStart) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).setLoop(looperStart);
        }
    }

    public void pause(AUIVideoListViewHolder holder) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).pause();
        }
    }

    public void start(AUIVideoListViewHolder holder) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).start();
        }
    }

    public void seekTo(AUIVideoListViewHolder holder, long playPosition) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).seek(playPosition);
        }
    }

    public void setStartTime(AUIVideoListViewHolder holder, long time) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).setStartTime(time);
        }
    }

    private void bindVideoPlayer(AUIVideoListViewHolder holder) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).bind();
        }
    }

    public void unbindVideoPlayer(AUIVideoListViewHolder holder) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).unbind();
            ((AUIShortVideoListViewHolder) holder).hidePanelIfNeed();
        }
    }

    public void rebindVideoPlayer(AUIVideoListViewHolder holder) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).rebind();
        }
    }

    public void showTimeSpeed(AUIVideoListViewHolder holder, boolean mTimeSpeedDisplay) {
        if (holder instanceof AUIShortVideoListViewHolder) {
            ((AUIShortVideoListViewHolder) holder).showTimeSpeed(mTimeSpeedDisplay);
        }
    }

    public void setOnViewHolderCreateListener(OnViewHolderCreateListener listener) {
        this.mOnViewHolderCreateListener = listener;
    }

    @Override
    public long getStartPlayPosition() {
        return super.getStartPlayPosition();
    }
}
