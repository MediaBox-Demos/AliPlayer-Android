package com.lzf.easyfloat.interfaces

import android.view.MotionEvent
import android.view.View

/**
 * @author: liuzhenfeng
 * @function: 浮窗的一些状态回调 Some state callbacks for floating windows
 * @date: 2019-07-16  14:11
 */
interface OnFloatCallbacks {

    /**
     * 浮窗的创建结果，是否创建成功 The result of creating a floating window, whether it was created successfully or not
     *
     * @param isCreated     是否创建成功 Whether it was created successfully or not
     * @param msg           失败返回的结果 Failure return result
     * @param view          浮窗xml布局 Floating window xml layout
     */
    fun createdResult(isCreated: Boolean, msg: String?, view: View?)

    fun show(view: View)

    fun hide(view: View)

    fun dismiss()

    /**
     * 触摸事件的回调
     */
    /****
     * Callbacks for touch events
     */
    fun touchEvent(view: View, event: MotionEvent)

    /**
     * 浮窗被拖拽时的回调，坐标为浮窗的左上角坐标
     */
    /****
     * Callbacks for dragging a floating window, the coordinates are the coordinates of the upper left corner of the floating window
     */
    fun drag(view: View, event: MotionEvent)

    /**
     * 拖拽结束时的回调，坐标为浮窗的左上角坐标
     */
    /****
     * Callback at the end of the drag and drop, with the coordinates of the upper-left corner of the floating window
     */
    fun dragEnd(view: View)

}