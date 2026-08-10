package com.aliyun.player.thumbnail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.common.Constants;
import com.aliyun.player.common.utils.ToastUtils;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerScene;
import com.aliyun.player.nativeclass.Thumbnail;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.videoview.AliDisplayView;
import com.aliyun.thumbnail.ThumbnailBitmapInfo;
import com.aliyun.thumbnail.ThumbnailHelper;

import java.util.List;

/**
 * @author junhuiYe
 * @date 2025/6/9
 * @brief 功能演示缩略图 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现缩略图功能演示
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1: 创建播放器实例
 * - 使用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 设置播放场景 setPlayerScene()
 * - 设置错误监听 setOnErrorListener()
 * - 可选：设置 traceId 开启播放器单点追查
 * <p>
 * Step 2: 初始化视图
 * - 加载 AliDisplayView 并设置视图类型
 * - 加载缩略图视图 ThumbnailView
 * - 调用 setDisplayView() 设置播放视图
 * <p>
 * Step 3: 设置播放源
 * - 创建 VidAuth 播放源对象(点播凭证鉴权方式，需设置 vid + playAuth)
 * - 调用 setDataSource() 设置播放源
 * <p>
 * Step 4: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法开始播放(prepare 后可直接调用，onPrepared 回调后自动起播)
 * <p>
 * Step 5: 设置播放器准备完成监听
 * - 设置进度条长度 mSeekBar.setMax((int) mAliPlayer.getDuration())
 * - 从 MediaInfo 中取出缩略图列表 getThumbnailList()
 * - 拿到缩略图 URL 后触发 Step 6，加载缩略图(此步骤在回调内部完成，不在 onCreate 中直接调用)
 * <p>
 * Step 6: 加载缩略图 & 设置缩略图相关监听
 * - 使用缩略图 URL 创建 ThumbnailHelper
 * - 调用 setOnPrepareListener 监听缩略图资源准备结果
 * - 调用 setOnThumbnailGetListener 监听指定位置缩略图的获取结果
 * - 在 onThumbnailGetSuccess() 中通过 thumbnailBitmapInfo.getThumbnailBitmap() 获取 Bitmap 并设置到视图
 * - 调用 prepare() 加载缩略图资源
 * <p>
 * Step 7: 缩略图显示 & 进度条联动
 * - 设置 seekBar 监听 setOnSeekBarChangeListener
 * - 在 onProgressChanged() 中调用 requestBitmapAtPosition() 请求指定位置的缩略图
 * - 在 onStartTrackingTouch() 中显示缩略图视图
 * - 在 onStopTrackingTouch() 中隐藏缩略图视图，并调用 seekTo() 跳转播放进度
 * - 通过 setOnInfoListener 监听播放进度，实时更新 seekBar
 * <p>
 * Step 8: 清理资源
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 */
public class ThumbnailActivity extends AppCompatActivity {
    private static final String TAG = "ThumbnailActivity";

    // 播放器实例
    private AliPlayer mAliPlayer;

    // 播放器视图
    private AliDisplayView mAliDisplayView;

    // 缩略图帮助类
    private ThumbnailHelper mThumbnailHelper;

    // 拖动进度条
    private SeekBar mSeekBar;

