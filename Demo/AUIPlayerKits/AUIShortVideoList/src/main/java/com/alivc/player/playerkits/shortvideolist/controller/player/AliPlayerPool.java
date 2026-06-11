package com.alivc.player.playerkits.shortvideolist.controller.player;

import android.content.Context;

import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;
import com.alivc.player.playerkits.shortvideolist.utils.SLog;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

/**
 * @author baorunchen
 * @date 2024/4/10
 * @brief Manages a pool of AliPlayer instances to be reused across the application.
 * @note Call logic and timing:
 * @note {@link AliPlayerPool#init(Context)} -> {@link AliPlayerPool#acquire(T)} -> ... -> {@link AliPlayerPool#release()}
 */
public class AliPlayerPool<T> {
    // The number of AliPlayer instances to be initialized
    public static final int INITIAL_CAPACITY = AUIShortVideoListConstants.PLAYER_POOL_CAPACITY;
    private final LinkedHashMap<T, AliPlayer> mPlayerPool = new LinkedHashMap<>(INITIAL_CAPACITY, 0.75f, true);

    // Through interface settings, a full screen effect can be achieved, with the default being IPlayer ScaleMode SCALE_ASPECT_FIT
    // The current SDK defaults to IPlayer ScaleMode SCALE-ASPECT_FIT, which means that the image is filled based on its own aspect ratio, and the shorter side is not fully covered on the entire screen, but it will cause the remaining space to be transparent, similar to the top and bottom black edges
    // If you want a full screen effect, please set it to IPlayer ScaleMode SCALE_ASPECT-FILL, which means that the image is filled based on its own aspect ratio, and the excess is cropped, but it can cause the image to be displayed incompletely, similar to zooming in and displaying a portion
    private static final IPlayer.ScaleMode DEFAULT_VIDEO_SCALE_MODE = IPlayer.ScaleMode.SCALE_ASPECT_FILL;

    // Counter and lock for thread-safe initialization and release
    private int refCounter = 0;
    private final Object refLock = new Object();

    private WeakReference<Context> mContext;

    private AliPlayerPool() {
    }

    private static class Inner {
        private static final AliPlayerPool instance = new AliPlayerPool();
    }

    public static AliPlayerPool getInstance() {
        return Inner.instance;
    }

    /**
     * Initializes the player pool with predetermined number of AliPlayer instances.
     *
     * @param context The application context.
     * @note Considering the instant performance, avoid using lazy loading to initialize three player instances,
     * @note please call the init method at a more appropriate location to initialize the player.
     * @attention Corresponding interface: {@link AliPlayerPool#release()}
     */
    public void init(Context context) {
        synchronized (refLock) {
            mContext = new WeakReference<>(context.getApplicationContext());
            refCounter++;
        }
    }

    /**
     * Releases all AliPlayer instances and clears the pool.
     *
     * @note Considering memory issues, please destroy 3 player instances in a more suitable location to avoid memory leakage
     */
    public void release() {
        synchronized (refLock) {
            if (refCounter > 0) {
                refCounter--;
            }
            if (refCounter == 0) {
                // Last release
                releasePlayers();
                mContext = null;
            }
        }
    }

    /**
     * Releases all AliPlayer instances and clears the pool.
     */
    private void releasePlayers() {
        synchronized (mPlayerPool) {
            for (AliPlayer aliPlayer : mPlayerPool.values()) {
                destroyAliPlayerInstance(aliPlayer, "RELEASE");
            }
            mPlayerPool.clear();
        }
        checkPlayerPoolSizeAssert();
    }

