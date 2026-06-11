package com.alivc.player.playerkits.shortvideolist.controller;

import android.content.Context;

import com.alivc.player.playerkits.shortvideolist.controller.player.AliPlayerPool;
import com.alivc.player.playerkits.shortvideolist.controller.preload.AliPlayerPreload;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;
import com.aliyun.player.AliPlayerGlobalSettings;

import java.io.File;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2023/8/29
 * @brief 短视频列表播放-页面控制器
 */
public class AUIShortVideoListController {

    // source information for biz
    private static final String EXTRA_DATA_SHORT_VIDEO_LIST = "{\"scene\":\"aui-episode\",\"platform\":\"android\",\"style\":\"function-list\"}";

    // 开启本地缓存
    private static final boolean ENABLE_LOCAL_CACHE_FLAG = true;
    // 5.4.7.1及以后版本已废弃，暂无作用
    private static final int LOCAL_CACHE_MAX_BUFFER_MEMORY_KB = 10 * 1024;
    // 5.4.7.1及以后版本已废弃，暂无作用。
    private static final int LOCAL_CACHE_EXPIRE_MIN = 30 * 24 * 60;
    // 最大缓存容量。单位：兆，默认值20GB
    // 在清理时，如果缓存总容量超过此大小，则会以cacheItem为粒度，按缓存的最后时间排序，一个一个的删除最旧的缓存文件，直到小于等于最大缓存容量。
    private static final int LOCAL_CACHE_MAX_CAPACITY_MB = 20 * 1024;
    // 磁盘最小空余容量。单位：兆，默认值0
    // 在清理时，同最大缓存容量，如果当前磁盘容量小于该值，也会按规则一个一个的删除缓存文件，直到freeStorage大于等于该值或者所有缓存都被清理掉。
    private static final int LOCAL_CACHE_FREE_STORAGE_MB = 0;

    // player pool
    private AliPlayerPool mAliPlayerPool;
    // player preload
    private AliPlayerPreload mAliPlayerPreload;

    //Play the 0 th video for the first time
    private int mCurrentPosition;

    public AUIShortVideoListController(Context context) {
        // setup player configs, such as local cache (for preload)
        initPlayerConfigs(context);

        // init player pool
        mAliPlayerPool = AliPlayerPool.getInstance();
        mAliPlayerPool.init(context);

        // init player preload
        mAliPlayerPreload = new AliPlayerPreload();
        mAliPlayerPreload.init(context);
    }

    // set the bandwidth size
    public void setBandWidth(int bandWidth) {
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.setBandWidth(bandWidth);
        }
    }

    /**
     * load Video List
     * 1. set Video List to AliPlayerPreload
     */
    public void loadSources(List<VideoInfo> videoList) {
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.setItems(videoList);
        }
    }

    public void addSource(List<VideoInfo> videoBeanList) {
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.addItems(videoBeanList);
        }
    }

    // 播放器相关配置
    // Player-related configuration
    private void initPlayerConfigs(Context context) {
        // 设置业务来源信息
        // Set business source information
        AliPlayerGlobalSettings.setOption(AliPlayerGlobalSettings.SET_EXTRA_DATA, EXTRA_DATA_SHORT_VIDEO_LIST);

        //开启本地缓存，统一约定在cache路径下的Preload目录
        //Turn on local cache and standardize on the Preload directory under the cache path
        String cacheDir = context.getExternalCacheDir() + File.separator + "Preload";
        AliPlayerGlobalSettings.enableLocalCache(ENABLE_LOCAL_CACHE_FLAG, LOCAL_CACHE_MAX_BUFFER_MEMORY_KB, cacheDir);

        // 设置缓存清除策略
        // Set cache clear strategy
        AliPlayerGlobalSettings.setCacheFileClearConfig(LOCAL_CACHE_EXPIRE_MIN, LOCAL_CACHE_MAX_CAPACITY_MB, LOCAL_CACHE_FREE_STORAGE_MB);

        // 清除缓存
        // Clear cache
        //AliPlayerGlobalSettings.clearCaches();
    }

    public void onInitComplete() {
        SLog.w(this, "INIT_COMPLETE", mCurrentPosition + "->" + 0);
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.moveTo(0);
        }
    }

    public void updatePositionWhenFastSlide(int position) {
        if (Math.abs(position - mCurrentPosition) < 3) {
            return;
        }
        int targetPosition = position - 1;
        SLog.w(this, "PEEK_START", mCurrentPosition + "->" + targetPosition);
        // If you drag the page without letting go, the onPageSelected method of the item will not execute.
        // Therefore, you can use the attach method of the item to move the pointer to ensure that the preload works properly
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.moveTo(targetPosition);
        }
    }

    public void updatePositionByPageSelect(int position) {
        if (mCurrentPosition == position) {
            return;
        }
        SLog.w(this, "UPDATE_POSITION", mCurrentPosition + "->" + position);
        this.mCurrentPosition = position;
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.moveTo(position);
        }
    }

    public void destroy() {
        if (mAliPlayerPool != null) {
            mAliPlayerPool.release();
            mAliPlayerPool = null;
        }
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.release();
            mAliPlayerPreload = null;
        }
    }
}
