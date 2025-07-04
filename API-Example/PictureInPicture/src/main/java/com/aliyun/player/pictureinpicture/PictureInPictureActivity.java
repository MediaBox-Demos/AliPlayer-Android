package com.aliyun.player.pictureinpicture;

import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.common.Constants;
import com.aliyun.player.source.UrlSource;


public class PictureInPictureActivity extends AppCompatActivity {

    private final static String TAG = "PipActivity";

    private AliPlayer mAliPlayer;
    private SurfaceView mSurfaceView;

    private Button mBtnPip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_in_picture);
        Log.e(TAG, "onCreate 创建" + (savedInstanceState != null));

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.basic_pip_demo_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // 初始化视图
        initViews();

        // Step 1: 创建播放器实例
        setupPlayer();

        // Step 2 & Step 3: 设置播放源并开始播放
        startPlayback();
    }


    private void setUIShow(boolean isPipShow) {
        if (getSupportActionBar() != null) {
            if (isPipShow) {
                // 进入画中画模式时的处理
                getSupportActionBar().hide();
                mBtnPip.setVisibility(View.GONE);
                Log.e(TAG, "进入画中画模式");
            } else {
                // 退出画中画模式时的处理
                getSupportActionBar().show();
                mBtnPip.setVisibility(View.VISIBLE);
                Log.e(TAG, "退出画中画模式");
            }
        }
    }

    /**
     * 离开当前activity 时调用
     */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        enterPiPMode();

        Log.e(TAG,"画中画 onUserLeaveHint");
    }


    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        setUIShow(isInPictureInPictureMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Step 4: 资源清理
        cleanupPlayer();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInPictureInPictureMode()) {
                finish();
            }
        }
        Log.e(TAG, "OnStop 停止");
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        mSurfaceView = findViewById(R.id.surface_view);
        mBtnPip = findViewById(R.id.btn_pip);
        mBtnPip.setOnClickListener(view -> enterPiPMode());
    }

    /**
     * 启用画中画
     */
    private void enterPiPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(16, 9); // 画中画的宽高比
            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(aspectRatio);
            enterPictureInPictureMode(pipBuilder.build());
        }
    }

    /**
     * Step 1: 创建播放器实例
     */
    private void setupPlayer() {
        // 1.1 创建播放器实例
        mAliPlayer = AliPlayerFactory.createAliPlayer(getApplicationContext());

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

        // 1.3 配置播放器基本参数
        mAliPlayer.setAutoPlay(true);
        mAliPlayer.setLoop(true);

        Log.d(TAG, "[Step 1] 播放器创建完成: " + mAliPlayer);
    }

    /**
     * Step 2: 设置播放源 & Step 3: 开始播放
     */
    private void startPlayback() {
        // Step 2: 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.SAMPLE_VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        // Step 3: 准备播放（会自动开始播放，因为设置了 autoPlay = true）
        mAliPlayer.prepare();

        Log.d(TAG, "[Step 2&3] 开始播放视频: " + Constants.DataSource.SAMPLE_VIDEO_URL);
    }

    /**
     * Step 4: 资源清理
     */
    private void cleanupPlayer() {
        if (mAliPlayer != null) {
            // 4.1 停止播放
            mAliPlayer.stop();

            // 4.2 销毁播放器实例
            mAliPlayer.release();

            // 4.3 清空引用，避免内存泄漏
            mAliPlayer = null;

            Log.d(TAG, "[Step 4] 播放器资源清理完成");
        }
    }
}
