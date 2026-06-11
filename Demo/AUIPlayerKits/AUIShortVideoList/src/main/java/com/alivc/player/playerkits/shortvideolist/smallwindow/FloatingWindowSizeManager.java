package com.alivc.player.playerkits.shortvideolist.smallwindow;

import android.content.Context;
import android.util.DisplayMetrics;

import com.alivc.player.playerkits.shortvideolist.utils.SLog;

/**
 * 悬浮窗尺寸管理器
 * 负责计算和管理悬浮窗的尺寸
 */
public class FloatingWindowSizeManager {
    private Context mContext;
    // 默认尺寸
    private static final int DEFAULT_SHORT_SIZE = 540;
    private static final int DEFAULT_LONG_SIZE = 960;

    public FloatingWindowSizeManager(Context context) {
        mContext = context;
    }

    public WindowSize calculateWindowSize(int videoWidth, int videoHeight) {
        WindowSize windowSize;
        if (videoWidth <= 0 || videoHeight <= 0) {
            SLog.w(this, "Invalid video size: " + videoWidth + "x" + videoHeight + ", using default");
            return new WindowSize(DEFAULT_SHORT_SIZE, DEFAULT_LONG_SIZE);
        }

        // 计算宽高比
        float aspectRatio = (float) videoWidth / videoHeight;

        // 视频比例-> 9:16 | 16:9
        // 判断是竖屏还是横屏视频
        int screenWidth = getScreenWidth(mContext);
        int screenHeight = getScreenHeight(mContext);

        SLog.d(this, "Screen size: " + screenWidth + "x" + screenHeight);

        // === 强制输出宽高比为 16:9 ===
        float targetAspectRatio = 16.0f / 9.0f; // 16:9

        // 设定悬浮窗最大宽度（例如：屏幕宽度的 70%）
        int maxWidth = (int) (screenWidth * 0.7);
        int maxHeight = (int) (screenHeight * 0.7);

        // 初始宽度
        int windowWidth = maxWidth;
        int windowHeight = (int) (windowWidth / targetAspectRatio);

        // 如果高度超过最大限制，按高度反向计算
        if (windowHeight > maxHeight) {
            windowHeight = maxHeight;
            windowWidth = (int) (windowHeight * targetAspectRatio);
        }

        // 可选：设置最小尺寸，避免太小
        int minWidth = 320;  // 至少 320px 宽
        int minHeight = 180; // 至少 180px 高
        if (windowWidth < minWidth || windowHeight < minHeight) {
            windowWidth = minWidth;
            windowHeight = (int) (windowWidth / targetAspectRatio);
            if (windowHeight < minHeight) {
                windowHeight = minHeight;
            }
            windowWidth = (int) (windowHeight * targetAspectRatio);
        }

        if (aspectRatio < 1.0f) {
            // 竖屏视频 (高 > 宽)
            windowSize = new WindowSize(windowHeight, windowWidth);
        } else {
            // 横屏视频 (宽 > 高)
            windowSize = new WindowSize(windowWidth, windowHeight);
        }
        return windowSize;
    }

    // 获取屏幕宽度（像素）
    private int getScreenWidth(Context mContext) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    // 获取屏幕高度（像素）
    private int getScreenHeight(Context mContext) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static class WindowSize {
        public final int width;
        public final int height;

        public WindowSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "WindowSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            WindowSize that = (WindowSize) obj;
            return width == that.width && height == that.height;
        }
    }
}
