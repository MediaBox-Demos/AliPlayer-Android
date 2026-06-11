package com.alivc.player.settings.backstage;

import com.alivc.player.settings.backstage.sp.SharedPrefBusinessManager;
import com.alivc.player.playerkits.shortvideolist.AUIShortVideoListConstants;

/**
 * @author keria
 * @date 2024/9/13
 * @brief
 */
public class AUIBackstageSettings {
    private AUIBackstageSettings() {
    }

    public static void updateVideoListConfigurations() {
        AUIShortVideoListConstants.PLAYER_POOL_CAPACITY = SharedPrefBusinessManager.getPlayerPoolCapacity(2);
        AUIShortVideoListConstants.ENABLE_COVER_URL_STRATEGY = SharedPrefBusinessManager.isCoverFallbackStrategyEnabled(true);
    }
}
