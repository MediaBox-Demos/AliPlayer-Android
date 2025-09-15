package com.aliyun.player.preload;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.loader.MediaLoaderV2;
import com.aliyun.loader.OnPreloadListener;
import com.aliyun.loader.PreloadTask;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.common.utils.ToastUtils;
import com.aliyun.player.nativeclass.PreloadConfig;
import com.aliyun.player.source.Definition;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * @author junhuiYe
 * @date 2025/6/19
 * @brief 播放器预加载（URL）功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现预加载功能，提升视频起播速度
 * <p>
 * ==================== 预加载 API 调用步骤 ====================
 * Step 1: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 可选：设置 traceId 开启播放器单点追查
 * <p>
 * Step 2: 设置本地缓存
 * - 使用 AliPlayerGlobalSettings.enableLocalCache() 开启本地缓存
 * - 配置缓存清理策略（可选）
 * <p>
 * Step 3: 初始化视图组件
 * - 初始化预加载控制按钮
 * - 设置按钮点击事件监听
 * <p>
 * Step 4: 配置预加载任务
 * - 创建 PreloadConfig 配置对象
 * - 构建 PreloadTask 预加载任务
 * - 添加任务到 MediaLoaderV2 实例
 * <p>
 * Step 5: 预加载控制操作
 * - 开始预加载：addTask()
 * - 暂停预加载：pauseTask()
 * - 恢复预加载：resumeTask()
 * - 取消预加载：cancelTask()
 * <p>
 * Step 6: 资源清理
 * - 取消所有预加载任务
 * - 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 */
public class PreloadActivity extends AppCompatActivity {
    private static final String TAG = "PreloadActivity";

    // 预加载缓冲时长，单位：毫秒
    private static final int PRELOAD_BUFFER_DURATION = 1000;
    // 默认分辨率
    private static final int DEFAULT_RESOLUTION = 640 * 480;
    // 默认码率
    private static final int DEFAULT_BAND_WIDTH = 400 * 1000;
    // 默认清晰度
    private static final String DEFAULT_QUALITY = "AUTO";
    // 默认清晰度列表
    private static final List<Definition> DEFAULT_DEFINITION_LIST = new ArrayList<>();
    private static final String SAMPLE_VID = "";
    private static final String SAMPLE_PLAY_AUTH = "";

    // 播放器实例
    private AliPlayer mAliPlayer;
    // 预加载任务
    private PreloadTask mPreloadTask;
    // 任务ID
    private String mTaskId;

    // UI 控件
    private Button mStartBtn;
    private Button mPauseBtn;
    private Button mResumeBtn;
    private Button mCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_preload_url_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2: 设置本地缓存
        setupLocalCache();

        // Step 3: 初始化视图组件
        setupViews();

