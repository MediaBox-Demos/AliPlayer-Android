package com.aliyun.player.downloader;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.downloader.AliDownloaderFactory;
import com.aliyun.downloader.AliMediaDownloader;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.VidAuth;
import com.aliyun.private_service.PrivateService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author 叶俊辉
 * @date 2025/6/24
 * @brief 视频下载功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现视频下载功能
 * <p>
 * ==================== 下载器 API 调用步骤 ====================
 * Step 1: 初始化组件
 * - 初始化私有服务（安全下载需要配置 encryptedApp.dat）
 * - 获取应用目录作为下载保存路径
 * <p>
 * Step 2: 初始化UI组件
 * - 绑定下载按钮
 * - 绑定下载项列表
 * <p>
 * Step 3: 设置下载器
 * - 使用 AliDownloaderFactory.create() 创建下载器
 * - 配置下载保存路径
 * - 设置下载器各种监听器
 * <p>
 * Step 4: 准备下载源 & 获取下载轨道
 * - 构造 VidAuth 并设置 vid 与 playAuth
 * - 调用下载器 prepare() 获取可下载轨道列表
 * - 展示下载选项供用户选择
 * <p>
 * Step 5: 资源清理
 * - 停止并释放下载器实例
 * - 清空相关引用，避免内存泄漏
 * <p>
 * 播放下载后的视频：本模块仅提供下载 API 示例，播放功能可参考文档自行实现。
 * // 下载完成后获取视频文件的绝对路径
 * String path = mAliDownloader.getFilePath();
 * // 通过点播 UrlSource 方式设置绝对路径进行播放
 * UrlSource urlSource = new UrlSource();
 * urlSource.setUri("播放地址"); // 设置下载视频的绝对路径
 * aliPlayer.setDataSource(urlSource);
 * aliPlayer.prepare();
 * aliPlayer.start();
 * 参考文档 <a href="https://help.aliyun.com/zh/vod/developer-reference/advanced-features">安全下载</a>
 */
public class DownloaderActivity extends AppCompatActivity {
    private static final String TAG = "DownLoadActivity";
    // 下载存储地址
    private String mDownloadPath = "";

    // 下载器相关
    private AliMediaDownloader mAliDownloader;
    private DownloaderAdapter mAdapter;

    // UI组件
    private RecyclerView mRecyclerView;
    private Button mDownloadStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_downloader_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initSafeDownloader();

        // Step 1: 初始化组件
        initComponents();

        // Step 2: 初始化UI组件
        initViews();

        // Step 3: 设置下载器
        setupDownloader();

