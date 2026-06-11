package com.alivc.player.playerkits.shortvideolist;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alivc.player.playerkits.shortvideolist.business.trackinfo.AUIVideoTrackInfoPanelView;
import com.alivc.player.playerkits.shortvideolist.controller.AUIShortVideoListController;
import com.alivc.player.playerkits.shortvideolist.controller.player.AliVideoView;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.listener.OnLoadDataListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnPlayerEventListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnProhibitRefreshListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnSeekChangedListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnViewHolderItemClickListener;
import com.alivc.player.playerkits.shortvideolist.listener.OnViewPagerListener;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIShortVideoListAdapter;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIShortVideoListViewHolder;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIVideoListAdapter;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIVideoListDiffCallback;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIVideoListLayoutManager;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIVideoListViewHolder;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2023/9/26
 * @brief 短视频列表播放View组件
 */
public class AUIShortVideoListView extends FrameLayout implements OnViewHolderItemClickListener, OnPlayerEventListener, OnSeekChangedListener, OnViewPagerListener {
    private static final int DEFAULT_PRELOAD_NUMBER = 5;

    /**
     * Lifecycle
     */
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    private AUIVideoListLayoutManager mAUIVideoListLayoutManager;

    protected int mSelectedPosition;
    private OnLoadDataListener mOnLoadDataListener;
    public SwipeRefreshLayout mRefreshLayout;
    protected AUIVideoListAdapter mAUIVideoListAdapter;
    private boolean mIsLoadMore = false;
    protected final List<VideoInfo> mDataList = new ArrayList<>();

    public static final String KEY_VIDEO_INFO_START_TIME = "VIDEO_INFO_START_TIME";
    public static final String KEY_VIDEO_INFO_LIST_DATA = "VIDEO_INFO_LIST_DATA";

    private int currentIndex;
    private AUIShortVideoListController mController;
    private AUIVideoListViewHolder mViewHolderForAdapterPosition;
    private boolean mAutoPlayNext;

    private int mSelectBandWidth;