        // Step 4 & Step 5: 设置预加载控制
        setupPreloadControls();
    }

    @Override
    protected void onDestroy() {
        // Step 6: 资源清理
        cleanupResources();

        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Step 1: 创建播放器实例
     */
    private void setupPlayer() {
        if (TextUtils.isEmpty(Constants.DataSource.SAMPLE_LIVESTREAM_VIDEO_URL)) {
            ToastUtils.showToastLong(getString(R.string.set_stream_url_first));
            return;
        }
        // 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());

        // 可选：推荐使用`播放器单点追查`功能，当使用阿里云播放器 SDK 播放视频发生异常时，可借助单点追查功能针对具体某个用户或某次播放会话的异常播放行为进行全链路追踪，以便您能快速诊断问题原因，可有效改善播放体验治理效率。
        // traceId 值由您自行定义，需为您的用户或用户设备的唯一标识符，例如传入您业务的 userid 或者 IMEI、IDFA 等您业务用户的设备 ID。
        // 传入 traceId 后，埋点日志上报功能开启，后续可以使用播放质量监控、单点追查和视频播放统计功能。
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);

        Log.d(TAG, "[Step 1] 播放器创建完成: " + mAliPlayer);

        if (mAliPlayer == null) {
            return;
        }

        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.showToastLong(errorInfo.getExtra());
            }
        });
    }

    /**
     * Step 2: 设置本地缓存
     * <p>
     * 开启本地缓存功能，提升预加载效果
     */
    private void setupLocalCache() {
        // 1.1 开启本地缓存
        AliPlayerGlobalSettings.enableLocalCache(true, this);

        /**
         * 也可以使用下方代码进行缓存设置
         * 开启本地缓存，开启之后，就会缓存到本地文件中。
         * @param enable：本地缓存功能开关。true：开启，false：关闭，默认关闭。
         * @param maxBufferMemoryKB：5.4.7.1及以后版本已废弃，暂无作用。
         * @param localCacheDir：必须设置，本地缓存的文件目录，为绝对路径。
         * AliPlayerGlobalSettings.enableLocalCache(enable, maxBufferMemoryKB, localCacheDir);
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

        Log.d(TAG, "[Step 2] 本地缓存已开启");
    }

    /**
     * Step 3: 初始化视图组件
     * <p>
     * 获取预加载控制按钮并初始化
     */
    private void setupViews() {
        mStartBtn = findViewById(R.id.mediaLoaderV2_start);
        mPauseBtn = findViewById(R.id.mediaLoaderV2_pause);
        mResumeBtn = findViewById(R.id.mediaLoaderV2_resume);
        mCancelBtn = findViewById(R.id.mediaLoaderV2_cancel);

        Log.d(TAG, "[Step 3] 视图组件初始化完成");
    }

    /**
     * Step 4 & Step 5: 设置预加载控制
     * <p>
     * 为预加载控制按钮设置点击事件监听
     */
    private void setupPreloadControls() {
        // 开始预加载
        mStartBtn.setOnClickListener(view -> {
            startPreloadWithUrl(Constants.DataSource.PRELOAD_VID_URL);
        });

        // 暂停预加载
        mPauseBtn.setOnClickListener(view -> {
            pausePreload();
        });

        // 恢复预加载
        mResumeBtn.setOnClickListener(view -> {
            resumePreload();
        });

        // 取消预加载
        mCancelBtn.setOnClickListener(view -> {
            cancelPreload();
        });

        Log.d(TAG, "[Step 4&5] 预加载控制设置完成");
    }

    /**
     * 使用 VID 方式开始预加载
     * <p>
     * 适用于阿里云 VOD 服务的视频播放
     */

    // FIXME: 使用VOD播放的预加载功能时，传入您对应的 VID 和 PlayAuth
    private void startPreloadWithVid() {
        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(SAMPLE_VID);
        vidAuth.setPlayAuth(SAMPLE_PLAY_AUTH);
        vidAuth.setQuality(DEFAULT_QUALITY);
        vidAuth.setDefinition(DEFAULT_DEFINITION_LIST);

        // 构建预加载任务
        buildPreloadTask(vidAuth);

        // 设置播放数据源
        mAliPlayer.setDataSource(vidAuth);

        Log.d(TAG, "开始 VID 预加载");
    }

    /**
     * 使用 URL 方式开始预加载
     * <p>
     * 适用于直接 URL 地址的视频播放
     *
     * @param url 视频播放地址
     */
    private void startPreloadWithUrl(String url) {
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        urlSource.setQuality(DEFAULT_QUALITY);

        // 构建预加载任务
        buildPreloadTask(urlSource);

        // 设置播放数据源
        mAliPlayer.setDataSource(urlSource);

        Log.d(TAG, "开始 URL 预加载: " + url);
    }

    /**
     * 构建 VidAuth 预加载任务
     *
     * @param vidAuth VID 播放源对象
     */
    private void buildPreloadTask(VidAuth vidAuth) {
        PreloadConfig preloadConfig = createPreloadConfig();

        mPreloadTask = new PreloadTask(vidAuth, preloadConfig);

        mTaskId = MediaLoaderV2.getInstance().addTask(mPreloadTask, new PreloadListenerImpl());

        vidAuth.setQuality(DEFAULT_QUALITY, false);

        Log.d(TAG, "VidAuth 预加载任务已创建，TaskId: " + mTaskId);
    }

    /**
     * 构建 UrlSource 预加载任务
     *
     * @param urlSource URL 播放源对象
     */
    private void buildPreloadTask(UrlSource urlSource) {
        PreloadConfig preloadConfig = createPreloadConfig();

        mPreloadTask = new PreloadTask(urlSource, preloadConfig);

        mTaskId = MediaLoaderV2.getInstance().addTask(mPreloadTask, new PreloadListenerImpl());

        Log.d(TAG, "UrlSource 预加载任务已创建，TaskId: " + mTaskId);
    }

    /**
     * 创建预加载配置
     * <p>
     * 配置预加载的各项参数
     *
     * @return PreloadConfig 预加载配置对象
     */
    private PreloadConfig createPreloadConfig() {
        PreloadConfig config = new PreloadConfig();
        // 设置预加载时长
        config.setDuration(PRELOAD_BUFFER_DURATION);
        // 设置默认清晰度
        config.setDefaultQuality(DEFAULT_QUALITY);
        // 设置默认码率
        config.setDefaultBandWidth(DEFAULT_BAND_WIDTH);
        // 设置默认分辨率
        config.setDefaultResolution(DEFAULT_RESOLUTION);

        Log.d(TAG, "预加载配置已创建");
        return config;
    }

    /**
     * 暂停预加载任务
     */
    private void pausePreload() {
        if (!TextUtils.isEmpty(mTaskId)) {
            MediaLoaderV2.getInstance().pauseTask(mTaskId);
            Log.d(TAG, "预加载任务已暂停，TaskId: " + mTaskId);
        }
    }

    /**
     * 恢复预加载任务
     */
    private void resumePreload() {
        if (!TextUtils.isEmpty(mTaskId)) {
            MediaLoaderV2.getInstance().resumeTask(mTaskId);
            Log.d(TAG, "预加载任务已恢复，TaskId: " + mTaskId);
        }
    }

    /**
     * 取消预加载任务
     */
    private void cancelPreload() {
        if (!TextUtils.isEmpty(mTaskId)) {
            MediaLoaderV2.getInstance().cancelTask(mTaskId);
            Log.d(TAG, "预加载任务已取消，TaskId: " + mTaskId);
        }
    }

    /**
     * Step 5: 预加载监听器实现
     * <p>
     * 监听预加载任务的各种状态变化
     */
    private class PreloadListenerImpl extends OnPreloadListener {

        @Override
        public void onError(@NonNull String taskId, @NonNull String urlOrVid, @NonNull ErrorInfo errorInfo) {
            ToastUtils.showToastLong(String.format(getString(R.string.preload_onError),
                    taskId, urlOrVid, errorInfo.getMsg()));
        }

        @Override
        public void onCompleted(@NonNull String taskId, @NonNull String urlOrVid) {
            Log.d(TAG, String.format(getString(R.string.preload_onCompleted), taskId, urlOrVid));
            ToastUtils.showToastLong(String.format(getString(R.string.preload_onCompleted), taskId, urlOrVid));
        }

        @Override
        public void onCanceled(@NonNull String taskId, @NonNull String urlOrVid) {
            Log.w(TAG, String.format(getString(R.string.preload_onCanceled), taskId, urlOrVid));
            ToastUtils.showToastLong(String.format(getString(R.string.preload_onCanceled), taskId, urlOrVid));
        }
    }

    /**
     * Step 6: 资源清理
     * <p>
     * 避免内存泄漏
     */
    private void cleanupResources() {
        if (mAliPlayer != null) {
            mAliPlayer.stop();
            mAliPlayer.release();
            mAliPlayer = null;
        }

        Log.d(TAG, "[Step 6] 预加载资源清理完成");
    }
}
