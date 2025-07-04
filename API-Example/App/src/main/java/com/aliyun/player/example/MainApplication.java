package com.aliyun.player.example;

import android.app.Application;
import android.content.Context;

import com.aliyun.player.AliPlayerGlobalSettings;

/**
 * @author keria
 * @date 2025/7/4
 * @brief 自定义 Application 类，用于全局初始化
 */
public class MainApplication extends Application {
    /**
     * 业务来源信息
     */
    private static final String EXTRA_DATA_API_EXAMPLE = "{\"scene\":\"api-example\",\"platform\":\"android\",\"style\":\"java\"}";

    /**
     * attachBaseContext 可用于多语言、插件化等特殊场景的初始化
     * 这里保留父类实现，如需扩展可在此处添加
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 可在此处添加多语言或热修复等初始化代码
    }

    /**
     * 应用启动时调用，适合做全局初始化工作
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // 设置业务来源信息
        AliPlayerGlobalSettings.setOption(AliPlayerGlobalSettings.SET_EXTRA_DATA, EXTRA_DATA_API_EXAMPLE);

        // 可选：如有其他全局初始化操作，请在此处添加
    }
}