    public AUIShortVideoListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIShortVideoListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIShortVideoListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View mInflateView = LayoutInflater.from(mContext).inflate(R.layout.ilr_view_short_video_list_view, this, true);
        mController = new AUIShortVideoListController(context);
        mRecyclerView = mInflateView.findViewById(R.id.recyclerview);
        mRefreshLayout = mInflateView.findViewById(R.id.refresh);
        mRefreshLayout.setOnRefreshListener(() -> {
            if (mOnLoadDataListener != null) {
                mOnLoadDataListener.onRefresh();
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
//        mRecyclerView.setItemViewCacheSize(5);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mAUIVideoListAdapter = initAUIVideoListAdapter(mContext);
        mAUIVideoListLayoutManager = initLayoutManager();
        mAUIVideoListLayoutManager.setOnViewPagerListener(this);

        mRecyclerView.setLayoutManager(mAUIVideoListLayoutManager);
        mRecyclerView.setAdapter(mAUIVideoListAdapter);
        mAUIVideoListAdapter.setOnViewPagerListener(this);
        mAUIVideoListAdapter.setOnItemClickListener(this);
        mAUIVideoListAdapter.setOnSeekBarStateChangeListener(this);
        mAUIVideoListAdapter.setOnPlayerListener(this);
    }

    protected AUIVideoListLayoutManager initLayoutManager() {
        return new AUIVideoListLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    }


    protected AUIVideoListAdapter initAUIVideoListAdapter(Context context) {
        mAUIVideoListAdapter = new AUIShortVideoListAdapter(new AUIVideoListDiffCallback());
        return mAUIVideoListAdapter;
    }

    public void showPlayIcon(boolean isShow) {
        if (mViewHolderForAdapterPosition != null) {
            mViewHolderForAdapterPosition.showPlayIcon(isShow);
        }
    }

    protected AUIVideoListViewHolder getViewHolderByPosition(int position) {
        return (AUIVideoListViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
    }

    protected AUIVideoListViewHolder findRecyclerViewLastVisibleHolder() {
        int lastVisibleItemPosition = mAUIVideoListLayoutManager.findLastVisibleItemPosition();
        return (AUIVideoListViewHolder) mRecyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRecyclerView.setAdapter(null);
        mController.destroy();
    }

    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    public void onItemClick(int position) {
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(position);
        if (viewHolderByPosition != null) {
            viewHolderByPosition.changePlayState();
            ((AUIShortVideoListViewHolder) viewHolderByPosition).hidePanelIfNeed();
            ((AUIShortVideoListViewHolder) viewHolderByPosition).hideSpeedPanel();
        }
    }

    public void onPageSelected(int position, boolean autoStart) {
        if (mDataList.size() - position < DEFAULT_PRELOAD_NUMBER && !mIsLoadMore) {
            // 正在加载中, 防止网络太慢或其他情况造成重复请求列表
            // Loading, to prevent slow networks and other conditions from duplicating the request list.
            mIsLoadMore = true;
//            if (mOnLoadDataListener != null) {
//                mOnLoadDataListener.onLoadMore();
//            }
        }
        if (position == mDataList.size() - 1) {
            Toast.makeText(mContext, R.string.alivc_player_tip_last_video, Toast.LENGTH_SHORT).show();
        }

        // when viewHolder is empty, don't need to call onPageSelected, because it's not ready just now
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.updatePositionByPageSelect(position);
            SLog.i(this, "PAGE_SELECT", position, viewHolder);
        }
        if (mAUIVideoListAdapter instanceof AUIShortVideoListAdapter) {
            this.mSelectedPosition = position;
            mController.updatePositionByPageSelect(position);
            if (viewHolder instanceof AUIShortVideoListViewHolder) {
                AUIShortVideoListViewHolder shortVideoListViewHolder = (AUIShortVideoListViewHolder) viewHolder;
                shortVideoListViewHolder.setEpisodeSelectVideoInfo(mDataList.get(position));
                if (!autoStart) {
                    shortVideoListViewHolder.start();
                }
                shortVideoListViewHolder.setOnProhibitRefreshListener(new OnProhibitRefreshListener() {
                    @Override
                    public void setOnProhibitRefresh(boolean isProhibit) {
                        if (isProhibit) {
                            if (mRefreshLayout == null) {
                                SLog.e(this, "mRefreshLayout is null");
                                return;
                            }
                            mRefreshLayout.setEnabled(false);
                        }
                    }
                });
            }
            SLog.i(this, "PAGE_SELECT", position, viewHolder);
            ((AUIShortVideoListAdapter) mAUIVideoListAdapter).setSelectedPosition(position);
        }
    }

    public void onPageRelease(int position) {
        // viewHolder cannot get from adapter, because it's already detached from window;
        // So we don't need to check if viewHolder is empty
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder instanceof AUIShortVideoListViewHolder) {
            AUIShortVideoListViewHolder shortVideoListViewHolder = (AUIShortVideoListViewHolder) viewHolder;
            shortVideoListViewHolder.unbind();
            mRefreshLayout.setEnabled(true);
        }
        SLog.i(this, "PAGE_RELEASE", position, viewHolder);
    }

    /*
     * =============================================
     */

    public void setOnRefreshListener(OnLoadDataListener listener) {
        this.mOnLoadDataListener = listener;
    }

    public void loadSources(List<VideoInfo> videoBeanList) {
        this.mDataList.clear();
        this.mDataList.addAll(videoBeanList);
        mAUIVideoListAdapter.submitList(videoBeanList);
        mController.loadSources(videoBeanList);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            /*
                刷新后，ListAdapter.submitList 不会刷新 RecyclerView，导致 onPageSelected 没有触发，导致最终不会调用 moveTo
                有两种解决方法：
                    1.如下，如果是刷新加载数据，手动调用 onPageSelected(0)。
                    2.创建新的 List 对象，设置给 ListAdapter。
             */
            /*
                After refreshing, ListAdapter.submitList will not refresh the RecyclerView, resulting in onPageSelected not being triggered, leading to moveTo not being called eventually.
                There are two solutions:
                    1. as below, if it is refreshing to load data, call onPageSelected(0) manually.
                    2. Create a new List object and set it to ListAdapter.
             */
            MoveToPosition(0);
            onPageSelected(0, false);
        }
    }

    public void addSources(List<VideoInfo> videoBeanList) {
        mController.addSource(videoBeanList);
        mIsLoadMore = false;
        this.mDataList.addAll(mDataList.size(), videoBeanList);
        mAUIVideoListAdapter.submitList(mDataList);
    }

