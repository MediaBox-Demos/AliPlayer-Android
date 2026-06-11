package com.alivc.player.scenes.shortplaylistfeeds;

import static android.view.View.GONE;
import static com.alivc.player.playerkits.shortvideolist.AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA;

import android.graphics.Color;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.alivc.player.playerkits.shortvideolist.data.PlayInfoListRequestParams;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModel;
import com.alivc.player.playerkits.shortvideolist.viewmodel.AUIShortVideoListViewModelFactory;
import com.alivc.player.scenes.shortplaylistfeeds.adapter.AUIShortPlaylistFeedsFragmentAdapter;
import com.alivc.player.scenes.shortplaylistfeeds.fragment.AUIShortPlaylistFeedsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AUIShortPlaylistFeedsActivity extends AppCompatActivity {
    private AUIShortVideoListViewModel<List<VideoInfo>> mVideoListViewModel;
    private AUIShortPlaylistFeedsFragmentAdapter fragmentAdapter;
    private final List<VideoInfo> videoInfoList = new ArrayList<>();

    private ProgressBar mFeedsLoadingView;
    private final AUIShortVideoListViewModel.DataProvider<List<VideoInfo>> dataProvider = new AUIShortVideoListViewModel.DataProvider<List<VideoInfo>>() {
        @Override
        public void onLoadData(AUIShortVideoListViewModel.DataCallback<List<VideoInfo>> callback) {
            PlayInfoListRequestParams requestParams = new PlayInfoListRequestParams(
                    AUIShortVideoListConstants.PAGE_NO,
                    AUIShortVideoListConstants.PAGE_SIZE, AUIShortVideoListConstants.SORT_BY
            );
            AUIShortVideoListUtil.requestVideoInfoList(requestParams, new AUIShortVideoListUtil.OnNetworkCallBack<List<VideoInfo>>() {
                @Override
                public void onResponse(List<VideoInfo> data) {
                    mFeedsLoadingView.post(() -> mFeedsLoadingView.setVisibility(GONE));
                    if (callback != null && data != null && !data.isEmpty()) {
                        videoInfoList.clear();
                        videoInfoList.addAll(data);
                        callback.onData(data);
                    }
                }
            });
        }
    };

    private final List<Fragment> fragments = new ArrayList<>();
    private BottomNavigationView topToolbar;
    private ViewPager2 mViewPager;
    private boolean mPreload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_feeds);
        mFeedsLoadingView = findViewById(R.id.feeds_loading_view);
        mVideoListViewModel = new ViewModelProvider(this, new AUIShortVideoListViewModelFactory<>(dataProvider)).get(AUIShortVideoListViewModel.class);
        initView();
    }

    private void initView() {
        fragments.add(new AUIShortPlaylistFeedsFragment(R.id.viewpager_one, R.layout.fragment_viewpager_one));
        fragments.add(new AUIShortPlaylistFeedsFragment(R.id.viewpager_two, R.layout.fragment_viewpager_two));
        fragmentAdapter = new AUIShortPlaylistFeedsFragmentAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        initObserver();
        topToolbar = findViewById(R.id.aui_tab_top_toolbar);
        mViewPager = findViewById(R.id.aui_tab_main_vp2);
        // TODO yjh 禁用滑动，目前滑动时播放器有bug.
        mViewPager.setUserInputEnabled(false);
        topToolbar.setBackgroundColor(Color.TRANSPARENT); // 设置背景色为透明
        topToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_page_one) {
                    mViewPager.setCurrentItem(0);
                    return true;
                } else if (item.getItemId() == R.id.tab_page_two) {
                    mViewPager.setCurrentItem(1);
                    return true;
                }
                return false;
            }
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        topToolbar.setSelectedItemId(R.id.tab_page_one);
                        break;
                    case 1:
                        topToolbar.setSelectedItemId(R.id.tab_page_two);
                        if (mPreload) {
                            mViewPager.setOffscreenPageLimit(1);
                        }
                        mPreload = false;
                        break;
                }
            }
        });
    }

    private void initObserver() {
        mVideoListViewModel.mLoadMoreVideoListLiveData.observe(this, new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfoList) {
                String json = AUIShortVideoListUtil.serializeVideoInfoListToJson(videoInfoList);
                Bundle args = new Bundle();
                args.putString(KEY_VIDEO_INFO_LIST_DATA, json);
                fragments.get(0).setArguments(args);
                fragments.get(1).setArguments(args);
                mViewPager.setAdapter(fragmentAdapter);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragments.clear();
        mFeedsLoadingView = null;
    }
}