package com.aliyun.auiplayerapp;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alivc.player.settings.backstage.sp.SharedPrefBusinessManager;

public class PlayerApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        ARouter.openDebug();
        ARouter.openLog();
        ARouter.printStackTrace();
        ARouter.init(this);
        SharedPrefBusinessManager.init(this);
    }
}
