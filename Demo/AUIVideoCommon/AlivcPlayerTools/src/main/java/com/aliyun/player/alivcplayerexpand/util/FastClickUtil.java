package com.aliyun.player.alivcplayerexpand.util;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * 限制快速点击多次触发的工具类
 *
 * 注意：如果第一次点击涉及到阻塞主线程/主线程耗时的情况则FastClickUtil的判断并不靠谱
 */
/****
 * Tool class to limit multiple triggers for quick clicks
 *
 * Note: FastClickUtil's judgment is not reliable if the first click involves blocking the main thread / main thread timing out.
 */
public class FastClickUtil {


    private static final String TAG = FastClickUtil.class.getSimpleName();
    /**
     * 两次点击间隔不能少于300ms
     */
    /****
     * The interval between two clicks cannot be less than 300ms.
     */
    private static final int MIN_DELAY_TIME = 500;

    /**
     * activity两次点击间隔不能少于800ms
     */
    /****
     * Activity two clicks interval cannot be less than 800ms.
     */
    private static final int MIN_DELAY_TIME_ACTIVITY = 800;
    private static long sLastClickTime;
    private static String sLastActivitySimpleName;

    public static boolean isFastClick() {
        long currentClickTime = System.currentTimeMillis();
        boolean isFastClick = (currentClickTime - sLastClickTime) <= MIN_DELAY_TIME;
        Log.e(TAG, "log_common_FastClickUtil : " + (currentClickTime - sLastClickTime));
        sLastClickTime = currentClickTime;
        return isFastClick;
    }

    /**
     * fix连续点击弹出多个activity
     * 1.设置android:launchMode="singleTop"在这个场景下并不管用
     * 2.部分手机可能因为activity的阻塞耗时不同会导致计算的间隔超出500ms，这里定位800毫秒能处理绝大部分的手机和情况
     * 3.通过记录activitySimpleName，避免出现用户熟练连续进入多个页面的时候需要等待
     *
     * @param activitySimpleName Activity.class.getSimpleName()
     * @return boolean
     */
    /****
     * fix consecutive clicks to pop up multiple activities
     * 1. Setting android:launchMode="singleTop" does not work in this scenario
     * 2. Some phones may have different blocking times for activities, causing the interval to exceed 500ms. Here we locate 800 milliseconds can handle the majority of phones and situations
     * 3. Through recording activitySimpleName, avoid the situation where users need to wait for the user to be familiar with consecutive entry of multiple pages
     *
     * @param activitySimpleName Activity.class.getSimpleName()
     * @return boolean
     */
    public static boolean isFastClickActivity(@NonNull String activitySimpleName) {

        long currentClickTime = System.currentTimeMillis();
        boolean isFastClick = (currentClickTime - sLastClickTime) <= MIN_DELAY_TIME_ACTIVITY;
        sLastClickTime = currentClickTime;
        if (!activitySimpleName.equals(sLastActivitySimpleName)) {
            //如果两次的activity不是同一个，不是快速点击
            //If the two activities are not the same, it's not a quick click
            isFastClick = false;
            sLastActivitySimpleName = activitySimpleName;
        }
        return isFastClick;
    }

}
