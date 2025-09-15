package com.aliyun.player.multiresolution;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.common.Constants;
import com.aliyun.player.common.utils.ToastUtils;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.videoview.AliDisplayView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyq
 * @date 2025/7/3
 * @brief 多码率视频切换功能演示 - 阿里云播放器 SDK 最佳实践
 * <p>
 * 本示例展示了如何使用阿里云播放器 SDK 实现多码率视频切换
 * <p>
 * ==================== 播放器 API 调用步骤 ====================
 * Step 1:初始化视图UI
 * - 设置播放器渲染视图 AliDisplayView
 * - 初始化清晰度列表UI RecycleView
 * - 设置清晰度列表点击事件 MultiResolutionAdapter.setOnItemClickListener
 * <p>
 * Step 2: 设置播放源 && 设置清晰度切换监听
 * - 调用 AliPlayerFactory.createAliPlayer() 创建播放器
 * - 创建 UrlSource 播放源对象
 * - 调用 setDataSource() 设置播放地址
 * - 调用 setOnTrackChangedListener() 设置播放地址
 * <p>
 * Step 3: 获取并筛选清晰度数据
 * - 监听 setOnPreparedListener 准备获取清晰度数据
 * - 调用 getMediaInfo().getTrackInfos() 筛选清晰度数据
 * <p>
 * Step 4: 开始播放
 * - 调用 prepare() 方法准备播放
 * - 调用 start() 方法播放
 * <p>
 * Step 5: 切换清晰度
 * - 调用 selectTrack(trackInfo.getIndex()) 方法切换清晰度
 * <p>
 * Step 6: 资源清理
 * - 调用 stop() 停止播放
 * - 调用 release() 销毁播放器实例
 * - 清空相关引用，避免内存泄漏
 * <p>
 */
public class MultiResolutionActivity extends AppCompatActivity {

    private static final String TAG = "MultiResolutionActivity";

    // 播放器视图/ PlayerView
    private AliDisplayView mAliDisplayView;
    // 播放器 / Player
    private AliPlayer mAliPlayer;

