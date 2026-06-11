package com.alivc.player.playerkits.shortvideolist;

import static android.view.View.GONE;
import static com.alivc.player.playerkits.shortvideolist.AUIShortVideoListView.KEY_VIDEO_INFO_LIST_DATA;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;

import java.util.List;

/**
 * @author junhuiYe
 * @date 2024/9/12
 * @brief 短视频列表播放Activity页面（嵌套Fragment）
 */
@Route(path = "/shortvideolist/main")
public class AUIShortVideoListActivity extends AppCompatActivity {
    private AUIShortVideoListFragment mShortVideoListFragment;

    private ProgressBar mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_short_video_list);
        mLoadingView = findViewById(R.id.home_loading_view);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        // Embed ShortVideoList Fragment into Activity
        if (savedInstanceState == null) {
            // Only initialize the fragment if there's no saved state
            mShortVideoListFragment = new AUIShortVideoListFragment();
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(KEY_VIDEO_INFO_LIST_DATA)) {
                mShortVideoListFragment.setArguments(getIntent().getExtras());
                addFragmentView();
                mLoadingView.post(()-> mLoadingView.setVisibility(GONE));
            } else {
                String playlist = getIntent().getStringExtra("PLAYLIST_ID");
                // TODO：沉浸式播放，网络请求vid，如果未传入playlist，可在此处传入
                if (playlist == null) {
                    playlist = AUIShortVideoListConstants.DEFAULT_VIDEO_ID;
                }

                AUIShortVideoListUtil.requestVideoInfoList(playlist, new AUIShortVideoListUtil.OnNetworkCallBack<List<VideoInfo>>() {
                    @Override
                    public void onResponse(List<VideoInfo> data) {
                        if (data == null || data.isEmpty()) {
                            return;
                        }
                        Bundle args = new Bundle();
                        String videoListJSON = AUIShortVideoListUtil.serializeVideoInfoListToJson(data);

                        if (TextUtils.isEmpty(videoListJSON)) {
                            SLog.e(this, "syncVideoData: JSON serialization returned empty or null. Data count: " + data.size());
                            return;
                        }

                        args.putString(KEY_VIDEO_INFO_LIST_DATA, videoListJSON);
                        mShortVideoListFragment.setArguments(args);
                        mLoadingView.post(() -> mLoadingView.setVisibility(GONE));
                        addFragmentView();
                    }
                });
            }
        }
    }

    private void addFragmentView() {
        getSupportFragmentManager().beginTransaction().replace(R.id.home_entrance_fragment, mShortVideoListFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShortVideoListFragment = null;
        mLoadingView = null;
    }
}