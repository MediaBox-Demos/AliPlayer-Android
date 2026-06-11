package com.alivc.player.settings.backstage.sp;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 业务SharedPreference
 */
public class SharedPrefBusinessManager {

    private static Context sContext;

    // 封面图兜底策略
    private static final String KEY_COVER_FALLBACK_STRATEGY = "cover_fallback_strategy";
    private static final String KEY_PLAYER_POOL_CAPACITY = "player_pool_capacity";
    private static final String KEY_SELECTED_PLAYLIST_INFO = "selected_playlist_info";

    public static void init(Context context) {
        sContext = context;
    }

    public static void setCoverFallbackStrategy(boolean enable) {
        SharedPrefUtils.saveData(sContext, KEY_COVER_FALLBACK_STRATEGY, enable);
    }

    public static boolean isCoverFallbackStrategyEnabled(boolean defaultValue) {
        return SharedPrefUtils.getBooleanData(sContext, KEY_COVER_FALLBACK_STRATEGY, defaultValue);
    }

    public static void setPlayerPoolCapacity(int capacity) {
        SharedPrefUtils.saveData(sContext, KEY_PLAYER_POOL_CAPACITY, capacity);
    }

    public static int getPlayerPoolCapacity(int defaultValue) {
        return SharedPrefUtils.getIntData(sContext, KEY_PLAYER_POOL_CAPACITY, defaultValue);
    }

    public static void setSelectedPlayListInfoListUrl(String url) {
        SharedPrefUtils.saveData(sContext, KEY_SELECTED_PLAYLIST_INFO, url);
    }

    public static String getSelectedPlayListInfoListUrl() {
        return SharedPrefUtils.getStringData(sContext, KEY_SELECTED_PLAYLIST_INFO, "");
    }

    public static void clear(@NonNull Context context) {
        SharedPrefUtils.clear(context);
    }
}
