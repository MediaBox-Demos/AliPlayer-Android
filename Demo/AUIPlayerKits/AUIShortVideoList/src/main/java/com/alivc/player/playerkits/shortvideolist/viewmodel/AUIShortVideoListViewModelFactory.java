package com.alivc.player.playerkits.shortvideolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author junhuiYe
 * @date 2024/9/12
 * @brief 短视频列表播放ViewModelFactory
 */
public class AUIShortVideoListViewModelFactory<T> implements ViewModelProvider.Factory {

    private final AUIShortVideoListViewModel.DataProvider<T> mDataProvider;

    public AUIShortVideoListViewModelFactory(AUIShortVideoListViewModel.DataProvider<T> dataProvider) {
        this.mDataProvider = dataProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AUIShortVideoListViewModel(mDataProvider);
    }
}