    // 清晰度列表适配器 / multi-resolution Adapter
    private MultiResolutionAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_resolution);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_multi_resolution_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Step 1: 初始化播放器视图
        initView();

        // Step 2: 初始化播放器 && Step 3: 获取清晰度数据/设置清晰度切换监听
        setupPlayer();

        // Step 3: 设置播放源 & Step 4: 开始播放
        startupPlayer();
    }

    /**
     * Step 1:初始化UI
     */
    private void initView() {
        mAliDisplayView = findViewById(R.id.ali_display_view);
        RecyclerView mRecyclerView = findViewById(R.id.recycle_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new MultiResolutionAdapter();
        mRecyclerView.setAdapter(mAdapter);

        if (mAliPlayer == null) {
            return;
        }

        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                ToastUtils.showToastLong(errorInfo.getExtra());
            }
        });

        mAdapter.setOnItemClickListener(new MultiResolutionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position < 0) return;
                if (position != mAdapter.getSelectTrackInfoIndex()) {
                    TrackInfo trackInfo = mAdapter.getDataList().get(position);
                    mAdapter.selectTrackInfo(trackInfo.getIndex());
                    mAliPlayer.selectTrack(trackInfo.getIndex(), true);
                    Log.e(TAG, "[Step 4] selectTrack(trackInfo.getIndex())" + trackInfo.getVideoWidth() + "/" + trackInfo.getIndex());
                }
            }
        });
    }

    /**
     * Step 2: 初始化播放器 && Step 3: 获取清晰度数据/设置清晰度切换监听
     * 对播放器实例进行了配置，启用了循环播放、快切模式、配置了清晰度切换的监听
     * 监听 OnPrepare 获取 TrackInfo 筛选清晰度数据
     * 了解更多信息，请访问[播放器SDK 基础功能]
     * <a href="https://help.aliyun.com/zh/vod/developer-reference/basic-features-1">...</a>
     */
    private void setupPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        // 可选：推荐使用`播放器单点追查`功能，当使用阿里云播放器 SDK 播放视频发生异常时，可借助单点追查功能针对具体某个用户或某次播放会话的异常播放行为进行全链路追踪，以便您能快速诊断问题原因，可有效改善播放体验治理效率。
        // traceId 值由您自行定义，需为您的用户或用户设备的唯一标识符，例如传入您业务的 userid 或者 IMEI、IDFA 等您业务用户的设备 ID。
        // 传入 traceId 后，埋点日志上报功能开启，后续可以使用播放质量监控、单点追查和视频播放统计功能。
        // 文档：https://help.aliyun.com/zh/vod/developer-reference/single-point-tracing
        // mAliPlayer.setTraceId(traceId);
        mAliDisplayView.setPreferDisplayView(AliDisplayView.DisplayViewType.SurfaceView);
        mAliPlayer.setDisplayView(mAliDisplayView);

        // 设置循环播放
        mAliPlayer.setLoop(true);
        // 启用快切模式
        PlayerConfig config = mAliPlayer.getConfig();
        if (null != config) {
            // 启用 selectTrack 快切模式
            config.mSelectTrackBufferMode = 1;
            mAliPlayer.setConfig(config);
        }

        // 监听清晰度切换结果
        mAliPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {
                if (trackInfo.getType() == TrackInfo.Type.TYPE_VIDEO) {
                    Log.e(TAG, "[onChangedSuccess]" + trackInfo.getVideoWidth() + "/" + trackInfo.getIndex());
                    ToastUtils.showToastLong("切换清晰度:" + trackInfo.getVideoWidth() + "P");
                }
            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
                Log.e(TAG, "[onChangedFail]" + errorInfo.getCode().name() + ":" + errorInfo.getMsg());
                ToastUtils.showToastLong(errorInfo.getMsg());
            }
        });

        // 监听播放器prepare
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                if (null != mAliPlayer.getMediaInfo()) {
                    // 加载清晰度数据
                    filterResolution(mAliPlayer.getMediaInfo().getTrackInfos());
                }
            }
        });
    }

    /**
     * Step 2 设置播放源 && Step 4: 播放视频
     */
    private void startupPlayer() {
        // 创建播放源对象并设置播放地址
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(Constants.DataSource.MULTI_RESOLUTION_URL);
        mAliPlayer.setDataSource(urlSource);

        // 准备播放
        mAliPlayer.prepare();
        // 播放视频
        mAliPlayer.start();

        Log.d(TAG, "[Step 4] 开始播放视频: " + Constants.DataSource.MULTI_RESOLUTION_URL);
    }

    /**
     * Step 6: 释放播放器
     * Release the Player
     */
    private void cleanupPlayer() {
        if (null != mAliPlayer) {
            // 4.1 停止播放器  stop
            mAliPlayer.stop();

            // 4.2 释放播放器  release
            mAliPlayer.release();

            // (可选) 停止并销毁播放器, 与 4.1 和 4.2 步骤作用相同
            // (Optional) Stop and destroy the player; same as steps 4.1 and 4.2.
            // mAliPlayer.releaseAsync();

            // 4.3 清除引用,避免内存泄露哦 Nullify references to prevent memory leaks
            mAliPlayer = null;

            Log.d(TAG, "[Step 5] 播放器资源清理完成");
        }
    }

    /**
     * Step 3：筛选清晰度流,并添加进列表适配器
     *
     * @param data 整体的流数据
     */
    private void filterResolution(List<TrackInfo> data) {
        if (null != data && !data.isEmpty()) {
            List<TrackInfo> result = new ArrayList<>();
            for (TrackInfo info : data) {
                Log.e(TAG, "TrackInfoType:" + info.getType().name());
                if (info.getType() == TrackInfo.Type.TYPE_VIDEO) {
                    Log.e(TAG, "TrackInfo:" + info.getVideoWidth() + "/" + info.getVideoHeight());
                    result.add(info);
                }
            }
            mAdapter.setDataList(result);
            mAdapter.notifyItemRangeChanged(0, result.size());

            Log.d(TAG, "[Step 3] 筛选清晰度流: " + result.size());
        }
    }

    @Override
    protected void onDestroy() {
        // Step 4: 资源清理
        cleanupPlayer();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