        // Step 4: 准备下载源
        prepareDownloaderSource(Constants.DataSource.SAMPLE_VID, Constants.DataSource.SAMPLE_PLAY_AUTH);

    }

    @Override
    protected void onDestroy() {
        // Step 5: 资源清理
        cleanupResources();

        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Step 1: 初始化组件
     */
    private void initComponents() {
        // 初始化私有服务
        // 如需进行安全下载，需要配置encryptedApp.dat
        // 请参考 https://help.aliyun.com/zh/vod/developer-reference/secure-download
        // PrivateService.initService(getApplicationContext(), "encryptedApp.dat所在的文件路径");
        // 若未开启安全下载，下载后数据为明文

        // 获取应用目录
        File externalCacheDir = getExternalCacheDir();
        if (externalCacheDir != null) {
            mDownloadPath = externalCacheDir.getAbsolutePath();
            showToast("外部文件目录：" + mDownloadPath);
        } else {
            showToast("外部存储不可用");
        }
        Log.d(TAG, "[Step 1] 获取存储应用目录");
    }


    private void initSafeDownloader() {
        byte[] bytes = getAssetsFileBytes(this);
        PrivateService.initService(this, bytes);
    }

    private byte[] getAssetsFileBytes(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("encryptedApp.dat");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Step 2: 初始化视图组件
     */
    private void initViews() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mDownloadStart = findViewById(R.id.download_start);
        Log.d(TAG, "[Step 2.1] 注册视图组件");
    }

    /**
     * Step 3: 设置下载器
     */
    private void setupDownloader() {
        // 3.1 创建下载器实例
        mAliDownloader = AliDownloaderFactory.create(getApplicationContext());
        Log.d(TAG, "[Step 3.1] 创建下载器实例");
        if (mAliDownloader == null) {
            Log.e(TAG, "下载器创建失败");
            return;
        }

        // 3.2 配置下载保存路径
        mAliDownloader.setSaveDir(mDownloadPath);
        Log.d(TAG, "[Step 3.2] 配置下载保存路径: " + mDownloadPath);

        // 3.3 设置下载器监听
        setDownloaderListeners();

        Log.d(TAG, "[Step 3.3] 下载器设置完成");
    }

    /**
     * 设置下载器监听器
     */
    private void setDownloaderListeners() {
        if (mAliDownloader == null) return;

        // 准备下载器成功监听
        mAliDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {
                Log.d(TAG, "[Step 4] 下载器准备成功");

                // 下载器准备成功 & 获取下载源
                onDownloaderPreparedSuccess(mediaInfo);
            }
        });

        // 下载进度监听
        mAliDownloader.setOnProgressListener(new AliMediaDownloader.OnProgressListener() {
            @Override
            public void onDownloadingProgress(int percent) {
                Log.d(TAG, "下载进度: " + percent + "%");
            }

            @Override
            public void onProcessingProgress(int percent) {
                // 处理进度
            }
        });

        // 下载错误监听
        mAliDownloader.setOnErrorListener(new AliMediaDownloader.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG, "下载失败: " + errorInfo.getMsg());
                showToast("下载失败: " + errorInfo.getMsg());
                resetDownloadUI();
                stopAndReleaseDownloader();
            }
        });

        // 下载完成监听
        mAliDownloader.setOnCompletionListener(new AliMediaDownloader.OnCompletionListener() {
            @Override
            public void onCompletion() {
                Log.d(TAG, "下载完成");
                showToast("下载完成");
                resetDownloadUI();
                stopAndReleaseDownloader();
            }
        });
    }

    /**
     * Step 4: 下载器准备成功 & 获取下载源
     */
    private void onDownloaderPreparedSuccess(MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            Log.e(TAG, "MediaInfo为空");
            return;
        }

        List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
        if (trackInfos == null || trackInfos.isEmpty()) {
            Log.e(TAG, "TrackInfos为空");
            showToast("没有可下载的轨道");
            return;
        }

        // 设置适配器
        mAdapter = new DownloaderAdapter(trackInfos);
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        }

        // 默认选中第一项
        mAdapter.setSelectedPosition(0);

        // 设置下载按钮点击监听
        setupDownloadButton();
    }

    /**
     * 设置下载按钮
     */
    private void setupDownloadButton() {
        if (mDownloadStart != null && mAdapter != null) {
            mDownloadStart.setOnClickListener(v -> startDownload());
        }
    }

    /**
     * 开始下载
     */
    private void startDownload() {
        if (mAliDownloader == null || mAdapter == null) {
            Log.e(TAG, "下载器或适配器未初始化");
            return;
        }

        TrackInfo selectedTrack = mAdapter.getSelectedTrackInfo();
        if (selectedTrack == null) {
            showToast("请选择下载项");
            return;
        }

        // 选择下载项并开始下载
        mAliDownloader.selectItem(mAdapter.getSelectedPosition());
        mAliDownloader.start();

        // 隐藏UI
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.GONE);
        }
        if (mDownloadStart != null) {
            mDownloadStart.setVisibility(View.GONE);
        }

        showToast("开始下载");
        Log.d(TAG, "开始下载选中项");
    }

    /**
     * 准备下载源
     */
    private void prepareDownloaderSource(String vid, String playAuth) {
        if (mAliDownloader == null) {
            Log.e(TAG, "下载器未初始化");
            return;
        }

        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(vid);
        vidAuth.setPlayAuth(playAuth);
        mAliDownloader.prepare(vidAuth);

        Log.d(TAG, "[Step 4] 准备下载源");
    }

    /**
     * 重置下载UI
     */
    private void resetDownloadUI() {
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        if (mDownloadStart != null) {
            mDownloadStart.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 停止并释放下载器
     */
    private void stopAndReleaseDownloader() {
        if (mAliDownloader != null) {
            mAliDownloader.stop();
            mAliDownloader.release();
        }
    }

    /**
     * Step 5: 资源清理
     */
    private void cleanupResources() {
        // 5.1 清理下载器资源
        stopAndReleaseDownloader();
        mAliDownloader = null;

        // 5.2 清理其他引用
        mAdapter = null;

        Log.d(TAG, "[Step 5] 资源清理完成");
    }

    /**
     * 显示Toast消息
     */
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }
}