package com.alivc.player.playerkits.shortvideolist;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.smallwindow.FloatingWindowService;
import com.alivc.player.playerkits.shortvideolist.smallwindow.SmallWindowConstants;
import com.alivc.player.playerkits.shortvideolist.listener.OnLoadDataListener;
import com.alivc.player.playerkits.shortvideolist.smallwindow.OnSmallWindowListener;
import com.alivc.player.playerkits.shortvideolist.skeleton.AUIVideoListViewHolder;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModel;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModelFactory;
import com.aliyun.player.bean.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2024/9/12
 * @brief 短视频列表播放Fragment页面
 */
public class AUIShortVideoListFragment extends Fragment {
    private AUIShortVideoListViewModel<List<VideoInfo>> mShortVideoListViewModel;
    private AUIShortVideoListView mShortVideoListView;

    private final List<VideoInfo> videoInfoList = new ArrayList<>();

    private FloatingWindowService mService;

    private boolean isBound = false;

    // 该变量用于记录，是否点击按钮返回
    private boolean isClickBtn = false;
    private final AUIShortVideoListViewModel.DataProvider<List<VideoInfo>> dataProvider = new AUIShortVideoListViewModel.DataProvider<List<VideoInfo>>() {
        @Override
        public void onLoadData(AUIShortVideoListViewModel.DataCallback<List<VideoInfo>> callback) {
            if (getArguments() == null) {
                Toast.makeText(getContext(), getString(R.string.check_network_tip), Toast.LENGTH_SHORT).show();
                return;
            }

            List<VideoInfo> data = getVideoInfoListFromArguments(getArguments());

            if (data == null) {
                return;
            }

            if (data.size() == 1) {
                String firstPlaylistId = data.get(0).playlistId;
                if (firstPlaylistId == null) {
                    return;
                }

                AUIShortVideoListUtil.requestVideoInfoList(firstPlaylistId, new AUIShortVideoListUtil.OnNetworkCallBack<List<VideoInfo>>() {
                    @Override
                    public void onResponse(List<VideoInfo> data) {
                        if (callback != null){
                            callback.onData(data);
                        }
                    }
                });
            } else {
                videoInfoList.clear();
                videoInfoList.addAll(data);
                if (callback != null) {
                    callback.onData(videoInfoList);
                }
            }
        }
    };

