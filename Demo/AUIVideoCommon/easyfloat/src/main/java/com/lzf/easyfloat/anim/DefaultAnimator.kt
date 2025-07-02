package com.lzf.easyfloat.anim

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnFloatAnimator
import com.lzf.easyfloat.utils.DisplayUtils
import kotlin.math.min

/**
 * @author: liuzhenfeng
 * @function: 系统浮窗的默认效果，选择靠近左右侧的一边进行出入
 * @date: 2019-07-22  17:22
 */
/****
 * @author: liuzhenfeng
 * @function: System floating window default effect, choose the side closest to the left or right to enter and exit
 * @date: 2019-07-22  17:22
 */
open class DefaultAnimator : OnFloatAnimator {

    override fun enterAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? = getAnimator(view, params, windowManager, sidePattern, false)

    override fun exitAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? = getAnimator(view, params, windowManager, sidePattern, true)

    private fun getAnimator(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern,
        isExit: Boolean
    ): Animator {
        val triple = initValue(view, params, windowManager, sidePattern)
        // 退出动画的起始值、终点值，与入场动画相反
        // The start and end values of the exit animation are the opposite of the entry animation.
        val start = if (isExit) triple.second else triple.first
        val end = if (isExit) triple.first else triple.second
        return ValueAnimator.ofInt(start, end).apply {
            addUpdateListener {
                try {
                    val value = it.animatedValue as Int
                    if (triple.third) params.x = value else params.y = value
                    // 动画执行过程中页面关闭，出现异常
                    // An exception occurs during the animation process when the page is closed
                    windowManager.updateViewLayout(view, params)
                } catch (e: Exception) {
                    cancel()
                }
            }
        }
    }

    /**
     * 计算边距，起始坐标等
     */
    /****
     * Calculate the margin, starting coordinates, etc.
     */
    private fun initValue(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Triple<Int, Int, Boolean> {
        val parentRect = Rect()
        windowManager.defaultDisplay.getRectSize(parentRect)
        // 浮窗各边到窗口边框的距离
        // The distance from the edge of the window to the edge of the float
        val leftDistance = params.x
        val rightDistance = parentRect.right - (leftDistance + view.right)
        val topDistance = params.y
        val bottomDistance = parentRect.bottom - (topDistance + view.bottom)
        // 水平、垂直方向的距离最小值
        // The minimum distance in the horizontal and vertical directions
        val minX = min(leftDistance, rightDistance)
        val minY = min(topDistance, bottomDistance)

        val isHorizontal: Boolean
        val endValue: Int
        val startValue: Int = when (sidePattern) {
            SidePattern.LEFT, SidePattern.RESULT_LEFT -> {
                // 从左侧到目标位置，右移
                // From the left to the target position, right
                isHorizontal = true
                endValue = params.x
                -view.right
            }
            SidePattern.RIGHT, SidePattern.RESULT_RIGHT -> {
                // 从右侧到目标位置，左移
                // From the right to the target position, left
                isHorizontal = true
                endValue = params.x
                parentRect.right
            }
            SidePattern.TOP, SidePattern.RESULT_TOP -> {
                // 从顶部到目标位置，下移
                // From the top to the target position, down
                isHorizontal = false
                endValue = params.y
                -view.bottom
            }
            SidePattern.BOTTOM, SidePattern.RESULT_BOTTOM -> {
                // 从底部到目标位置，上移
                // From the bottom to the target position, up
                isHorizontal = false
                endValue = params.y
                parentRect.bottom + getCompensationHeight(view, params)
            }

            SidePattern.DEFAULT, SidePattern.AUTO_HORIZONTAL, SidePattern.RESULT_HORIZONTAL -> {
                // 水平位移，哪边距离屏幕近，从哪侧移动
                // Horizontal movement, which side is closer to the screen, from which side to move
                isHorizontal = true
                endValue = params.x
                if (leftDistance < rightDistance) -view.right else parentRect.right
            }
            SidePattern.AUTO_VERTICAL, SidePattern.RESULT_VERTICAL -> {
                // 垂直位移，哪边距离屏幕近，从哪侧移动
                // Vertical movement, which side is closer to the screen, from which side to move
                isHorizontal = false
                endValue = params.y
                if (topDistance < bottomDistance) -view.bottom
                else parentRect.bottom + getCompensationHeight(view, params)
            }

            else -> if (minX <= minY) {
                isHorizontal = true
                endValue = params.x
                if (leftDistance < rightDistance) -view.right else parentRect.right
            } else {
                isHorizontal = false
                endValue = params.y
                if (topDistance < bottomDistance) -view.bottom
                else parentRect.bottom + getCompensationHeight(view, params)
            }
        }
        return Triple(startValue, endValue, isHorizontal)
    }

    /**
     * 单页面浮窗（popupWindow），坐标从顶部计算，需要加上状态栏的高度
     */
    /****
     * Single-page floating window (popupWindow), coordinates are calculated from the top, and the height of the status bar needs to be added when calculating the bottom animation.
     */
    private fun getCompensationHeight(view: View, params: WindowManager.LayoutParams): Int {
        val location = IntArray(2)
        // 获取在整个屏幕内的绝对坐标
        // Get the absolute coordinates in the entire screen
        view.getLocationOnScreen(location)
        // 绝对高度和相对高度相等，说明是单页面浮窗（popupWindow），计算底部动画时需要加上状态栏高度
        // The absolute height and the relative height are equal, which means that it is a single-page floating window (popupWindow). When calculating the bottom animation, the height of the status bar needs to be added.
        return if (location[1] == params.y) DisplayUtils.statusBarHeight(view) else 0
    }

}