package com.alivc.player.settings.backstage.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.alivc.player.settings.backstage.R;

/**
 * @author junhuiYe
 * @date 2025/8/27
 * @brief 悬浮窗权限检查和请求工具类
 */
public class FloatingWindowPermissionHelper {
    private static final int REQUEST_CODE_FLOATING_WINDOW = 1001;

    /**
     * 检查是否有悬浮窗权限
     */
    public static boolean hasFloatingWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true; // Android 6.0以下默认有权限
    }

    /**
     * 请求悬浮窗权限
     */
    public static void requestFloatingWindowPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE_FLOATING_WINDOW);
            }
        }
    }

    /**
     * 显示权限说明对话框
     */
    public static void showPermissionDialog(Context context, OnPermissionDialogListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.player_pip_permission))
                .setMessage(context.getString(R.string.pip_permission_message))
                .setPositiveButton(context.getString(R.string.pip_permission_open), (dialog, which) -> {
                    if (listener != null) {
                        listener.onConfirm();
                    }
                })
                .setNegativeButton(context.getString(R.string.pip_permission_cancel), (dialog, which) -> {
                    if (listener != null) {
                        listener.onCancel();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public interface OnPermissionDialogListener {
        void onConfirm();
        void onCancel();
    }
}

