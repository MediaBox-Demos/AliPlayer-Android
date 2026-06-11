package com.alivc.player.playerkits.shortvideolist.controller.preload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.alivc.player.playerkits.shortvideolist.data.VideoInfo;
import com.alivc.player.playerkits.shortvideolist.utils.AUIShortVideoListUtil;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;
import com.aliyun.loader.MediaLoaderV2;
import com.aliyun.loader.OnPreloadListener;
import com.aliyun.loader.PreloadTask;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.PreloadConfig;
import com.aliyun.player.source.VidAuth;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author baorunchen
 * @date 2024/4/19
 * @brief By using the idea of sliding windows, handle the logic of MediaLoader preloading to ensure the effect of full screen second opening
 * @note Need to be used in conjunction with local caching functionality: {@link com.aliyun.player.AliPlayerGlobalSettings#enableLocalCache(boolean, int, String)}  }
 */
public class AliPlayerPreload {
    private static final boolean ENABLE_PRELOAD_LOG_FLAG = true;

    // Load duration for media loader. Unit: ms
    private static final int PRELOAD_BUFFER_DURATION = 3 * 1000;

    // if current item index is n, preload n-1 & n+2 video; n & n+1 use prepare instead of media loader
    private static final int[] MEDIA_PRELOAD_WINDOWS = {-1, 2};

    // if current item index is n, preload n-3 to n+10 cover
    private static final int COVER_PRELOAD_LEFT_WINDOW_SIZE = 3;
    private static final int COVER_PRELOAD_RIGHT_WINDOW_SIZE = 10;

    private final ReentrantLock mCallMethodLock = new ReentrantLock(true);

    private final AliSlidingWindow<VideoInfo> videoPreloader;
    private final AliSlidingWindow<String> coverPreloader;

    private ExecutorService mExecutorService = null;

    private Context mContext = null;

    private int mCurrentBandWidth;

    // 预加载任务
    private PreloadTask mPreloadTask;

    private MediaLoaderV2 mMediaLoaderV2;

    private PreloadConfig mPreloadConfig;

    private String taskId;

    public AliPlayerPreload() {
        videoPreloader = new AliSlidingWindow<>(MEDIA_PRELOAD_WINDOWS, new AliSlidingWindow.Callback<VideoInfo>() {
            @Override
            public void execute(VideoInfo videoInfo) {
                preloadMedia(videoInfo);
            }

            @Override
            public void cancel(VideoInfo videoInfo) {
                cancelPreloadMedia(taskId);
            }

            @Override
            public boolean isValid(VideoInfo videoInfo) {
                return !TextUtils.isEmpty(videoInfo.toString());
            }
        }, "VIDEO");

        coverPreloader = new AliSlidingWindow<>(COVER_PRELOAD_LEFT_WINDOW_SIZE, COVER_PRELOAD_RIGHT_WINDOW_SIZE, new AliSlidingWindow.Callback<String>() {
            @Override
            public void execute(String item) {
                preloadCover(item);
            }

            @Override
            public boolean isValid(String item) {
                return !TextUtils.isEmpty(item);
            }
        }, "COVER");
    }

    /**
     * initialize the media loader
     */
    public void init(Context context) {
        log4Preload(Log.INFO, "API-INIT");
        mContext = context;
        startExecutorService();
        initMediaLoaderV2();
    }

    /**
     * release the media loader
     */
    public void release() {
        log4Preload(Log.INFO, "API-RELEASE");
        videoPreloader.release();
        coverPreloader.release();
        releaseMediaLoader();
        stopExecutorService();

        mContext = null;
    }

    /**
     * force update the video list URLs
     *
     * @param items video list URLs
     */
    public synchronized void setItems(List<VideoInfo> items) {
        updateItems(items, true);
    }

    /**
     * append the video list URLs
     *
     * @param items video list URLs
     */
    public synchronized void addItems(List<VideoInfo> items) {
        updateItems(items, false);
    }

    /**
     * Updates video and cover URLs.
     *
     * @param items     video list URLs
     * @param overwrite true to overwrite existing items, false to append
     */
    private void updateItems(List<VideoInfo> items, boolean overwrite) {
        if (items == null || items.isEmpty()) {
            SLog.w(this, "[UPDATE_ITEMS]: Items list is null or empty");
            return;
        }

        List<VideoInfo> videoInfos = new ArrayList<>();
        List<String> coverUrls = new ArrayList<>();

        for (VideoInfo item : items) {
            if (item != null && !TextUtils.isEmpty(item.videoId)) { // 添加 videoId 检查
                videoInfos.add(item);
                if (!TextUtils.isEmpty(item.coverUrl)) { // 添加 coverUrl 检查
                    coverUrls.add(item.coverUrl);
                }
            }
        }

        if (videoInfos.isEmpty()) {
            SLog.w(this, "[UPDATE_ITEMS]: No valid video items found");
            return;
        }

        try {
            if (overwrite) {
                videoPreloader.setItems(videoInfos);
                if (AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY && !coverUrls.isEmpty()) {
                    coverPreloader.setItems(coverUrls);
                }
            } else {
                videoPreloader.addItems(videoInfos);
                if (AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY && !coverUrls.isEmpty()) {
                    coverPreloader.addItems(coverUrls);
                }
            }
        } catch (Exception e) {
            SLog.e(this, "[UPDATE_ITEMS][ERROR]: " + e.getMessage());
        }
    }

    /**
     * move to specific position
     *
     * @param position the position of video list
     */
    public synchronized void moveTo(int position) {
        if (position < 0) {
            SLog.w(this, "[MOVE_TO]: Invalid position: " + position);
            return;
        }

        try {
            videoPreloader.moveTo(position);
            if (AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY) {
                coverPreloader.moveTo(position);
            }
        } catch (Exception e) {
            SLog.e(this, "[MOVE_TO][ERROR]: " + e.getMessage());
        }
    }

    /**
     * setting bandwidth
     *
     * @param bandWidth the bandWidth of select bandWidth
     */
    public void setBandWidth(int bandWidth) {
        mCallMethodLock.lock();
        if (mCurrentBandWidth == bandWidth) {
            SLog.i(this, "[API][SET_BANDWIDTH][DUPLICATE]: Bandwidth value unchanged, no action needed.");
        }

        if (bandWidth < 0) {
            // 如果带宽为 0 或 负数，重置为默认状态
            mCurrentBandWidth = 0;
            SLog.i(this, "[API][SET_BANDWIDTH][RESET]: Reset to default state");
        } else {
            // 设置新的带宽
            mCurrentBandWidth = bandWidth;
            SLog.i(this, "[API][SET_BANDWIDTH][SET]: " + mCurrentBandWidth);
        }

        // 刷新预加载器逻辑
        refreshPreloader();
        mCallMethodLock.unlock();
    }

    private int getBandWidth() {
        return mCurrentBandWidth;
    }

    private void refreshPreloader() {
        if (videoPreloader != null) {
            videoPreloader.refresh();
        }
    }

    // ---- media preload by media loader ----

    private void initMediaLoaderV2() {
        mMediaLoaderV2 = MediaLoaderV2.getInstance();
        mPreloadConfig = new PreloadConfig();
    }

    private void releaseMediaLoader() {
        mCallMethodLock.lock();
        try {
            mMediaLoaderV2 = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCallMethodLock.unlock();
        }
    }

    private void preloadMedia(final VideoInfo videoInfo) {
        if (videoInfo == null || TextUtils.isEmpty(videoInfo.videoId) || TextUtils.isEmpty(videoInfo.playAuth)) {
            SLog.w(this, "[VIDEO-PRELOAD]: Invalid video info");
            return;
        }

        log4Preload(Log.INFO, "VIDEO-PRELOAD", videoInfo.videoId, mMediaLoaderV2);
        assert mExecutorService != null;
        mExecutorService.execute(() -> {
            mCallMethodLock.lock();
            try {
                VidAuth vidAuth = new VidAuth();
                vidAuth.setVid(videoInfo.videoId);
                vidAuth.setPlayAuth(videoInfo.playAuth);
                if (mCurrentBandWidth > 0) {
                    mPreloadConfig.setDuration(PRELOAD_BUFFER_DURATION);
                    // 如果设置了带宽值，同步传入带宽参数
                    mPreloadConfig.setDefaultBandWidth(mCurrentBandWidth);
                    mPreloadTask = new PreloadTask(vidAuth, mPreloadConfig);
                    taskId = mMediaLoaderV2.addTask(mPreloadTask, new PreloadListenerImpl());
                } else if (mMediaLoaderV2 != null) {
                    // 默认逻辑
                    mPreloadConfig.setDuration(PRELOAD_BUFFER_DURATION);
                    mPreloadTask = new PreloadTask(vidAuth, mPreloadConfig);
                    taskId = mMediaLoaderV2.addTask(mPreloadTask, new PreloadListenerImpl());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mCallMethodLock.unlock();
            }
        });
    }

    private void cancelPreloadMedia(final String taskId) {
        log4Preload(Log.INFO, "VIDEO-CANCEL", taskId, mMediaLoaderV2);
        assert mExecutorService != null;
        mExecutorService.execute(() -> {
            mCallMethodLock.lock();
            try {
                if (mMediaLoaderV2 != null && !TextUtils.isEmpty(taskId)) {
                    mMediaLoaderV2.cancelTask(taskId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mCallMethodLock.unlock();
            }
        });
    }

    // ---- cover preload by glide ----
    private void preloadCover(final String coverUrl) {
        if (!AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY) {
            return;
        }
        assert mExecutorService != null;
        mExecutorService.execute(() -> {
            if (TextUtils.isEmpty(coverUrl)) {
                return;
            }
            log4Preload(Log.INFO, "COVER-PRELOAD", coverUrl);
            String realCoverUrl = AUIShortVideoListUtil.convertURLFromHTTPS2HTTP(coverUrl);
            Glide.with(mContext).load(realCoverUrl).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    log4Preload(Log.ERROR, "COVER-CBK-PRELOAD-FAILED", coverUrl);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    log4Preload(Log.INFO, "COVER-CBK-PRELOAD-READY", coverUrl);
                    return false;
                }
            }).preload();
        });
    }

    /**
     * print logs
     *
     * @param level    log level
     * @param messages log messages
     */
    private void log4Preload(int level, String method, Object... messages) {
        if (ENABLE_PRELOAD_LOG_FLAG) {
            SLog.l(level, this, method, messages);
        }
    }

    /**
     * Start a scheduled task to update the latest method cost information
     */
    private void startExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
                private int threadCount = 0;

                @Override
                public Thread newThread(Runnable r) {
                    threadCount++;
                    return new Thread(r, "PlayerPreload#" + threadCount);
                }
            });
        }
    }

    /**
     * Stop scheduled task, and then not process messages.
     */
    private void stopExecutorService() {
        try {
            if (mExecutorService != null) {
                mExecutorService.shutdown();
                if (!mExecutorService.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                    mExecutorService.shutdownNow();
                }
                mExecutorService = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (mExecutorService != null) {
                mExecutorService = null;
            }
        }
    }

    private class PreloadListenerImpl extends OnPreloadListener {

        @Override
        public void onError(@NonNull String taskId, @NonNull String urlOrVid, @NonNull ErrorInfo errorInfo) {
            log4Preload(Log.ERROR, "VIDEO-CBK-ERROR", taskId);
        }

        @Override
        public void onCompleted(@NonNull String taskId, @NonNull String urlOrVid) {
            log4Preload(Log.INFO, "VIDEO-CBK-COMPLETE", taskId);
        }

        @Override
        public void onCanceled(@NonNull String taskId, @NonNull String urlOrVid) {
            log4Preload(Log.WARN, "VIDEO-CBK-CANCEL", taskId);
        }
    }
}
