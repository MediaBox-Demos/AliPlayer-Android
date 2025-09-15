package com.aliyun.player.downloader;

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
import com.aliyun.player.AliPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.common.bean.PlayAuthBean;
import com.aliyun.player.common.network.HttpClientUtils;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.VidAuth;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
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
 * - 初始化私有服务和UI组件
 * - 获取应用目录
 * Step 2: 网络请求获取playAuth
 * - 调用工具类进行网络请求
 * - VidAuth 获取vid和网络请求的playAuth
 * - 下载器调用 prepare() 方法
 * <p>
 * Step 3: 初始化UI组件
 * - 绑定下载按钮
 * - 绑定下载项列表
 * <p>
 * Step 4: 设置下载器
 * - 使用 AliDownloaderFactory.create() 创建下载器
 * - 配置下载保存路径
 * - 设置下载器各种监听器
 * <p>
 * Step 5: 下载器准备成功 & 获取下载源
 * - 通过网络请求获取播放授权
 * - 准备下载源并获取可下载轨道列表
 * - 展示下载选项供用户选择
 * <p>
 * Step 6: 播放视频
 * - 本模块只提供了下载API 示例，您可以参考文档自行实现播放功能
 * // 下载完成后获取视频文件的绝对路径
 * String path = mAliDownloader.getFilePath();
 * // 通过点播UrlSource方式设置绝对路径进行播放。
 * UrlSource urlSource = new UrlSource();
 * UrlSource.setUri("播放地址");//设置下载视频的绝对路径。
 * aliPlayer.setDataSource(urlSource);
 * aliPlayer.prepare();
 * aliPlayer.start();
 * 参考文档 <a href="https://help.aliyun.com/zh/vod/developer-reference/advanced-features">安全下载</a>
 * <p>
 * Step 7: 资源清理
 * - 停止并释放播放器实例
 * - 停止并释放下载器实例
 * - 清空相关引用，避免内存泄漏
 */
public class DownloaderActivity extends AppCompatActivity {
    private static final String TAG = "DownLoadActivity";
    // 下载存储地址
    private String mDownloadPath = "";

    // 播放器相关
    private AliPlayer mAliPlayer;

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

        // Step 1: 初始化组件
        initComponents();

        // Step 2: 网络请求获取playAuth
        requestPlayAuth(Constants.DataSource.REQUEST_PLAY_AUTH_URL + Constants.DataSource.DOWNLOAD_VID);

        // Step 3: 初始化UI组件
        initViews();

        // Step 4: 设置下载器
        setupDownloader();
    }

    @Override
    protected void onDestroy() {
        // Step 7: 资源清理
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

    /**
     * Step 2: 网络请求获取playAuth
     * 简单的网络请求 实际项目中请替换为您项目中的网络请求工具
     * @param url playAuth请求接口
     * url 换自己的 API 域名业务进行业务请求
     */
    private void requestPlayAuth(String url) {
        // 简单的网络请求工具 实际项目中请替换为您项目中的网络请求工具
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 网络请求，获取playAuth
                String responseData = HttpClientUtils.getContentFromURI(url);
                if (responseData == null) {
                    showToast("网络请求失败");
                    return;
                }

                Gson gson = new Gson();
                try {
                    PlayAuthBean playAuthBean = gson.fromJson(responseData, PlayAuthBean.class);
                    String playAuth = playAuthBean.getData().getPlayAuth();
                    // 准备下载资源
                    prepareDownloaderSource(Constants.DataSource.DOWNLOAD_VID, playAuth);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    showToast("JSON 解析错误" + e.getMessage());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    showToast("不匹配的类型" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Step 3: 初始化视图组件
     */
    private void initViews() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mDownloadStart = findViewById(R.id.download_start);
        Log.d(TAG, "[Step 3.1] 注册视图组件");
    }

    /**
     * Step 4: 设置下载器
     */
    private void setupDownloader() {
        // 4.1 创建下载器实例
        mAliDownloader = AliDownloaderFactory.create(getApplicationContext());
        Log.d(TAG, "[Step 4.1] 创建下载器实例");
        if (mAliDownloader == null) {
            Log.e(TAG, "下载器创建失败");
            return;
        }

        // 4.2 配置下载保存路径
        mAliDownloader.setSaveDir(mDownloadPath);
        Log.d(TAG, "[Step 4.2] 配置下载保存路径: " + mDownloadPath);

        // 4.3 设置下载器监听
        setDownloaderListeners();

        Log.d(TAG, "[Step 4.3] 下载器设置完成");
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
                Log.d(TAG, "[Step 4.3] 准备下载器成功");

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
     * Step 5: 下载器准备成功 & 获取下载源
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

        Log.d(TAG, "[Step 1.5] 准备下载源");
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
     * Step 7: 资源清理
     */
    private void cleanupResources() {
        // 6.1 清理播放器资源
        if (mAliPlayer != null) {
            mAliPlayer.stop();
            mAliPlayer.release();
            mAliPlayer = null;
        }

        // 6.2 清理下载器资源
        stopAndReleaseDownloader();
        mAliDownloader = null;

        // 6.3 清理其他引用
        mAdapter = null;

        Log.d(TAG, "[Step 6] 资源清理完成");
    }

    /**
     * 显示Toast消息
     */
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }
}