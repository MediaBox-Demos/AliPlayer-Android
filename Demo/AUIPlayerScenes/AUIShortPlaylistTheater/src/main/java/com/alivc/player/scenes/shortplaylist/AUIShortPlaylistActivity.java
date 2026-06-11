package com.alivc.player.scenes.shortplaylist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.alivc.player.playerkits.shortvideolist.data.PlaylistInfo;
import com.alivc.player.playerkits.shortvideolist.data.PlayInfoListRequestParams;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModel;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModelFactory;
import com.alivc.player.scenes.shortplaylist.fragment.AUIShortPlaylistDetailFragment;
import com.alivc.player.scenes.shortplaylist.fragment.AUIShortPlaylistRecommendFragment;
import com.alivc.player.scenes.shortplaylist.util.AUIShortPlaylistUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2024/9/12
 * @brief 短剧剧场场景-剧场页
 */
@Route(path = "/shortplaylist/main")
public class AUIShortPlaylistActivity extends AppCompatActivity {
    private Fragment fragment;

    private AUIShortPlaylistDetailFragment mShortPlaylistDetailFragment;

    private AUIShortVideoListViewModel<List<PlaylistInfo>> mVideoListViewModel;

    private final List<PlaylistInfo> playlist = new ArrayList<>();

    private final AUIShortVideoListViewModel.DataProvider<List<PlaylistInfo>> dataProvider = new AUIShortVideoListViewModel.DataProvider<List<PlaylistInfo>>() {
        @Override
        public void onLoadData(AUIShortVideoListViewModel.DataCallback<List<PlaylistInfo>> callback) {
            PlayInfoListRequestParams requestParams = new PlayInfoListRequestParams(
                    AUIShortVideoListConstants.PAGE_NO,
                    AUIShortVideoListConstants.PAGE_SIZE, AUIShortVideoListConstants.SORT_BY
            );
            AUIShortPlaylistUtil.requestPlaylistInfoList(requestParams, new AUIShortVideoListUtil.OnNetworkCallBack<List<PlaylistInfo>>() {
                @Override
                public void onResponse(List<PlaylistInfo> data) {
                    if (callback != null && data != null && !data.isEmpty()) {
                        playlist.clear();
                        playlist.addAll(data);
                        callback.onData(data);
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_playlist);
        mVideoListViewModel = new ViewModelProvider(this, new AUIShortVideoListViewModelFactory<>(dataProvider)).get(AUIShortVideoListViewModel.class);
        initView();
        initObserver();
    }

    private void initView() {
        fragment = new AUIShortPlaylistDetailFragment();
        mShortPlaylistDetailFragment = ((AUIShortPlaylistDetailFragment) fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.v_fragment_container, fragment).commit();
        BottomNavigationView topToolbar = findViewById(R.id.top_toolbar);
        topToolbar.setBackgroundColor(Color.TRANSPARENT);
        topToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.short_playlist_home_detail) {
                    if (fragment instanceof AUIShortPlaylistDetailFragment) {
                        return true;
                    } else {
                        String json = AUIShortPlaylistUtil.serializePlaylistInfoListToJson(playlist);
                        fragment = new AUIShortPlaylistDetailFragment();
                        replaceFragment(fragment, AUIShortPlaylistDetailFragment.KEY_DETAIL_PLAYLIST_DATA, json);
                    }
                } else if (item.getItemId() == R.id.short_playlist_home_recommend) {
                    if (fragment instanceof AUIShortPlaylistRecommendFragment) {
                        return true;
                    } else {
                        String json = AUIShortPlaylistUtil.serializePlaylistInfoListToJson(playlist);
                        fragment = new AUIShortPlaylistRecommendFragment();
                        replaceFragment(fragment, AUIShortPlaylistRecommendFragment.KEY_RECOMMEND_PLAYLIST_DATA, json);
                    }
                }
                return true;
            }
        });
    }

    private void initObserver() {
        mVideoListViewModel.mLoadMoreVideoListLiveData.observe(this, new Observer<List<PlaylistInfo>>() {
            @Override
            public void onChanged(List<PlaylistInfo> playlistInfos) {
                mShortPlaylistDetailFragment.setPlayListInfoList(playlistInfos);
            }
        });
    }

    private void replaceFragment(Fragment fragment, String key, String value) {
        Bundle args = new Bundle();
        args.putString(key, value);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_replace_fade_in, R.anim.fragment_replace_fade_out).replace(R.id.v_fragment_container, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playlist.clear();
    }
}
