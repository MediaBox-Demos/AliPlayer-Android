package com.lzf.easyfloat.enums

/**
 * @author: liuzhenfeng
 * @function: 浮窗显示类别 Floating window display category
 * @date: 2019-07-08  17:05
 */
enum class ShowPattern {

    // 只在当前Activity显示、仅应用前台时显示、仅应用后台时显示，一直显示（不分前后台）
    // Show only in the current Activity, show only when the app is in the foreground, show only when the app is in the background, show all the time (regardless of foreground and background)
    CURRENT_ACTIVITY, FOREGROUND, BACKGROUND, ALL_TIME
}