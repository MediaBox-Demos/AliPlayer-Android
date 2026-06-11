package com.alivc.player.scenes.shortplaylist.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.playerkits.shortvideolist.data.PlaylistInfo;
import com.alivc.player.scenes.shortplaylist.R;
import com.alivc.player.scenes.shortplaylist.adapter.AUIShortPlaylistDetailAdapter;
import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListActivity;
import com.alivc.player.scenes.shortplaylist.util.AUIShortPlaylistUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短剧剧场场景-剧场详情页
 */
public class AUIShortPlaylistDetailFragment extends Fragment {
    public static final String KEY_DETAIL_PLAYLIST_DATA = "detailPlaylistData";

    private RecyclerView mPlaylistDetailRv;
    private AUIShortPlaylistDetailAdapter mAdapter;

    private ProgressBar loading;

    private List<PlaylistInfo> playlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene_playlist_details, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mPlaylistDetailRv = view.findViewById(R.id.rv_playlist_detail_list);
        loading = view.findViewById(R.id.loading_view);
        mAdapter = new AUIShortPlaylistDetailAdapter(getContext());
        mPlaylistDetailRv.setAdapter(mAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mPlaylistDetailRv.setLayoutManager(gridLayoutManager);
        mAdapter.setShortPlaylistEventListener(new AUIShortPlaylistDetailAdapter.OnShortPlaylistEventListener() {
            @Override
            public void onClickPosition(String playlistId) {
                Intent intent = new Intent(getActivity(), AUIShortVideoListActivity.class);
                intent.putExtra("PLAYLIST_ID", playlistId);
                startActivity(intent);
            }

            @Override
            public void onClickJson(String json) {

            }
        });

        // 默认进入时是加载状态
        showLoading();
    }

    public void setPlayListInfoList(List<PlaylistInfo> newPlaylist) {
        if (newPlaylist == null) {
            newPlaylist = Collections.emptyList();
        }

        if (!isResumed()) {
            return;
        }

        this.playlist = new ArrayList<>(newPlaylist);

        mAdapter.setData(newPlaylist);

        if (this.playlist.isEmpty()) {
            showLoading();
        } else {
            showContent();
        }
    }

    private List<PlaylistInfo> parsePlayListInfoListFromArguments() {
        Bundle args = getArguments();
        if (args == null || !args.containsKey(KEY_DETAIL_PLAYLIST_DATA)) {
            return null;
        }
        String json = args.getString(KEY_DETAIL_PLAYLIST_DATA);
        return AUIShortPlaylistUtil.deserializePlaylistInfoListFromJson(json);
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
        mPlaylistDetailRv.setVisibility(View.GONE);
    }

    private void showContent() {
        loading.setVisibility(View.GONE);
        mPlaylistDetailRv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        List<PlaylistInfo> data = null;

        if (playlist != null && !playlist.isEmpty()) {
            data = playlist;
        } else {
            data = parsePlayListInfoListFromArguments();
        }

        if (data != null && !data.isEmpty()) {
            mAdapter.setData(data);
            mAdapter.notifyDataSetChanged();
            showContent();
        } else {
            // 如果还是没有数据，保持 loading 状态
            showLoading();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (playlist != null) {
            playlist.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mPlaylistDetailRv.setAdapter(null);
            mAdapter = null;
        }
        mPlaylistDetailRv = null;
        loading = null;
        playlist.clear();
    }
}