    /**
     * Retrieves an AliPlayer instance from the pool based on a key. If the key is
     * not associated with an instance, the least recently used player is recycled.
     *
     * @param key The key to identify the needed AliPlayer instance.
     * @return The AliPlayer instance associated with the given key.
     * @attention If there is a crash caused by a failure to retrieve the player instance after calling this interface,
     * @attention please ensure that you have completed the call to the interface {@link AliPlayerPool#init(Context)}.
     */
    public AliPlayer acquire(T key) {
        SLog.i(this, "ACQUIRE", key);
        synchronized (mPlayerPool) {
            AliPlayer aliPlayer = mPlayerPool.get(key);
            if (aliPlayer != null) {
                SLog.w(this, "REUSE", key, aliPlayer);
                mPlayerPool.remove(key); // Remove it first
                mPlayerPool.put(key, aliPlayer); // Then put it back
                checkPlayerPoolSizeAssert();
                return aliPlayer;
            }

            // Originally implemented using linked lists, the purpose was to achieve reusability of player instances and reduce performance overhead.
            // However, due to various invocation issues in the application layer, exceptions are caused.
            // Therefore, we adopt a method where one view corresponds to one unique player to avoid the reuse of player instances.
            if (mPlayerPool.size() == INITIAL_CAPACITY) {
                T oldestKey = mPlayerPool.keySet().iterator().next();
                AliPlayer oldAliPlayer = mPlayerPool.remove(oldestKey);
                checkPlayerPoolSizeAssert();
                destroyAliPlayerInstance(oldAliPlayer, "GC");
            }

            aliPlayer = initNewAliPlayerInstance(mContext.get());
            // Insert to end of LinkedHashMap, marking it as most recently used.
            mPlayerPool.put(key, aliPlayer);
            checkPlayerPoolSizeAssert();
            return aliPlayer;
        }
    }

    /**
     * recycle AliPlayer instance
     *
     * @param key The key to identify the needed AliPlayer instance.
     */
    public void recycle(T key) {
        SLog.i(this, "RECYCLE", key);
        synchronized (mPlayerPool) {
            AliPlayer oldAliPlayer = mPlayerPool.remove(key);
            checkPlayerPoolSizeAssert();
            destroyAliPlayerInstance(oldAliPlayer, "RECYCLE");
        }
    }

    /// If further design is needed, it can be considered to expose the init/destroy method in the form of a callback,
    /// with the internal logic only used for the creation, destruction, retrieval, reuse, and recycling of player instances

    /**
     * Creates and initializes a new AliPlayer instance.
     *
     * @param context The application context.
     * @return A new instance of AliPlayer.
     */
    private static AliPlayer initNewAliPlayerInstance(Context context) {
        AliPlayer aliPlayer = AliPlayerFactory.createAliPlayer(context);
        PlayerConfig config = aliPlayer.getConfig();
        config.mClearFrameWhenStop = true;
        aliPlayer.setConfig(config);
        aliPlayer.setScaleMode(DEFAULT_VIDEO_SCALE_MODE);
        // Allow pre-render to avoid black screen when sliding to the next item
        aliPlayer.setOption(IPlayer.ALLOW_PRE_RENDER, 1);
        SLog.w("AliPlayerPool", "PLAYER-API-INIT", aliPlayer);
        return aliPlayer;
    }

    /**
     * Destroys the AliPlayer instance and releases its resources.
     *
     * @param aliPlayer The AliPlayer instance to destroy.
     * @note do destroy on sub-thread to avoid high-cost
     */
    private static void destroyAliPlayerInstance(AliPlayer aliPlayer, String tag) {
        if (aliPlayer != null) {
            SLog.w("AliPlayerPool", "PLAYER-API-DESTROY_" + tag, aliPlayer);
            // * 方案1：stop + release，适用于通用场景；释放操作有耗时，会阻塞当前线程，直到资源完全释放。
            // * 方案2：releaseAsync，无需手动 stop，适用于短剧等场景；异步释放资源，不阻塞线程，内部已自动调用 stop。
            // * 注意：执行 release 或 releaseAsync 后，请不要再对播放器实例进行任何操作。
            aliPlayer.releaseAsync();
            aliPlayer = null;
        }
    }

    // The player pool can only have a maximum of two instances at the same time.
    // If it exceeds this limit, it may cause memory leaks, and the problem needs to be investigated.
    private void checkPlayerPoolSizeAssert() {
        assert mPlayerPool.size() <= INITIAL_CAPACITY;
    }
}
