/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.lzf.easyfloat.permission.rom;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


import com.lzf.easyfloat.R;
import com.lzf.easyfloat.permission.FloatViewPermissionUtils;

import java.lang.reflect.Method;

public class HuaweiUtils {
    private static final String TAG = "HuaweiUtils";

    /**
     * 检测 Huawei 悬浮窗权限 Detect Huawei Hover Window Permissions
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    /**
     * 去华为权限申请页面 Apply for Huawei Permissions Page
     */
    public static void applyPermission(Fragment fragment) {
        try {
            Intent intent = new Intent();
            //华为权限管理，跳转到指定app的权限管理位置需要华为接口权限，未解决
            // Huawei permission management, jump to the specified app's permission management location requires Huawei interface permissions, unresolved
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面 Floating Window Management Page
            intent.setComponent(comp);
            if (RomUtils.getEmuiVersion() == 3.1) {
                //emui 3.1 的适配
                //Adaptation of emui 3.1
                fragment.startActivityForResult(intent, FloatViewPermissionUtils.requestCode);
            } else {
                //emui 3.0 的适配
                //Adaptation of emui 3.0
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");//悬浮窗管理页面 Floating Window Management Page
                intent.setComponent(comp);
                fragment.startActivityForResult(intent, FloatViewPermissionUtils.requestCode);
            }
        } catch (SecurityException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //华为权限管理
            //HUaWEI permission management
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");
            //华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            // Huawei permission management, jump to this app's permission management page, this needs Huawei interface permissions, unresolved
            // 悬浮窗管理页面
            // Floating Window Management Page
            intent.setComponent(comp);
            fragment.startActivityForResult(intent, FloatViewPermissionUtils.requestCode);
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (ActivityNotFoundException e) {
            /**
             * 手机管家版本较低 HUAWEI SC-UL10
             */
            /****
             * Lower version of Phone Manager HUAWEI SC-UL10
             */
            Intent intent = new Intent();
            //权限管理页面 android4.4
            //Permission management page    android4.4
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");
            //此处可跳转到指定app对应的权限管理页面，但是需要相关权限，未解决
            //This can jump to the specified app corresponding permission management page, but needs related permissions, unresolved
            intent.setComponent(comp);
            fragment.startActivityForResult(intent, FloatViewPermissionUtils.requestCode);
            e.printStackTrace();
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (Exception e) {
            //抛出异常时提示信息
            //Throw an exception when prompting information
            Toast.makeText(fragment.getActivity(), R.string.failed_setup_page, Toast.LENGTH_LONG).show();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }
}


