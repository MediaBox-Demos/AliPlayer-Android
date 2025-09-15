package com.aliyun.player.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.AnyThread;
import androidx.annotation.StringRes;

import com.aliyun.player.common.BuildConfig;

/**
 * @author keria
 * @date 2025/9/15
 * @brief Toast 工具类
 * 支持普通提示、长提示、Debug 专用提示，内部自动获取上下文，无需外部传参。
 */
public class ToastUtils {
    private static final String TAG = "ToastUtils";

    // 全局静态 Handler，绑定主线程 Looper，只创建一次
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    private ToastUtils() {
        throw new AssertionError("No instances.");
    }

    /**
     * 显示短时 Toast（默认 Duration: SHORT）
     */
    @AnyThread
    public static void showToast(String toast) {
        showToast(toast, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时 Toast（通过资源 ID）
     */
    @AnyThread
    public static void showToast(@StringRes int resId) {
        String message = getString(resId);
        if (!TextUtils.isEmpty(message)) {
            showToast(message, Toast.LENGTH_SHORT);
        }
    }

    /**
     * 显示长时 Toast
     */
    @AnyThread
    public static void showToastLong(String toast) {
        showToast(toast, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时 Toast（通过资源 ID）
     */
    @AnyThread
    public static void showToastLong(@StringRes int resId) {
        String message = getString(resId);
        if (!TextUtils.isEmpty(message)) {
            showToast(message, Toast.LENGTH_LONG);
        }
    }

    /**
     * Debug 专用：显示短时 Toast，仅在 Debug 环境下生效，带标记前缀
     */
    @AnyThread
    public static void showDebugToast(String toast) {
        if (isDebugToastAllowed() && !TextUtils.isEmpty(toast)) {
            showToast("[仅Debug展示]: " + toast, Toast.LENGTH_SHORT);
        }
    }

    /**
     * Debug 专用：显示长时 Toast，仅在 Debug 环境下生效，带标记前缀
     */
    @AnyThread
    public static void showDebugToastLong(String toast) {
        if (isDebugToastAllowed() && !TextUtils.isEmpty(toast)) {
            showToast("[仅Debug展示]: " + toast, Toast.LENGTH_LONG);
        }
    }

    /**
     * 核心方法：显示 Toast（支持自定义时长）
     * 所有其他方法最终调用此方法
     */
    @AnyThread
    public static void showToast(final String toast, final int duration) {
        if (TextUtils.isEmpty(toast)) {
            return;
        }

        final Context context = ContextUtils.getSafeToastContext();
        if (context == null) {
            Log.w(TAG, "Cannot show toast: context is null");
            return;
        }

        Log.i(TAG, "showToast: " + toast);

        sMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, toast, duration).show();
            }
        });
    }

    /**
     * 从资源中获取字符串，若失败则返回 null 并记录警告
     */
    private static String getString(@StringRes int resId) {
        Context context = ContextUtils.getSafeToastContext();
        if (context == null) {
            Log.w(TAG, "getString failed: context is null, resId=" + resId);
            return null;
        }

        try {
            return context.getString(resId);
        } catch (Exception e) {
            Log.e(TAG, "GetString resource failed: id=" + resId, e);
            return null;
        }
    }

    /**
     * 控制 Debug Toast 是否启用
     */
    private static boolean isDebugToastAllowed() {
        return BuildConfig.DEBUG;
    }
}
