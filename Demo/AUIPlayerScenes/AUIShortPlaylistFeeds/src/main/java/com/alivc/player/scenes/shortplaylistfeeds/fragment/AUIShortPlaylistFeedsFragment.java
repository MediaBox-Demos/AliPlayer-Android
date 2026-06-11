package com.alivc.player.scenes.shortplaylistfeeds.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListFragment;

/**
 * @author junhuiYe
 * @date 2024/9/14
 * @brief
 */
public class AUIShortPlaylistFeedsFragment extends Fragment {
    private AUIShortVideoListFragment fragment;

    private boolean mFirstEnterPage = true;
    private final int[] screenIndex = new int[2];
    private boolean slidingDown = true;
    private int viewId;
    private int currentContainer;

    public AUIShortPlaylistFeedsFragment(int viewId, int currentContainer) {
        this.viewId = viewId;
        this.currentContainer = currentContainer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragment = new AUIShortVideoListFragment();
            fragment.setArguments(getArguments());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(viewId, fragment)
                    .commit();
        }
        return inflater.inflate(currentContainer, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public int getVideoPosition() {
        return fragment.getVideoPosition();
    }

    // TODO problem 1. 不使用下方逻辑
    @Override
    public void onResume() {
        super.onResume();
        screenIndex[1] = screenIndex[0];
        screenIndex[0] = getVideoPosition();
        if (!mFirstEnterPage) {
            if (screenIndex[0] - screenIndex[1] < 0) {
                slidingDown = false;
            } else if (screenIndex[0] - screenIndex[1] > 0) {
                slidingDown = true;
            }
            int nextVideoPlayerIndex = slidingDown ? fragment.getVideoPosition() + 1 : fragment.getVideoPosition() - 1;
            fragment.rebindVideoPlayer(getVideoPosition());
            fragment.rebindVideoPlayer(nextVideoPlayerIndex);
        }

        mFirstEnterPage = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        fragment.unbindVideoPlayer(getVideoPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragment = null;
    }
}
