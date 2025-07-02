package com.alivc.auiplayer.videoepisode;


import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.IListPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.PlayerConfig;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class AUIVideoEpisodeController {

    private final AliListPlayer aliListPlayer;
    private IPlayer preRenderPlayer;

    private int mOldPosition = 0;

    private int mCurrentPlayerState;
    private int mCurrentPlayerStateCallBack;
    private final SparseArray<String> mIndexWithUUID = new SparseArray<>();
    private PlayerListener mPlayerListener;

    // 通过接口设置，可以达到全屏效果，默认是 IPlayer.ScaleMode.SCALE_ASPECT_FIT
    // Full screen effect can be achieved through interface settings, default is IPlayer.ScaleMode.SCALE_ASPECT_FIT
    // 当前SDK默认是 IPlayer.ScaleMode.SCALE_ASPECT_FIT ，即：图片以自身宽高比为准填充，较短一边未铺满全屏幕，但是会导致剩余空间透明，类似于上下黑边
    // The current SDK default is IPlayer.ScaleMode.SCALE_ASPECT_FIT, i.e.: the image is filled with its own aspect ratio, the shorter side is not spread over the whole screen, but it will lead to the transparency of the remaining space, similar to the upper and lower black edges.
    // 如果想要全屏效果，请设置为 IPlayer.ScaleMode.SCALE_ASPECT_FILL ，即：图片以自身宽高比为准填充 ，超出部分裁剪，但是会导致图片显示不全，类似于放大并显示一部分
    // If you want full-screen effect, please set it to IPlayer.ScaleMode.SCALE_ASPECT_FILL, i.e. the image will be filled with its own aspect ratio, and the exceeding part will be cropped, but it will lead to incomplete display of the image, similar to zooming in and displaying a part of it.
    private static final IPlayer.ScaleMode DEFAULT_VIDEO_SCALE_MODE = IPlayer.ScaleMode.SCALE_ASPECT_FIT;

    // 通过接口设置，可以达到精准seek效果，默认是 IPlayer.SeekMode.Inaccurate
    // The accurate seek effect can be achieved through the interface settings, the default is IPlayer.SeekMode.Inaccurate.
    // 当前SDK默认是 IPlayer.SeekMode.Inaccurate ，即：非精准seek
    // The current SDK default is IPlayer.SeekMode.Inaccurate, i.e.: non-accurate seek
    // 如果想要精准seek，请设置为 IPlayer.SeekMode.Accurate
    // If you want accurate seek, please set it to IPlayer.SeekMode.Accurate
    private static final IPlayer.SeekMode DEFAULT_SEEK_MODE = IPlayer.SeekMode.Accurate;

    public AUIVideoEpisodeController(Context context) {
        // AF_LOG_LEVEL_INFO，默认info级别
        // AF_LOG_LEVEL_TRACE, default trace level
//        Logger.getInstance(context).enableConsoleLog(true);
//        Logger.getInstance(context).setLogLevel(Logger.LogLevel.AF_LOG_LEVEL_TRACE);

        aliListPlayer = AliPlayerFactory.createAliListPlayer(context);
        aliListPlayer.setScaleMode(DEFAULT_VIDEO_SCALE_MODE);

        preRenderPlayer = aliListPlayer.getPreRenderPlayer();

        initPlayerConfigs(context);
        initPlayerListeners();
    }

    // 播放器相关配置
    // Player-related configuration
    private void initPlayerConfigs(Context context) {
        //若采取智能预加载策略，则无需设置preloadCount
        //If you adopt the smart preload strategy, you do not need to set preloadCount
        //setPreloadCount(2);

        //设置预加载最大缓存内存大小，这里为500
        //Set the maximum cache memory size of preloading, here is 500
        String preloadStrategyParam = "{\"algorithm\":\"sub\",\"offset\":\"500\"}";
        setPreloadStrategy(true, preloadStrategyParam);

        //开启本地缓存，统一约定在cache路径下的Preload目录
        //Enable local caching, and the unified convention is in the Preload directory under the cache path
        String cacheDir = context.getExternalCacheDir() + File.separator + "Preload";
        enableLocalCache(true, cacheDir);
        setCacheFileClearConfig(30 * 24 * 60, 20 * 1024, 0);

        // 音视频终端SDK和播放器SDK从6.12.0版本开始无需手动开启HTTPDNS。
        // The Terminal SDK and Player SDK do not need to manually enable HTTPDNS since version 6.12.0.

        //配置网络超时重试时间与次数
        //Configure network timeout retry time and number of times
        setNetworkRetryTimes(5000, 2);
    }

    // 播放器相关回调
    // Player-related callback
    private void initPlayerListeners() {
        aliListPlayer.setOnPreparedListener(() -> {
            mPlayerListener.onPrepared(-1);
        });

        aliListPlayer.setOnRenderingStartListener(() -> {
            mPlayerListener.onRenderingStart(-1, aliListPlayer.getDuration());
        });

        aliListPlayer.setOnInfoListener(infoBean -> {
            long duration = aliListPlayer.getDuration();
            mPlayerListener.onInfo((int) duration, infoBean);
            toRenderingStartOnInfo();
        });

        aliListPlayer.setOnStateChangedListener(i -> {
            mCurrentPlayerStateCallBack = i;
            mPlayerListener.onPlayStateChanged(-1, mCurrentPlayerStateCallBack == IPlayer.paused);
        });

        aliListPlayer.setOnErrorListener((ErrorInfo errorInfo) -> {
            mPlayerListener.onError(errorInfo);
            aliListPlayer.stop();
        });
    }

    public void loadSource(List<AUIEpisodeVideoInfo> listVideo) {
        aliListPlayer.clear();
        mIndexWithUUID.clear();
        for (int i = 0; i < listVideo.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i, randomUUID);
            aliListPlayer.addUrl(listVideo.get(i).getUrl(), randomUUID);
        }
    }

    public void addSource(List<AUIEpisodeVideoInfo> videoBeanList) {
        for (int i = 0; i < videoBeanList.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i, randomUUID);
            aliListPlayer.addUrl(videoBeanList.get(i).getUrl(), randomUUID);
        }
    }

    public void openLoopPlay(boolean openLoopPlay) {
        if (aliListPlayer != null) {
            aliListPlayer.setLoop(openLoopPlay);
        }
        if (preRenderPlayer != null) {
            preRenderPlayer.setLoop(openLoopPlay);
        }
    }

    public void setPlayerListener(PlayerListener listener) {
        this.mPlayerListener = listener;
    }

    // 用于剧集的跳转
    // For the episode jump
    public void onPageSelected(int position) {
        Log.i("CheckFunc", "onPageSelected (int) position " + position);
        moveToPosition(position, null);
        this.mOldPosition = position;
    }

    // 用于短视频的上下滑动
    // Swipe up and down for short videos
    public void onPageSelected(int position, Surface surface) {
        Log.i("CheckFunc", "onPageSelected (int, surface)" + " position " + position);
        moveToPosition(position, surface);
        this.mOldPosition = position;
    }

    // 移动到指定位置的共用方法
    // Shared method for moving to a specific location
    private void moveToPosition(int position, Surface surface) {
        setSurface(surface);

        // 如果是第一个位置或者跳跃式改变位置
        // If it's the first position or a jump to change positions
        if (position == 0 || Math.abs(position - mOldPosition) != 1) {
            aliListPlayer.moveTo(mIndexWithUUID.get(position));
            return;
        }

        // 如果是相邻位置的平滑过渡
        // If it's a smooth transition between neighboring positions
        if (mOldPosition < position) {
            handleNextWithPreRender(surface);
        } else { // 向前滑动 slide forward
            aliListPlayer.moveToPrev();
        }
    }

    // 处理预渲染的播放器逻辑
    // Handle pre-rendered player logic
    private void handleNextWithPreRender(Surface surface) {
        if (preRenderPlayer == null) {
            aliListPlayer.clearScreen();
            return;
        }

        // 使用预渲染的播放器播放下一个视频
        // Play the next video with the pre-rendered player
        preRenderPlayer.setSurface(surface);
        preRenderPlayer.start();
        aliListPlayer.setSurface(null);
        aliListPlayer.moveToNextWithPrerendered();
        setupPreRenderedPlayerListeners(preRenderPlayer);
    }

    // 设置预渲染播放器的监听器
    // Set the listener of the pre-rendered player
    private void setupPreRenderedPlayerListeners(IPlayer preRenderPlayer) {
        preRenderPlayer.setOnInfoListener(infoBean -> {
            long duration = preRenderPlayer.getDuration();
            mPlayerListener.onInfo((int) duration, infoBean);
            toRenderingStartOnInfo();
        });
        preRenderPlayer.setOnStateChangedListener(i -> {
            mCurrentPlayerStateCallBack = i;
            mPlayerListener.onPlayStateChanged(-1, mCurrentPlayerStateCallBack == IPlayer.paused);
        });
        preRenderPlayer.setOnPreparedListener(() -> mPlayerListener.onPrepared(-1));
        preRenderPlayer.setOnErrorListener((ErrorInfo errorInfo) -> {
            mPlayerListener.onError(errorInfo);
            preRenderPlayer.stop();
        });
    }

    /**
     * 设置智能预渲染
     *
     * @note 当前版本，PreRender Player仅支持预渲染列表下一个视频的画面；指定预渲染上一个视频的画面，有待后续版本支持。
     */
    /****
     * Setting up smart pre-rendering
     *
     * @note In the current version, PreRender Player only supports pre-rendering the screen of the next video in the list; specifying pre-rendering the screen of the previous video is to be supported in subsequent versions.
     */
    public void setSurfaceToPreRenderPlayer(Surface surface) {
        Log.i("CheckFunc", "setSurfaceToPreRenderPlayer " + "mOldPosition " + mOldPosition);
        preRenderPlayer = aliListPlayer.getPreRenderPlayer();
        if (preRenderPlayer != null && surface != null) {
            preRenderPlayer.setScaleMode(DEFAULT_VIDEO_SCALE_MODE);
            preRenderPlayer.setSurface(surface);
            preRenderPlayer.seekTo(0, DEFAULT_SEEK_MODE);
        }
    }

    public void setSurface(Surface surface) {
        Log.w("CheckFunc", "setSurface: [" + surface + "]");

        aliListPlayer.setSurface(surface);
    }

    public void destroy() {
        aliListPlayer.clear();
        aliListPlayer.stop();
        aliListPlayer.release();
    }

    public void onPlayStateChange() {
        Log.i("CheckFunc", "onPlayStateChange" + " mCurrentPlayerState " + mCurrentPlayerState + " moldPosition " + mOldPosition + " mCurrentPlayerStateCallBack " + mCurrentPlayerStateCallBack);
        if (mCurrentPlayerStateCallBack < IPlayer.prepared) {
            return;
        }
        if (mCurrentPlayerState == IPlayer.paused) {
            aliListPlayer.start();
            mCurrentPlayerState = IPlayer.started;
        } else {
            aliListPlayer.pause();
            mCurrentPlayerState = IPlayer.paused;
        }
    }

    public void seek(long seekPosition) {
        if (seekPosition >= aliListPlayer.getDuration()) {
            // 避免直接跳转到视频尾部，与自动跳转到下一集出现逻辑上的冲突，出现黑屏的的情况。
            // Avoid jumping directly to the end of the video, which would logically conflict with automatically jumping to the next episode, resulting in a black screen.
            seekPosition -= 10;
        }
        if (seekPosition < 0 || seekPosition > aliListPlayer.getDuration()) {
            Log.w("CheckFunc", "seek, seekTo not valid: " + seekPosition);
            return;
        }
        aliListPlayer.seekTo(seekPosition, DEFAULT_SEEK_MODE);
        Log.i("CheckFunc", "seek, seekTo " + seekPosition);

    }

    public void pausePlay() {
        aliListPlayer.pause();
    }

    public void resumePlay() {
        aliListPlayer.start();
    }

    public boolean isCurrentPlayerStateCallBackPaused() {
        return mCurrentPlayerStateCallBack == IPlayer.paused;
    }

    //backUp: in case of preRenderPlayer onRenderingStart being called back ，aliListPlayer cannot get onRenderingStart called back.
    private void toRenderingStartOnInfo() {
        if (aliListPlayer.getDuration() > 0) {
            mPlayerListener.onRenderingStart(-1, aliListPlayer.getDuration());
        }
    }

    /**
     * 设置预加载数量
     */
    /****
     * Setting up the number of preloads
     */
    public void setPreloadCount(int preloadCount) {
        aliListPlayer.setPreloadCount(preloadCount);
    }

    /**
     * 设置智能预加载策略
     */
    /****
     * Setting up the smart preload strategy
     */
    public void setPreloadStrategy(boolean enable, String params) {
        aliListPlayer.setPreloadScene(IListPlayer.SceneType.SCENE_SHORT);
        aliListPlayer.enablePreloadStrategy(IListPlayer.StrategyType.STRATEGY_DYNAMIC_PRELOAD_DURATION, enable);
        if (enable) {
            aliListPlayer.setPreloadStrategy(IListPlayer.StrategyType.STRATEGY_DYNAMIC_PRELOAD_DURATION, params);
        }
    }

    /**
     * 开启本地缓存
     */
    /****
     * Enable local cache
     */
    public void enableLocalCache(boolean enable, String path) {
        AliPlayerGlobalSettings.enableLocalCache(enable, 10 * 1024, path);
        PlayerConfig config = aliListPlayer.getConfig();
        config.mEnableLocalCache = enable;
        aliListPlayer.setConfig(config);
    }

    /**
     * 设置缓存清除策略
     */
    /****
     * Set the cache clear strategy
     */
    public void setCacheFileClearConfig(long expireMin, long maxCapacityMB, long freeStorageMB) {
        AliPlayerGlobalSettings.setCacheFileClearConfig(expireMin, maxCapacityMB, freeStorageMB);
    }

    /**
     * 清除缓存
     */
    /****
     * Clear cache
     */
    public void clearCache() {
        AliPlayerGlobalSettings.clearCaches();
    }

    /**
     * 配置网络超时重试时间与次数
     */
    /****
     * Configure network timeout retry time and number of times
     */
    public void setNetworkRetryTimes(int timeoutMs, int retryCount) {
        PlayerConfig config = aliListPlayer.getConfig();
        config.mNetworkRetryCount = retryCount;
        config.mNetworkTimeout = timeoutMs;
        aliListPlayer.setConfig(config);
    }
}