    /**
     * 缩略图View
     */
    private ThumbnailView mThumbnailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail);
        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_thumbnail_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2: 初始化视图
        initViews();

        // Step 3 & Step 4: 设置播放源并开始播放
        startPlayback();

        // Step 5: 设置播放器准备完成监听(Step 6 加载缩略图会在其回调内部触发，此处不直接调用)
        setPlayerPrepared();

        // Step 7: 设置进度条与缩略图显示的联动监听
        setViewListener();
    }

    /**
     * Step 1: 创建播放器实例
     */
    private void setupPlayer() {
        // 1.1 创建播放器实例
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

        // 1.2 设置播放场景
        mAliPlayer.setPlayerScene(PlayerScene.LONG);

        // 1.3 设置错误监听
        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.showToastLong(errorInfo.getExtra());
            }
        });
    }

    /**
     * Step 2: 初始化视图组件
     */
    private void initViews() {
        // 2.1 初始化播放视图，可通过 setPreferDisplayView() 设置优先使用的视图类型
        mAliDisplayView = findViewById(R.id.ali_display_view);
        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);
        mAliPlayer.setDisplayView(mAliDisplayView);

        // 2.2 初始化缩略图视图，默认隐藏
        mThumbnailView = findViewById(R.id.thumbnail_view);
        mThumbnailView.hideThumbnailView();

        // 2.3 初始化进度条
        mSeekBar = findViewById(R.id.seekBar);

        Log.d(TAG, "[Step 2] 播放器视图初始化完成");
    }

    /**
     * Step 3: 设置播放源 & Step 4: 开始播放
     */
    private void startPlayback() {
        // Step 3: 创建 VidAuth 播放源对象并设置播放地址(点播凭证鉴权方式)
        VidAuth vidAuth = new VidAuth();
        vidAuth.setVid(Constants.DataSource.THUMBNAIL_VIDEO_VID);
        vidAuth.setPlayAuth(Constants.DataSource.THUMBNAIL_VIDEO_AUTH);
        mAliPlayer.setDataSource(vidAuth);

        // 支持设置宽高比适应、宽高比填充和拉伸填充这3种画面填充模式，由 setScaleMode 接口实现。
        // 参考文档：https://help.aliyun.com/zh/vod/developer-reference/basic-features-1
        mAliPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);

        // Step 4: 准备播放并开始播放
        mAliPlayer.prepare();
        // prepare 之后可以同步调用 start 操作，onPrepared 回调完成后会自动起播
        mAliPlayer.start();

        Log.d(TAG, "[Step 3&4] 开始播放视频");
    }

    /**
     * Step 5: 设置播放器准备完成监听
     */
    private void setPlayerPrepared() {
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                // 5.1 设置进度条长度
                mSeekBar.setMax((int) mAliPlayer.getDuration());

                Log.d(TAG, "[Step 5] 播放器准备完成");

                // 5.2 从 MediaInfo 中取出缩略图列表，取第一个缩略图资源地址触发 Step 6
                if (null != mAliPlayer.getMediaInfo()) {
                    MediaInfo mediaInfo = mAliPlayer.getMediaInfo();
                    List<Thumbnail> thumbnailList = mediaInfo.getThumbnailList();
                    Log.d(TAG, "[Step 5] 缩略图列表 " + thumbnailList.size());
                    if (!thumbnailList.isEmpty()) {
                        Thumbnail firstThumbnail = thumbnailList.get(0);
                        if (firstThumbnail != null && firstThumbnail.mURL != null && !firstThumbnail.mURL.isEmpty()) {
                            Log.d(TAG, "[Step 5] 缩略图 " + firstThumbnail.mURL);

                            // 触发 Step 6：加载缩略图
                            getThumbnail(firstThumbnail.mURL);
                        }
                    } else {
                        Log.d(TAG, "[Step 5] 缩略图列表为空");
                    }
                }
            }
        });
    }

    /**
     * Step 6: 加载缩略图 & 设置缩略图相关监听
     */
    private void getThumbnail(String thumbnail) {
        // 6.1 使用缩略图地址创建 ThumbnailHelper
        mThumbnailHelper = new ThumbnailHelper(thumbnail);

        // 6.2 设置缩略图资源准备结果监听
        mThumbnailHelper.setOnPrepareListener(new ThumbnailHelper.OnPrepareListener() {
            @Override
            public void onPrepareSuccess() {
                // 缩略图资源加载成功后，才能正常请求指定位置的缩略图
                Log.d(TAG, "[Step 6.2] 缩略图准备完成");
            }

            @Override
            public void onPrepareFail() {
                Log.d(TAG, "[Step 6.2] 缩略图准备失败");
            }
        });

        // 6.3 设置指定位置缩略图的获取结果监听
        mThumbnailHelper.setOnThumbnailGetListener(new ThumbnailHelper.OnThumbnailGetListener() {
            @Override
            public void onThumbnailGetSuccess(long positionMs, ThumbnailBitmapInfo thumbnailBitmapInfo) {
                if (thumbnailBitmapInfo != null && thumbnailBitmapInfo.getThumbnailBitmap() != null) {
                    // 获取指定位置缩略图的 Bitmap
                    Bitmap thumbnailBitmap = thumbnailBitmapInfo.getThumbnailBitmap();

                    // 将缩略图设置到视图上(缩略图视图需要自定义)
                    mThumbnailView.setThumbnailPicture(thumbnailBitmap);
                }
            }

            @Override
            public void onThumbnailGetFail(long positionMs, String errorMsg) {
                ToastUtils.showToastLong(errorMsg);
            }
        });

        // 6.4 加载缩略图资源
        mThumbnailHelper.prepare();

        Log.d(TAG, "[Step 6.4] 缩略图加载");
    }

    /**
     * Step 7: 缩略图显示 & 进度条更新
     */
    private void setViewListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mThumbnailHelper != null) {
                    // 7.1 请求获取指定位置的缩略图
                    mThumbnailHelper.requestBitmapAtPosition(progress);

                    // 更新缩略图上的时间文案(缩略图视图需要自定义)
                    mThumbnailView.setTime(TimeFormatter.formatMs(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mThumbnailView != null) {
                    // 7.2 开始拖动时显示缩略图
                    mThumbnailView.showThumbnailView();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mThumbnailHelper != null) {
                    // 7.3 拖动结束后隐藏缩略图，并跳转到指定播放位置
                    mThumbnailView.hideThumbnailView();
                    mAliPlayer.seekTo(seekBar.getProgress(), IPlayer.SeekMode.Accurate);
                }
            }
        });

        // 7.4 监听播放进度，实时更新 seekBar(默认 500ms 回调一次，最快 100ms)
        mAliPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
                if (infoBean.getCode() == InfoCode.CurrentPosition) {
                    mSeekBar.setProgress((int) infoBean.getExtraValue());
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        cleanupPlayer();
        super.onDestroy();
    }

    /**
     * Step 8: 资源清理
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 8.1 解绑播放器视图
            mAliPlayer.setDisplayView(null);

            // 8.2 停止播放
            mAliPlayer.stop();

            // 8.3 销毁播放器实例
            mAliPlayer.release();

            // 8.4 清空引用，避免内存泄漏
            mAliPlayer = null;
            mThumbnailHelper = null;

            Log.d(TAG, "[Step 8] 播放器资源清理完成");
        }
    }
}