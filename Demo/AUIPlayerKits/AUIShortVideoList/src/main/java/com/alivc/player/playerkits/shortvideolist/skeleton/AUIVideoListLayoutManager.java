package com.alivc.player.playerkits.shortvideolist.skeleton;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.listener.OnViewPagerListener;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放布局管理器
 */
public class AUIVideoListLayoutManager extends LinearLayoutManager {
    protected final PagerSnapHelper mPagerSnapHelper;
    protected final OrientationHelper mOrientationHelper;

    // 用于跟踪上一个位置
    // For tracking the previous position
    private int mOldPosition = -1;
    private RecyclerView mRecyclerView;
    // true->LAYOUT_START（即向上或向左滚动的方向），false->LAYOUT_END（即向下或向右滚动的方向）
    private boolean isLayoutDirectionStart = false;

    protected OnViewPagerListener mOnViewPagerListener;

    public AUIVideoListLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPagerSnapHelper = new PagerSnapHelper();
        mOrientationHelper = OrientationHelper.createOrientationHelper(this, RecyclerView.VERTICAL);
    }

    public void setOnViewPagerListener(OnViewPagerListener listener) {
        this.mOnViewPagerListener = listener;
    }

    @Override
    public boolean canScrollVertically() {
        // 解决滑动冲突的问题，避免episode panel展开时，当触摸事件发生的时候，list view上下滑的事件被先响应到。
        if (mRecyclerView != null) {
            for (int i = 0; i < mRecyclerView.getChildCount(); ++i) {
                View child = mRecyclerView.getChildAt(i);
                RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(child);
                // 在这里处理ViewHolder的滑动事件
                if (viewHolder instanceof AUIShortVideoListViewHolder) {
                    AUIShortVideoListViewHolder vh = (AUIShortVideoListViewHolder) viewHolder;
                    if (vh.isPanelShow()) {
                        return false;
                    }
                    if (vh.isSpeedPanelShow()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        mRecyclerView = recyclerView;
        // 如果RecyclerView为null，直接返回
        // If RecyclerView is null, return directly
        if (recyclerView == null) {
            return;
        }

        // 负责对齐页面
        // for aligning pages
        mPagerSnapHelper.attachToRecyclerView(recyclerView);
        // 监听子视图的附加和分离
        // for listening to the attachment and detachment of sub-views
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                int position = getPosition(view);
                SLog.i(this, "CHILD-ATTACH", position);
                if (recyclerView.getChildCount() == 1) {
                    SLog.w(this, "PAGE-INIT_COMPLETE", position);
                    if (mOnViewPagerListener != null) {
                        mOnViewPagerListener.onInitComplete();
                    }
                } else {
                    SLog.w(this, "PAGE-PEEK_START", position);
                    if (mOnViewPagerListener != null) {
                        mOnViewPagerListener.onPagePeekStart(position);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                int position = getPosition(view);
                SLog.i(this, "CHILD-DETACH", position);
                if (mOnViewPagerListener != null) {
                    SLog.w(this, "PAGE-RELEASE", position);
                    mOnViewPagerListener.onPageRelease(position);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            View snapView = mPagerSnapHelper.findSnapView(this);
            if (snapView != null && mOnViewPagerListener != null) {
                int position = getPosition(snapView);
                SLog.w(this, "PAGE_SELECT", mOldPosition + "->" + position);
                mOnViewPagerListener.onPageSelected(position, false);
                mOldPosition = position;
            }
        }
    }

    @Override
    protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state, @NonNull int[] extraLayoutSpace) {
        super.calculateExtraLayoutSpace(state, extraLayoutSpace);
        int extraLayoutSpaceStart = 0;
        int extraLayoutSpaceEnd = 0;
        if (isLayoutDirectionStart) {
            extraLayoutSpaceStart = 1;
        } else {
            extraLayoutSpaceEnd = 1;
        }
        extraLayoutSpace[0] = extraLayoutSpaceStart;
        extraLayoutSpace[1] = extraLayoutSpaceEnd;
    }

    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
//        SLog.i(this, "collectAdjacentPrefetchPositions", dx + ", " + dy, state);
        // 根据滑动方向(dx, dy)收集相邻的预取位置
        isLayoutDirectionStart = dy < 0;
    }

//
//    @Override
//    protected int getExtraLayoutSpace(RecyclerView.State state) {
//        // ugly codes, it will makes the viewpager2 scroll carton; but if not set, the viewholder cannot be preloaded
//        return 1;
//    }
}
