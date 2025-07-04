package com.aliyun.player.thumbnail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;
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
 * - 设置播放器渲染视图 TextureView
 * - 配置播放器基本参数
 * <p>
 * Step 2: 设置播放源
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * <p>
 * Step 3: 准备播放
 * - 调用 prepare() 方法准备播放
 * <p>
 * Step 4: 设置播放器准备完成监听
 * - 设置进度条长度 mSeekBar.setMax((int) mAliPlayer.getDuration());
 * - 调用 start() 开始播放
 * <p>
 * Step 5: 设置缩略图相关监听
 * - 调用 setOnThumbnailGetListener 设置监听
 * - 调用 requestBitmapAtPosition() 请求指定位置缩略图
 * - 在onThumbnailGetSuccess() 方法中调用 getThumbnailBitmap() 获取指定位置的Bitmap
 * - 对缩略图的图片进行设置
 * <p>
 * Step 6: 缩略图显示
 * - 设置 seekBar 监听 setOnSeekBarChangeListener
 * - 在onProgressChanged() 中调用 requestBitmapAtPosition() 获取指定位置的缩略图
 * - 在onStartTrackingTouch() 中进行缩略图显示设置
 * - 在onStopTrackingTouch() 中进行缩略图隐藏
 * - 在onStopTrackingTouch() 中调用 seekTo()
 * <p>
 * Step 7: 清理资源
 * - 调用 releaseAsync() 异步销毁播放器实例
 */
public class ThumbnailActivity extends AppCompatActivity {
    private static final String TAG = "ThumbnailActivity";

    // 播放器实例
    private AliPlayer mAliPlayer;

    // 播放器视图
    private SurfaceView mSurfaceView;

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
            getSupportActionBar().setTitle(getString(R.string.thumbnail_demo_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // 初始化视图
        initViews();

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2 & Step 3: 设置播放源并准备播放
        startPlayback();

        // Step 4 设置播放器准备完成监听 Step 5 进行缩略图相关设置
        setThumbnail();

        // Step6 缩略图视图显示
        setViewListener();
    }

    private void initViews() {
        mSurfaceView = findViewById(R.id.surface_view);
        mThumbnailView = findViewById(R.id.thumbnail_view);
        mSeekBar = findViewById(R.id.seekBar);
        // 初始隐藏视图
        mThumbnailView.hideThumbnailView();
    }

    private void setupPlayer() {
        // 1.1 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);

        // 1.2 设置播放器渲染视图
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mAliPlayer.setSurface(holder.getSurface());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                mAliPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                mAliPlayer.setSurface(null);
            }
        });
    }

    /**
     * Step 2: 设置播放源 & Step 3: 准备播放
     */
    private void startPlayback() {
        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.THUMBNAIL_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        mAliPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
        // Step 3: 准备播放
        mAliPlayer.prepare();
    }

    /**
     * Step 4: 设置播放器准备完成监听 & Step 5: 设置缩略图相关监听
     */
    private void setThumbnail() {
        // Step 4: 准备完成监听
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                // 4.1 设置进度条长度
                mSeekBar.setMax((int) mAliPlayer.getDuration());
                // 4.2 开始播放
                mAliPlayer.start();
                // Step5: 创建缩略图帮助类。
                // 注：Step5有两种方式进行设置，可参考(https://help.aliyun.com/zh/vod/developer-reference/faq-about-apsaravideo-player-sdk-for-android?scm=20140722.S_help%40%40文档%40%40442223._.ID_help%40%40文档%40%40442223-RL_缩略图-LOC_doc%7EUND%7Eab-OR_ser-PAR1_212a5d4017500395211985046d20cc-V_4-RE_new5-P0_1-P1_0&spm=a2c4g.11186623.help-search.i1)
                // 可以使用mAliPlayer.getMediaInfo()的方式来获取缩略图
                mThumbnailHelper = new ThumbnailHelper(Constants.DataSource.THUMBNAIL_URL);
                // 5.1 设置缩略图相关监听。
                mThumbnailHelper.setOnPrepareListener(new ThumbnailHelper.OnPrepareListener() {
                    @Override
                    public void onPrepareSuccess() {
                        // 5.2 缩略图加载成功后，可以请求获取指定位置的缩略图。
                        mThumbnailHelper.requestBitmapAtPosition(1000);
                        Log.d(TAG, "onPrepareSuccess");
                    }

                    @Override
                    public void onPrepareFail() {
                        Log.d(TAG, "onPrepareFail");
                    }
                });

                mThumbnailHelper.setOnThumbnailGetListener(new ThumbnailHelper.OnThumbnailGetListener() {
                    @Override
                    public void onThumbnailGetSuccess(long positionMs, ThumbnailBitmapInfo thumbnailBitmapInfo) {
                        if (thumbnailBitmapInfo != null && thumbnailBitmapInfo.getThumbnailBitmap() != null) {
                            // 5.3 获取指定位置缩略图的Bitmap。
                            Bitmap thumbnailBitmap = thumbnailBitmapInfo.getThumbnailBitmap();
                            Log.d(TAG, "onThumbnailGetSuccess");

                            // 5.4 缩略图图片设置(缩略图视图需要自定义)
                            mThumbnailView.setThumbnailPicture(thumbnailBitmap);
                        }
                    }

                    @Override
                    public void onThumbnailGetFail(long positionMs, String errorMsg) {
                        Log.d(TAG, "onThumbnailGetFail");
                    }
                });

                // 5.6 加载缩略图。
                mThumbnailHelper.prepare();
            }
        });
    }

    // Step: 6 缩略图显示
    private void setViewListener() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mThumbnailHelper != null) {
                    // 6.1 请求获取指定位置的缩略图。
                    mThumbnailHelper.requestBitmapAtPosition(progress);
                    // 6.2 缩略图 TextView 设置(缩略图视图需要自定义)
                    mThumbnailView.setTime(TimeFormater.formatMs(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mThumbnailView != null) {
                    // 6.2 缩略图显示
                    mThumbnailView.showThumbnailView();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mThumbnailHelper != null) {
                    // 6.3 缩略图隐藏
                    mThumbnailView.hideThumbnailView();
                    mAliPlayer.seekTo(seekBar.getProgress());
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
        super.onDestroy();
        // Step 7：清理资源
        mAliPlayer.releaseAsync();
        mThumbnailHelper = null;
    }
}