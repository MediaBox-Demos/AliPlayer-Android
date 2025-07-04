package com.aliyun.player.thumbnail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.videoview.AliDisplayView;
import com.aliyun.thumbnail.ThumbnailBitmapInfo;
import com.aliyun.thumbnail.ThumbnailHelper;

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
 * - 可选：设置 traceId 开启播放器单点追查
 * <p>
 * Step 2: 初始化视图
 * - 加载 AliDisplayView 并设置视图类型
 * - 加载 缩略图视图
 * - 调用 setDisplayView() 设置视图
 * <p>
 * Step 3: 设置播放源
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * <p>
 * Step 4: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法开始播放
 * <p>
 * Step 5: 设置播放器准备完成监听
 * - 设置进度条长度 mSeekBar.setMax((int) mAliPlayer.getDuration());
 * <p>
 * Step 6: 设置缩略图相关监听
 * - 调用 setOnThumbnailGetListener 设置监听
 * - 在onThumbnailGetSuccess() 方法中调用 getThumbnailBitmap() 获取指定位置的Bitmap
 * - 对缩略图的图片进行设置
 * <p>
 * Step 7: 缩略图显示
 * - 设置 seekBar 监听 setOnSeekBarChangeListener
 * - 在onProgressChanged() 中调用 requestBitmapAtPosition() 获取指定位置的缩略图
 * - 在onStartTrackingTouch() 中进行缩略图显示设置
 * - 在onStopTrackingTouch() 中进行缩略图隐藏
 * - 在onStopTrackingTouch() 中调用 seekTo()
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

    // 创建缩略图帮助类
    private ThumbnailHelper mThumbnailHelper;

    // 拖动按钮
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

        // Step 3 & Step 4: 设置播放源并准备播放
        startPlayback();

        // Step 5 设置播放器准备完成监听
        setPlayerPrepared();

        // Step 6: 设置缩略图相关监听及缩略图实现
        /**
         * 注：Step 6有两种方式进行设置
         * 可参考：https://help.aliyun.com/zh/vod/developer-reference/faq-about-apsaravideo-player-sdk-for-android
         * 可以使用mAliPlayer.getMediaInfo()的方式来获取缩略图
         * 注：若当前使用VID方式播放，创建ThumbnailHelper对象可参考下方注释
         * getThumbnail onPrepared()监听中调用
         * List<Thumbnail> thumbnailList = mAliPlayer.getMediaInfo().getThumbnailList();
         * getThumbnail(thumbnailList.get(0).mURL);
         */
        getThumbnail(Constants.DataSource.THUMBNAIL_URL);

        // Step 7 缩略图视图显示
        setViewListener();
    }

    /**
     * Step 1: 创建播放器实例
     */
    private void setupPlayer() {
        // 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());

        // 可选：推荐使用`播放器单点追查`功能，当使用阿里云播放器 SDK 播放视频发生异常时，可借助单点追查功能针对具体某个用户或某次播放会话的异常播放行为进行全链路追踪，以便您能快速诊断问题原因，可有效改善播放体验治理效率。
        // traceId 值由您自行定义，需为您的用户或用户设备的唯一标识符，例如传入您业务的 userid 或者 IMEI、IDFA 等您业务用户的设备 ID。
        // 传入 traceId 后，埋点日志上报功能开启，后续可以使用播放质量监控、单点追查和视频播放统计功能。
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);

        Log.d(TAG, "[Step 1] 播放器创建完成: " + mAliPlayer);
    }

    /**
     * Step 2: 初始化视图组件
     */
    private void initViews() {
        mAliDisplayView = findViewById(R.id.ali_display_view);
        // 可以通过 setPreferDisplayView() 设置播放视图类型
        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);

        mAliPlayer.setDisplayView(mAliDisplayView);

        mThumbnailView = findViewById(R.id.thumbnail_view);

        mSeekBar = findViewById(R.id.seekBar);
        // 初始隐藏视图
        mThumbnailView.hideThumbnailView();

        Log.d(TAG, "[Step 2] 播放器视图初始化完成");
    }

    /**
     * Step 3: 设置播放源 & Step 4: 开始播放
     */
    private void startPlayback() {
        // Step 3: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.THUMBNAIL_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        // 支持设置宽高比适应、宽高比填充和拉伸填充这3种画面填充模式，由setScaleMode接口实现。
        // 参考文档：
        // https://help.aliyun.com/zh/vod/developer-reference/basic-features-1
        mAliPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
        // Step 4: 准备播放
        mAliPlayer.prepare();
        // prepare 以后可以同步调用 start 操作，onPrepared 回调完成后会自动起播
        mAliPlayer.start();

        Log.d(TAG, "[Step 3&4] 开始播放视频: " + Constants.DataSource.SAMPLE_VIDEO_URL);
    }

    /**
     * Step 5: 设置播放器准备完成监听
     */
    private void setPlayerPrepared() {
        // Step 5: 播放器准备完成监听
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                // 5.1 设置进度条长度
                mSeekBar.setMax((int) mAliPlayer.getDuration());

                Log.d(TAG, "[Step 5] 播放器准备完成");
            }
        });
    }

    // Step 6 设置缩略图相关监听及缩略图实现
    private void getThumbnail(String thumbnail) {
        mThumbnailHelper = new ThumbnailHelper(thumbnail);
        // 设置缩略图相关监听。
        mThumbnailHelper.setOnPrepareListener(new ThumbnailHelper.OnPrepareListener() {
            @Override
            public void onPrepareSuccess() {
                // 6.1 缩略图加载成功后，可以请求获取指定位置的缩略图。
                Log.d(TAG, "[Step 6.1] 缩略图准备完成");
            }

            @Override
            public void onPrepareFail() {
                Log.d(TAG, "[Step 6.1] 缩略图准备失败");
            }
        });

        mThumbnailHelper.setOnThumbnailGetListener(new ThumbnailHelper.OnThumbnailGetListener() {
            @Override
            public void onThumbnailGetSuccess(long positionMs, ThumbnailBitmapInfo thumbnailBitmapInfo) {
                if (thumbnailBitmapInfo != null && thumbnailBitmapInfo.getThumbnailBitmap() != null) {
                    // 6.2 获取指定位置缩略图的Bitmap。
                    Bitmap thumbnailBitmap = thumbnailBitmapInfo.getThumbnailBitmap();

                    // 6.3 缩略图图片设置(缩略图视图需要自定义)
                    mThumbnailView.setThumbnailPicture(thumbnailBitmap);
                }
            }

            @Override
            public void onThumbnailGetFail(long positionMs, String errorMsg) {

            }
        });

        // 6.4 加载缩略图。
        mThumbnailHelper.prepare();

        Log.d(TAG, "[Step 6.4] 缩略图加载");
    }

    // Step: 7 缩略图显示 & 进度条更新
    private void setViewListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mThumbnailHelper != null) {
                    // 7.1 请求获取指定位置的缩略图。
                    mThumbnailHelper.requestBitmapAtPosition(progress);

                    // 缩略图 TextView 设置(缩略图视图需要自定义)
                    mThumbnailView.setTime(TimeFormatter.formatMs(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mThumbnailView != null) {
                    // 7.2 缩略图显示
                    mThumbnailView.showThumbnailView();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mThumbnailHelper != null) {
                    // 7.3 缩略图隐藏
                    mThumbnailView.hideThumbnailView();
                    // 进度seek到指定位置
                    mAliPlayer.seekTo(seekBar.getProgress(), IPlayer.SeekMode.Accurate);
                }
            }
        });

        // 进度条更新 (默认500ms 最块100ms)
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
            // 8.1 停止播放
            mAliPlayer.stop();

            // 8.2 销毁播放器实例
            mAliPlayer.release();

            // 8.3 清空引用，避免内存泄漏
            mAliPlayer = null;
            mThumbnailHelper = null;

            Log.d(TAG, "[Step 8] 播放器资源清理完成");
        }
    }
}