    public void MoveToPosition(int position) {
        mRecyclerView.post(() -> mRecyclerView.scrollToPosition(position));
    }

    public void showPlayTitleContent(boolean open) {
        AUIVideoListViewHolder.enableTitleTextView(open);
        AUIVideoListViewHolder.enableAuthTextView(open);
    }

    public void setRefreshing(boolean isRefresh) {
        mRefreshLayout.setRefreshing(isRefresh);
    }

    public void setRefreshLayoutEnable(boolean enable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setEnabled(enable);
        }
    }

    /**
     * video duration
     *
     * @param duration company ms
     */
    public void onVideoFrameShow(int position, long duration) {
        mViewHolderForAdapterPosition = findRecyclerViewLastVisibleHolder();
        AUIVideoListViewHolder videoListViewHolder = getViewHolderByPosition(position);
        if (videoListViewHolder != null && videoListViewHolder.getSeekBar() != null) {
            videoListViewHolder.getSeekBar().setMax((int) duration);
        }
    }

    /**
     * update Current Position
     *
     * @param extraValue ms
     */
    public void updateCurrentPosition(int position, long extraValue) {
        AUIVideoListViewHolder videoListViewHolder = getViewHolderByPosition(position);
        if (videoListViewHolder != null && videoListViewHolder.getSeekBar() != null) {
            videoListViewHolder.getSeekBar().setProgress((int) extraValue);
        }
    }

    public void showError(ErrorInfo errorInfo) {
        Toast.makeText(mContext, "error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg(), Toast.LENGTH_SHORT).show();
    }

    public void loadMore() {
        Toast.makeText(mContext, R.string.alivc_player_tip_last_video, Toast.LENGTH_SHORT).show();
    }

    public void openLoopPlay(boolean openLoopPlay) {
    }

    public void autoPlayNext(boolean autoPlayNext) {
        this.mAutoPlayNext = autoPlayNext;
    }

    @Override
    public void onBackPress() {
        if (mContext instanceof Activity) {
            if (!((Activity) mContext).isFinishing()) {
                ((Activity) mContext).finish();
            }
        }
    }

    @Override
    public void onItemLongClick(int position, boolean touch) {
        ((AUIShortVideoListAdapter) mAUIVideoListAdapter).showTimeSpeed(getViewHolderByPosition(position), touch);
    }

    @Override
    public void onInitComplete() {
        if (mController != null) {
            mController.onInitComplete();
        }
    }

    @Override
    public void onPagePeekStart(int position) {
        SLog.i(this, "PEEK_START", position);
        if (mController != null) {
            mController.updatePositionWhenFastSlide(position);
        }
    }

    /**
     * Player Listener onPrepared
     */
    @Override
    public void onPrepared(int position) {
        // Player has been prepared, only when position is current position, then start player; if not, do nothing.
        if (position != mSelectedPosition) {
            AUIVideoListViewHolder viewHolder = getViewHolderByPosition(mSelectedPosition);
            if (viewHolder instanceof AUIShortVideoListViewHolder) {
                AUIShortVideoListViewHolder auiShortVideoListViewHolder = (AUIShortVideoListViewHolder) viewHolder;
                auiShortVideoListViewHolder.setOnTrackInfoListener(new AUIVideoTrackInfoPanelView.OnTrackInfoListener() {
                    @Override
                    public void onTrackInfoListUpdated(List<TrackInfo> trackInfoList) {

                    }

                    @Override
                    public void onTrackInfoSelected(TrackInfo trackInfo) {
                        if (trackInfo != null) {
                            mSelectBandWidth = trackInfo.getVideoBitrate();
                            mController.setBandWidth(mSelectBandWidth);
                            if (mAUIVideoListAdapter instanceof AUIShortVideoListAdapter) {
                                mAUIVideoListAdapter.setSelectBandWidth(mSelectBandWidth);
                            }
                        }
                    }
                });
            }
            return;
        }
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder instanceof AUIShortVideoListViewHolder) {
            AliVideoView videoView = ((AUIShortVideoListViewHolder) viewHolder).getVideoView();
            videoView.start();
        }
    }

    @Override
    public void onInfo(int position, InfoBean infoBean) {
        if (infoBean.getCode() == InfoCode.CurrentPosition) {
            updateCurrentPosition(position, infoBean.getExtraValue());
        }
    }

    @Override
    public void onPlayStateChanged(int position, boolean isPaused) {
        if (position == mSelectedPosition) {
            showPlayIcon(isPaused);
            currentIndex = position;
        }
    }

    @Override
    public void onRenderingStart(int position, long duration) {
        onVideoFrameShow(position, duration);
    }

    @Override
    public void onCompletion(int position) {
        if (position < mAUIVideoListAdapter.getItemCount() && mAutoPlayNext) {
            mRecyclerView.smoothScrollToPosition(position + 1);
        }
    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onSeek(int position, long seekPosition) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder instanceof AUIShortVideoListViewHolder) {
            AliVideoView videoView = ((AUIShortVideoListViewHolder) viewHolder).getVideoView();
            videoView.seekTo(seekPosition);
        }
    }

    public int getCurrentPosition() {
        return currentIndex;
    }

    public void rebindVideoPlayer(AUIVideoListViewHolder holder) {
        ((AUIShortVideoListAdapter) mAUIVideoListAdapter).rebindVideoPlayer(holder);
    }

    public void unbindVideoPlayer(AUIVideoListViewHolder holder) {
        ((AUIShortVideoListAdapter) mAUIVideoListAdapter).unbindVideoPlayer(holder);
    }

    public void pause(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        ((AUIShortVideoListAdapter) mAUIVideoListAdapter).pause(viewHolder);
    }

    public void start(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        ((AUIShortVideoListAdapter) mAUIVideoListAdapter).start(viewHolder);
    }

    public void seekTo(int position, long playPosition) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        ((AUIShortVideoListAdapter) mAUIVideoListAdapter).seekTo(viewHolder, playPosition);
    }

    public void setStartTime(int position, long time) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        ((AUIShortVideoListAdapter) mAUIVideoListAdapter).setStartTime(viewHolder, time);
    }

    public long getCurrentTimeWithLong() {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(currentIndex);
        if (viewHolder == null) {
            return 0;
        }
        return viewHolder.getCurrentTimeWithLong();
    }

    public void startPlayTimePosition(long playPosition) {
        mAUIVideoListAdapter.setStartPlayPosition(playPosition);
    }

    public void updateUI() {
        mAUIVideoListAdapter.notifyDataSetChanged();
    }

    public int getVideoPosition() {
        return mSelectedPosition;
    }

    public VideoInfo getCurrentVideoInfoToPip(int position) {
        if (mDataList == null || mDataList.isEmpty()) {
            return null;
        }

        // 将 position 限制在有效范围内 [0, size - 1]
        int size = mDataList.size();
        int index = position < 0 ? 0 : (position >= size ? size - 1 : position);
        return mDataList.get(index);
    }

    public void setOnVideoInfoSync(final int position) {
        MoveToPosition(position);
        mRecyclerView.post(() -> {
            onPageSelected(position, true);
        });
    }

    /**
     * 强制暂停所有播放器
     */
    public void pauseAllPlayers() {
        // 暂停当前位置的播放器
        if (mSelectedPosition >= 0 && mSelectedPosition < mDataList.size()) {
            pause(mSelectedPosition);
        }

        // 遍历所有可见的ViewHolder，确保都暂停
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            View child = mRecyclerView.getChildAt(i);
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(child);
            if (holder instanceof AUIShortVideoListViewHolder) {
                ((AUIShortVideoListViewHolder) holder).pause();
            }
        }
    }

    public void updateViewHolder() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * 同步播放位置和时间（专门用于小窗返回时的同步）
     */
    public void syncVideoPositionAndTime(int position, long playPosition) {
        setOnVideoInfoSync(position);

        // 使用递归 post，直到 ViewHolder 出现或超时
        postSeekRunnable(position, playPosition, 5);
    }

    private void postSeekRunnable(int position, long playPosition, int retryCount) {
        mRecyclerView.post(() -> {
            RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(position);
            if (vh != null && vh.itemView != null) {
                seekTo(position, playPosition);
            } else if (retryCount > 0) {
                // 未找到，继续重试
                postSeekRunnable(position, playPosition, retryCount - 1);
            }
        });
    }
}