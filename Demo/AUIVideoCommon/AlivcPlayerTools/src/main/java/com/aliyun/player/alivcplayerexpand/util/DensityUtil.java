/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.player.alivcplayerexpand.util;

import android.content.Context;

public class DensityUtil {

    public static int dip2px(Context paramContext, float paramFloat) {
        return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
    }

    public static int px2dip(Context paramContext, float paramFloat) {
        return (int)(0.5F + paramFloat / paramContext.getResources().getDisplayMetrics().density);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    /****
     * Convert sp values to px values, ensuring that the text size remains the same
     *
     * @param spValue
     * (property scaledDensity in DisplayMetrics class)
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
