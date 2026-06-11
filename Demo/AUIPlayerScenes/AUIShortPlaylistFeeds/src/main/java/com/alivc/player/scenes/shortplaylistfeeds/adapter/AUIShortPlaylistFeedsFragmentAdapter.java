package com.alivc.player.scenes.shortplaylistfeeds.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class AUIShortPlaylistFeedsFragmentAdapter extends FragmentStateAdapter {
    private List<Fragment> fragments;

    public AUIShortPlaylistFeedsFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Fragment> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        return fragments.get(position).hashCode();
    }

    // 获取当前Fragment的位置
    public int getCurrentFragmentPosition(ViewPager2 viewPager) {
        return viewPager.getCurrentItem();
    }

}
