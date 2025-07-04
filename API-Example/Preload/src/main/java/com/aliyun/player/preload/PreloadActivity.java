package com.aliyun.player.preload;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.loader.MediaLoader;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;

/**
 * @author junhuiYe
 * @date 2025/6/19
 * @brief 播放器预加载（URL）功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现预加载功能演示
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1: 设置本地缓存
 * - 使用 AliPlayerGlobalSettings.enableLocalCache() 开启本地缓存
 * - 创建 MediaLoader 实例进行预加载
 * - 设置预加载状态监听
 * <p>
 * Step 2: 设置预加载器
 * - 创建单例 MediaLoader 实例
 * - 设置加载状态回调
 * - 开始预加载文件（异步加载，可同时加载多个视频文件）
 * <p>
 * Step 3: 开始播放
 * - 设置预加载可提升视频起播速度
 * <p>
 * Step 4: 取消预加载
 */
public class PreloadActivity extends AppCompatActivity {
    private static final String TAG = "PreloadActivity";

    // Load duration for media loader. Unit: ms
    private static final int PRELOAD_BUFFER_DURATION = 3 * 1000;

    // 预加载实例
    private MediaLoader mMediaLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_preload_url_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 1: 设置本地缓存
        setupLocalCache();

        // Step 2: 设置预加载器
        setupMediaLoader();
    }

    /**
     * Step 1: 设置本地缓存
     */
    private void setupLocalCache() {
        // 1.1 开启本地缓存
        AliPlayerGlobalSettings.enableLocalCache(true, this);

        /**
         *  也可以使用下方代码进行缓存设置
         *  开启本地缓存，开启之后，就会缓存到本地文件中。
         *  @param enable：本地缓存功能开关。true：开启，false：关闭，默认关闭。
         *  @param maxBufferMemoryKB：5.4.7.1及以后版本已废弃，暂无作用。
         *  @param localCacheDir：必须设置，本地缓存的文件目录，为绝对路径。
         *  AliPlayerGlobalSettings.enableLocalCache(enable, maxBufferMemoryKB, localCacheDir);
         */

        /**
         * 本地缓存文件清理相关配置。
         * @param expireMin - 5.4.7.1及以后版本已废弃，暂无作用。
         * @param maxCapacityMB - 最大缓存容量。单位：兆，默认值20GB，在清理时，如果缓存总容量超过此大小，则会以cacheItem为粒度，按缓存的最后时间排序，一个一个的删除最旧的缓存文件，直到小于等于最大缓存容量。
         * @param freeStorageMB - 磁盘最小空余容量。单位：兆，默认值0，在清理时，同最大缓存容量，如果当前磁盘容量小于该值，也会按规则一个一个的删除缓存文件，直到freeStorage大于等于该值或者所有缓存都被清理掉。
         * public static void setCacheFileClearConfig(long expireMin,
         *         long maxCapacityMB,
         *         long freeStorageMB)
         */

        // 参考文档:
        // https://help.aliyun.com/zh/vod/developer-reference/advanced-features

        Log.d(TAG, "[Step 1.1] 本地缓存已开启");
    }

    /**
     * Step 2: 设置预加载器
     */
    private void setupMediaLoader() {
        // 2.1 创建单例 MediaLoader 实例
        mMediaLoader = MediaLoader.getInstance();
        Log.d(TAG, "[Step 2.1] MediaLoader 实例创建完成");

        // 2.2 设置加载状态回调
        mMediaLoader.setOnLoadStatusListener(new MediaLoader.OnLoadStatusListener() {
            @Override
            public void onErrorV2(String s, ErrorInfo errorInfo) {
                // 加载出错
                Log.d(TAG, "[Step 2.2] 预加载出错: " + s + " errorInfo: " + errorInfo.getMsg());
                Toast.makeText(getApplicationContext(), getString(R.string.preload_error) + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleted(String s) {
                // 加载完成
                Log.d(TAG, "[Step 2.2] 预加载完成: " + s);
                Toast.makeText(getApplicationContext(), getString(R.string.preload_complete), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), getString(R.string.playback_not_implemented), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(String s) {
                // 加载取消
                Log.d(TAG, "[Step 2.2] 预加载取消: " + s);
                Toast.makeText(getApplicationContext(), getString(R.string.preload_canceled) + s, Toast.LENGTH_SHORT).show();
            }
        });

        // 2.3 开始预加载文件（异步加载，可同时加载多个视频文件）
        mMediaLoader.load(Constants.DataSource.THUMBNAIL_VIDEO_URL, PRELOAD_BUFFER_DURATION);
        Log.d(TAG, "[Step 2.3] 开始预加载视频文件，时长: " + PRELOAD_BUFFER_DURATION + "ms");
    }

    /**
     * Step 3: 开始播放
     * 本模块未实现播放功能，只展示预加载能力，您可在此位置自行实现
     * 设置预加载可提升视频起播速度
     */

    /**
     * Step 4: 取消预加载
     */
    private void cancelMediaLoader() {
        // 取消预加载
        mMediaLoader.cancel(Constants.DataSource.THUMBNAIL_VIDEO_URL);
        Log.d(TAG, "[Step 2.4] 取消预加载视频文件");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        cancelMediaLoader();
        super.onDestroy();
    }
}