    private boolean mPendingShowFloatingWindow = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_video_list, container, false);
        mShortVideoListViewModel = new ViewModelProvider(this, new AUIShortVideoListViewModelFactory<>(dataProvider)).get(AUIShortVideoListViewModel.class);
        initView(view);
        initObserver();
        Intent intent = new Intent(getContext(), FloatingWindowService.class);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        videoInfoList.clear();

        // 清除回调，避免内存泄漏
        if (isBound && mService != null) {
            mService.setSmallWindowListener(null);
        }

        // 解绑Service
        if (isBound) {
            getContext().unbindService(serviceConnection);
            isBound = false;
        }

        // 停止Service
        Intent intent = new Intent(getContext(), FloatingWindowService.class);
        getContext().stopService(intent);
    }

    private void initView(View view) {
        mShortVideoListView = view.findViewById(R.id.aui_video_list_view);
        mShortVideoListView.showPlayTitleContent(true);
        if (startPlayTime() > 0) {
            mShortVideoListView.startPlayTimePosition(startPlayTime());
        }
//        mShortVideoListView.openLoopPlay(false);
//        mShortVideoListView.autoPlayNext(true);
    }

    private void initObserver() {
        mShortVideoListViewModel.mLoadMoreVideoListLiveData.observe(getViewLifecycleOwner(), new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mShortVideoListView.addSources(videoInfos);
            }
        });

        mShortVideoListViewModel.mRefreshVideoListLiveData.observe(getViewLifecycleOwner(), new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mShortVideoListView.loadSources(videoInfos);
            }
        });

        mShortVideoListView.setOnRefreshListener(new OnLoadDataListener() {
            @Override
            public void onRefresh() {
                mShortVideoListViewModel.loadData(true);
                mShortVideoListView.updateUI();
            }

            @Override
            public void onLoadMore() {
                mShortVideoListViewModel.loadData(false);
            }
        });
    }

    // TODO yjh 为什么不能在当前方法的onResume()方法中，内部完成这块的逻辑处理，而是需要外部来调用？
    public void rebindVideoPlayer(int position) {
        AUIVideoListViewHolder viewHolder = mShortVideoListView.getViewHolderByPosition(position);
        mShortVideoListView.rebindVideoPlayer(viewHolder);
    }

    // TODO yjh 为什么不能在当前方法的onResume()方法中，内部完成这块的逻辑处理，而是需要外部来调用？
    public void unbindVideoPlayer(int position) {
        AUIVideoListViewHolder viewHolder = mShortVideoListView.getViewHolderByPosition(position);
        mShortVideoListView.unbindVideoPlayer(viewHolder);
    }

    public void pause(int position) {
        if (mShortVideoListView != null) {
            mShortVideoListView.pause(position);
        }
    }

    public void start(int position) {
        if (mShortVideoListView != null) {
            mShortVideoListView.start(position);
        }
    }

    public int getVideoPosition() {
        return (mShortVideoListView != null) ? mShortVideoListView.getVideoPosition() : -1;
    }

    public long startPlayTime() {
        long currentTime = 0;
        Bundle args = getArguments();
        if (args != null && args.containsKey(AUIShortVideoListView.KEY_VIDEO_INFO_START_TIME)) {
            currentTime = args.getLong(AUIShortVideoListView.KEY_VIDEO_INFO_START_TIME);
        }
        return currentTime;
    }

    /**
     * 从Bundle中获取视频信息(bundle -> getString-> List<VideoInfo>)
     *
     * @param bundle arguments from fragment
     * @return List<VideoInfo>
     */
    private static List<VideoInfo> getVideoInfoListFromArguments(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA)) {
            return null;
        }
        String json = bundle.getString(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA);
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        return AUIShortVideoListUtil.deserializeVideoInfoListFromJson(json);
    }

    // 显示悬浮窗
    private void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
            // TODO: 是否开启每次返回后台时，弹出权限配置页
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getContext().getPackageName()));
//            startActivityForResult(intent, 1000);
        } else {
            startFloatingWindowService();
        }
    }

    // 开启悬浮窗服务
    private void startFloatingWindowService() {
        if (isBound && mService != null) {
            VideoInfo currentVideoInfo = mShortVideoListView.getCurrentVideoInfoToPip(getVideoPosition());
            long currentPlayPosition = mShortVideoListView.getCurrentTimeWithLong();

            // 添加空指针检查
            if (currentVideoInfo != null && !TextUtils.isEmpty(currentVideoInfo.videoId)) {
                mService.syncVideoIndex(getVideoPosition());
                mService.handleIntent(currentVideoInfo, currentPlayPosition);
            }
        } else {
            // 标记需要显示悬浮窗，等待Service连接
            mPendingShowFloatingWindow = true;

            // 同时启动Service（防止bindService失败）
            Intent intent = new Intent(getContext(), FloatingWindowService.class);
            VideoInfo currentVideoInfo = mShortVideoListView.getCurrentVideoInfoToPip(getVideoPosition()); // 使用当前位置
            if (currentVideoInfo != null) {
                long currentPlayTime = mShortVideoListView.getCurrentTimeWithLong();
                intent.putExtra(SmallWindowConstants.KEY_VIDEO_INFO, currentVideoInfo);
                intent.putExtra(SmallWindowConstants.KEY_CURRENT_PLAY_POSITION, currentPlayTime);
                getContext().startService(intent);
            }
        }
    }

    // 隐藏悬浮窗
    private void hideFloatingWindow() {
        // 通过绑定的Service直接隐藏悬浮窗，而不是停止整个Service
        if (isBound && mService != null) {
            mService.hideFloatingWindow();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(getContext())) {
                startFloatingWindowService();
            } else {
                Toast.makeText(getContext(), "需要悬浮窗权限才能使用此功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FloatingWindowService.MyBinder binder = (FloatingWindowService.MyBinder) service;
            mService = binder.getService();
            isBound = true;

            // 注册回调接口
            mService.setSmallWindowListener(mOnSmallWindowListener);

            // 如果有待执行的显示悬浮窗操作
            if (mPendingShowFloatingWindow) {
                mPendingShowFloatingWindow = false;
                VideoInfo currentVideoInfo = mShortVideoListView.getCurrentVideoInfoToPip(getVideoPosition());
                if (currentVideoInfo != null && !TextUtils.isEmpty(currentVideoInfo.videoId)) {
                    long currentPlayPosition = mShortVideoListView.getCurrentTimeWithLong();
                    mService.syncVideoIndex(getVideoPosition());
                    mService.handleIntent(currentVideoInfo, currentPlayPosition);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            mService = null;
        }
    };

    // 小窗与控制器之间的事件回调监听
    private OnSmallWindowListener mOnSmallWindowListener = new OnSmallWindowListener() {
        @Override
        public void onSmallWindowPrepared(int index, long durationMs) {
            // 小窗准备完成时，先暂停前台播放器
            mShortVideoListView.pause(index);
        }

        @Override
        public void onSmallWindowCompleted(int index) {
            // 小窗播放完成时，同步位置
            mShortVideoListView.setOnVideoInfoSync(index);
        }

        @Override
        public VideoInfo onSmallWindowRequestSwitch(int newIndex) {
            mShortVideoListView.updateViewHolder();
            // 小窗切集时，先暂停前台当前播放的视频
            int currentForegroundPosition = mShortVideoListView.getVideoPosition();
            mShortVideoListView.pause(currentForegroundPosition);

            // 同步前台位置到新的视频位置，但不开始播放
            mShortVideoListView.setOnVideoInfoSync(newIndex);
            VideoInfo videoInfo = new VideoInfo();

            String playlistVideoId = mShortVideoListView.getCurrentVideoInfoToPip(newIndex).videoId;
            String playAuth = mShortVideoListView.getCurrentVideoInfoToPip(newIndex).playAuth;
            if (playlistVideoId == null && playAuth == null) {
                return null;
            }
            videoInfo.videoId = playlistVideoId;
            videoInfo.playAuth = playAuth;
            return videoInfo;
        }

        @Override
        public void onSmallWindowClosed(int index, long playPosition) {
            isClickBtn = true;
            hideFloatingWindow();
            mShortVideoListView.syncVideoPositionAndTime(index, playPosition);
            mShortVideoListView.pause(index);
        }

        @Override
        public void onSmallWindowError(int index, ErrorInfo errorInfo) {
            // 小窗出错时，恢复前台播放
            mShortVideoListView.start(mShortVideoListView.getVideoPosition());
        }

        @Override
        public void onSmallWindowReturn(int index, long playPosition) {
            hideFloatingWindow();
            isClickBtn = true;
            mShortVideoListView.syncVideoPositionAndTime(index, playPosition);
            mShortVideoListView.start(index);
            // 启动或恢复主Activity
            if (getActivity() != null) {
                Intent intent = new Intent(getContext(), getActivity().getClass());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // 创建转场动画选项
                ActivityOptions options = ActivityOptions.makeCustomAnimation(
                        getContext(),
                        R.anim.slide_in_from_bottom,  // 进入动画
                        R.anim.slide_out_to_top       // 退出动画
                );

                startActivity(intent, options.toBundle());
            }
        }

        @Override
        public void onSmallWindowHide(int index, long playPosition) {
            // 同步位置和播放时间
            mShortVideoListView.syncVideoPositionAndTime(index, playPosition);
            mShortVideoListView.start(index);
        }
    };

    private void activateFloatingWindow() {
        // 检查Fragment是否正在被移除或销毁
        if (isRemoving() || getActivity() == null || getActivity().isFinishing()) {
            // Fragment正在被移除或Activity正在结束，不显示悬浮窗
            return;
        }

        // 强制暂停所有播放器，避免多个播放器同时播放
        mShortVideoListView.pauseAllPlayers();

        // 确保有有效的视频信息才显示悬浮窗
        VideoInfo currentVideoInfo = mShortVideoListView.getCurrentVideoInfoToPip(getVideoPosition());
        if (currentVideoInfo != null && !TextUtils.isEmpty(currentVideoInfo.videoId)) {
            showFloatingWindow();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activateFloatingWindow();
    }

    @Override
    public void onResume() {
        if (!isClickBtn) {
            hideFloatingWindow();
        } else {
            isClickBtn = false;
        }
        super.onResume();
    }
}
