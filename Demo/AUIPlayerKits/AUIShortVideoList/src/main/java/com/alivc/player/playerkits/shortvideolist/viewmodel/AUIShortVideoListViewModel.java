package com.alivc.player.playerkits.shortvideolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author junhuiYe
 * @date 2024/9/12
 * @brief 短视频列表播放 ViewModel
 * <p>
 * 支持：
 * - 首次加载
 * - 下拉刷新 (isRefresh = true)
 * - 加载更多 (isRefresh = false)
 * - 外部主动刷新全部数据（refreshAllData）
 */
public class AUIShortVideoListViewModel<T> extends ViewModel {

    // 数据提供者，负责实际的数据加载逻辑
    private final DataProvider<T> mDataProvider;

    // 存储“加载更多”操作返回的数据
    private final MutableLiveData<T> _mLoadMoreVideoListLiveData = new MutableLiveData<>();
    public LiveData<T> mLoadMoreVideoListLiveData = _mLoadMoreVideoListLiveData;

    // 存储“刷新”或“强制更新”操作返回的数据
    private final MutableLiveData<T> _mRefreshVideoListLiveData = new MutableLiveData<>();
    public LiveData<T> mRefreshVideoListLiveData = _mRefreshVideoListLiveData;

    /**
     * 构造函数
     *
     * @param dataProvider 数据来源提供者
     */
    public AUIShortVideoListViewModel(DataProvider<T> dataProvider) {
        this.mDataProvider = dataProvider;
        // 初始化时加载初始数据（非刷新）
        loadData(false);
    }

    /**
     * 加载数据（用于分页场景：刷新 or 加载更多）
     *
     * @param isRefresh 是否为下拉刷新操作
     */
    public void loadData(boolean isRefresh) {
        mDataProvider.onLoadData(new DataCallback<T>() {
            @Override
            public void onData(T data) {
                if (isRefresh) {
                    _mRefreshVideoListLiveData.postValue(data);
                } else {
                    _mLoadMoreVideoListLiveData.postValue(data);
                }
            }
        });
    }

    /**
     * 【新增】强制刷新全部数据
     * <p>
     * 使用场景举例：
     * - 从详情页进入，先显示单条数据，再异步获取完整列表后调用此方法刷新整体
     * - 外部事件触发重新设置整个列表内容
     * - 接收到推送的新剧集列表，需要替换当前所有数据
     *
     * @param newData 新的完整数据集合，不可为 null
     */
    public void refreshAllData(@NonNull T newData) {
        if (newData == null) {
            throw new IllegalArgumentException("refreshAllData() 不允许传入 null 数据");
        }
        _mRefreshVideoListLiveData.postValue(newData);
    }

    /**
     * 数据回调接口
     * 由 DataProvider 在完成数据加载后回调
     *
     * @param <T> 数据类型
     */
    public interface DataCallback<T> {
        void onData(T data);
    }

    /**
     * 数据提供者接口
     * 实现类需提供具体的加载逻辑（如网络请求、数据库查询等）
     *
     * @param <T> 数据类型
     */
    public interface DataProvider<T> {
        void onLoadData(DataCallback<T> callback);
    }
}
