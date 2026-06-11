package com.alivc.player.scenes.shortplaylist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListActivity;
import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListView;
import com.alivc.player.playerkits.shortvideolist.data.PlaylistInfo;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.listener.OnLoadDataListener;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModel;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModelFactory;
import com.alivc.player.scenes.shortplaylist.R;
import com.alivc.player.scenes.shortplaylist.util.AUIShortPlaylistUtil;
import com.alivc.player.scenes.shortplaylist.view.AUIShortPlaylistDetailView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短剧剧场场景-剧场推荐页
 */
public class AUIShortPlaylistRecommendFragment extends Fragment {
    public static final String KEY_RECOMMEND_PLAYLIST_DATA = "recommendPlaylistData";

    private VideoInfo videoInfo;
    private AUIShortVideoListViewModel<List<VideoInfo>> mVideoListViewModel;
    private final AUIShortVideoListViewModel.DataProvider<List<VideoInfo>> dataProvider = new AUIShortVideoListViewModel.DataProvider<List<VideoInfo>>() {
        @Override
        public void onLoadData(AUIShortVideoListViewModel.DataCallback<List<VideoInfo>> callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<VideoInfo> playlistData = getPlaylistData();
                    if (callback != null && playlistData != null) {
                        callback.onData(playlistData);
                    }
                }
            }).start();
        }
    };

    private AUIShortVideoListView mShortVideoListView;
    private AUIShortPlaylistDetailView mAUIShortPlaylistDetailView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene_recommend_list, container, false);
        mVideoListViewModel = new ViewModelProvider(this, new AUIShortVideoListViewModelFactory<>(dataProvider)).get(AUIShortVideoListViewModel.class);
        initView(view);
        initObserver();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView(View view) {
        mShortVideoListView = view.findViewById(R.id.v_short_playlist);
        mShortVideoListView.showPlayTitleContent(true);
        mAUIShortPlaylistDetailView = view.findViewById(R.id.v_short_playlist_details);
        if (getPlaylistData().size() > 0) {
            mAUIShortPlaylistDetailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireActivity(), AUIShortVideoListActivity.class);

                    List<VideoInfo> chooseEpisodeData = setChooseEpisodeData(mShortVideoListView.getCurrentPosition());
                    // convert List<VideoInfo> to serialized JSON string
                    String videoInfoListJSON = AUIShortVideoListUtil.serializeVideoInfoListToJson(chooseEpisodeData);
                    intent.putExtra(AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA, videoInfoListJSON);

                    long currentTimeWithLong = mShortVideoListView.getCurrentTimeWithLong();
                    intent.putExtra(AUIShortVideoListView.KEY_VIDEO_INFO_START_TIME, currentTimeWithLong);

                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    mAUIShortPlaylistDetailView.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initObserver() {
        mVideoListViewModel.mLoadMoreVideoListLiveData.observe(getViewLifecycleOwner(), new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mShortVideoListView.addSources(videoInfos);
            }
        });

        mVideoListViewModel.mRefreshVideoListLiveData.observe(getViewLifecycleOwner(), new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mShortVideoListView.loadSources(videoInfos);
            }
        });

        mShortVideoListView.setOnRefreshListener(new OnLoadDataListener() {
            @Override
            public void onRefresh() {
                mVideoListViewModel.loadData(true);
            }

            @Override
            public void onLoadMore() {
                mVideoListViewModel.loadData(false);
            }
        });
    }

    private List<PlaylistInfo> setData() {
        Bundle args = getArguments();
        List<PlaylistInfo> playlist = new ArrayList<>();
        if (args != null) {
            String json = args.getString(KEY_RECOMMEND_PLAYLIST_DATA);
            playlist = AUIShortPlaylistUtil.deserializePlaylistInfoListFromJson(json);
            return playlist;
        }
        return playlist;
    }

    private List<VideoInfo> getPlaylistData() {
        List<PlaylistInfo> playlistInfoList = setData(); // 假设这个方法返回 List<PlaylistInfo>
        if (playlistInfoList == null || playlistInfoList.isEmpty()) {
            return new ArrayList<>();
        }

        List<VideoInfo> videoInfoList = new ArrayList<>();

        for (PlaylistInfo playlistInfo : playlistInfoList) {
            if (playlistInfo.playlistVideos != null && !playlistInfo.playlistVideos.isEmpty()) {
                // 取第一个视频
                VideoInfo videoInfo = playlistInfo.playlistVideos.get(0);

                videoInfoList.add(videoInfo);
            }
        }

        return videoInfoList;
    }


    private List<VideoInfo> setChooseEpisodeData(int index) {
        List<VideoInfo> choosePlaylist = new ArrayList<>();
        List<PlaylistInfo> playlist = setData();
        if (playlist == null) {
            return null;
        }
        List<PlaylistInfo> videoInfos = setData();
        VideoInfo mChooseData = new VideoInfo();
        for (int i = 0; i < videoInfos.get(index).playlistVideos.size(); i++) {
            mChooseData = videoInfos.get(index).playlistVideos.get(i);
            videoInfo = new VideoInfo();
            videoInfo.videoId = mChooseData.videoId;
            videoInfo.playAuth = mChooseData.playAuth;
            videoInfo.coverUrl = mChooseData.coverUrl;
            videoInfo.title = mChooseData.title;
            videoInfo.description = mChooseData.description;
            videoInfo.playlistId = mChooseData.playlistId;
            choosePlaylist.add(videoInfo);
        }
        return choosePlaylist;
    }

    @Override
    public void onResume() {
        super.onResume();
        mShortVideoListView.updateUI();
        mAUIShortPlaylistDetailView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
