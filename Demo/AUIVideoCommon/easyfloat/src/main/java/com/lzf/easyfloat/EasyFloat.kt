package com.lzf.easyfloat

import android.app.Activity
import android.content.Context
import android.view.View
import com.lzf.easyfloat.core.FloatingWindowManager
import com.lzf.easyfloat.data.FloatConfig
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.*
import com.lzf.easyfloat.interfaces.OnPermissionResult
import com.lzf.easyfloat.permission.FloatViewPermissionUtils
import com.lzf.easyfloat.utils.LifecycleUtils
import com.lzf.easyfloat.interfaces.FloatCallbacks
import com.lzf.easyfloat.utils.DisplayUtils
import com.lzf.easyfloat.utils.Logger
import java.lang.Exception

/**
 * @author: liuzhenfeng
 * @github：https://github.com/princekin-f
 * @function: 悬浮窗使用工具类  Floating window use utils class
 * @date: 2019-06-27  15:22
 */
class EasyFloat {

    companion object {

        /**
         * 通过上下文，创建浮窗的构建者信息，使浮窗拥有一些默认属性
         * @param activity 上下文信息，优先使用Activity上下文，因为系统浮窗权限的自动申请，需要使用Activity信息
         * @return 浮窗属性构建者
         */
        /****
         * Through the context, create the builder information of the floating window, so that the floating window has some default properties
         * @param activity context information, prioritize the use of Activity context, because the automatic application of the system floating window permissions, need to use the Activity information
         * @return Floating window attribute builder
         */
        @JvmStatic
        fun with(activity: Context): Builder = if (activity is Activity) Builder(activity)
        else Builder(LifecycleUtils.getTopActivity() ?: activity)

        /**
         * 关闭当前浮窗
         * @param tag 浮窗标签
         * @param force 立即关闭，有退出动画也不执行
         */
        /****
         * Close the current floating window
         * @param tag Floating window tag
         * @param force Immediately close, without animation
         */
        @JvmStatic
        @JvmOverloads
        fun dismiss(tag: String? = null, force: Boolean = false) =
            FloatingWindowManager.dismiss(tag, force)

        /**
         * 隐藏当前浮窗
         * @param tag 浮窗标签
         */
        /****
         * Hide the current floating window
         * @param tag Floating window tag
         */
        @JvmStatic
        @JvmOverloads
        fun hide(tag: String? = null) = FloatingWindowManager.visible(false, tag, false)

        /**
         * 设置当前浮窗可见
         * @param tag 浮窗标签
         */
        /****
         * Set the current floating window visible
         * @param tag Floating window tag
         */
        @JvmStatic
        @JvmOverloads
        fun show(tag: String? = null) = FloatingWindowManager.visible(true, tag, true)

        /**
         * 设置当前浮窗是否可拖拽，先获取浮窗的config，后修改相应属性
         * @param dragEnable 是否可拖拽
         * @param tag 浮窗标签
         */
        /****
         * Set the current floating window whether it can be dragged, first get the floating window config, and then modify the corresponding property
         * @param dragEnable Whether it can be dragged
         * @param tag Floating window tag
         */
        @JvmStatic
        @JvmOverloads
        fun dragEnable(dragEnable: Boolean, tag: String? = null) =
            getConfig(tag)?.let { it.dragEnable = dragEnable }

        /**
         * 获取当前浮窗是否显示，通过浮窗的config，获取显示状态
         * @param tag 浮窗标签
         * @return 当前浮窗是否显示
         */
        /****
         * Get the current floating window is displayed, through the floating window config, get the display status
         * @param tag Floating window tag
         * @return The current floating window is displayed
         */
        @JvmStatic
        @JvmOverloads
        fun isShow(tag: String? = null) = getConfig(tag)?.isShow ?: false

        /**
         * 获取当前浮窗中，我们传入的View
         * @param tag 浮窗标签
         */
        /****
         * Get the current floating window in which we pass in the View
         * @param tag Floating window tag
         */
        @JvmStatic
        @JvmOverloads
        fun getFloatView(tag: String? = null): View? = getConfig(tag)?.layoutView

        /**
         * 更新浮窗坐标，未指定坐标执行吸附动画
         * @param tag 浮窗标签
         * @param x 更新后的X轴坐标
         * @param y 更新后的Y轴坐标
         */
        /****
         * Update the floating window coordinates, without specifying coordinates, execute the attaching animation
         * @param tag Floating window tag
         * @param x Updated X axis coordinate
         * @param y Updated Y axis coordinate
         */
        @JvmStatic
        @JvmOverloads
        fun updateFloat(tag: String? = null, x: Int = -1, y: Int = -1) =
            FloatingWindowManager.getHelper(tag)?.updateFloat(x, y)

        // 以下几个方法为：系统浮窗过滤页面的添加、移除、清空
        // The following methods are used to add, remove, and clear the system's floating window filter page.
        /**
         * 为当前浮窗过滤，设置需要过滤的Activity
         * @param activity 需要过滤的Activity
         * @param tag 浮窗标签
         */
        /****
         * For the current floating window, set the Activity that needs to be filtered
         * @param activity Activity to be filtered
         * @param tag Floating window tag
         */
        @JvmStatic
        @JvmOverloads
        fun filterActivity(activity: Activity, tag: String? = null) =
            getFilterSet(tag)?.add(activity.componentName.className)

        /**
         * 为当前浮窗，设置需要过滤的Activity类名（一个或者多个）
         * @param tag 浮窗标签
         * @param clazz 需要过滤的Activity类名，一个或者多个
         */
        /****
         * For the current floating window, set the Activity class name (one or more) that needs to be filtered
         * @param tag Floating window tag
         * @param clazz Activity class name to be filtered, one or more
         */
        @JvmStatic
        @JvmOverloads
        fun filterActivities(tag: String? = null, vararg clazz: Class<*>) =
            getFilterSet(tag)?.addAll(clazz.map { it.name })

        /**
         * 为当前浮窗，移除需要过滤的Activity
         * @param activity 需要移除过滤的Activity
         * @param tag 浮窗标签
         */
        /****
         * For the current floating window, remove the Activity that needs to be filtered
         * @param activity Activity to be removed from filtering
         * @param tag Floating window tag
         */
        @JvmStatic
        @JvmOverloads
        fun removeFilter(activity: Activity, tag: String? = null) =
            getFilterSet(tag)?.remove(activity.componentName.className)

        /**
         * 为当前浮窗，移除需要过滤的Activity类名（一个或者多个）
         * @param tag 浮窗标签
         * @param clazz 需要移除过滤的Activity类名，一个或者多个
         */
        /****
         * For the current floating window, remove the Activity class name (one or more) that needs to be filtered
         * @param tag Floating window tag
         * @param clazz Activity class name to be removed from filtering, one or more
         */
        @JvmStatic
        @JvmOverloads
        fun removeFilters(tag: String? = null, vararg clazz: Class<*>) =
            getFilterSet(tag)?.removeAll(clazz.map { it.name })

        /**
         * 清除当前浮窗的所有过滤信息
         * @param tag 浮窗标签
         */
        /****
         * Clear all filtering information of the current floating window
         * @param tag Floating window tag
         */
        @JvmStatic
        @JvmOverloads
        fun clearFilters(tag: String? = null) = getFilterSet(tag)?.clear()

        /**
         * 获取当前浮窗的config
         * @param tag 浮窗标签
         */
        /****
         * Get the current floating window's config
         * @param tag Floating window tag
         */
        private fun getConfig(tag: String?) = FloatingWindowManager.getHelper(tag)?.config

        /**
         * 获取当前浮窗的过滤集合
         * @param tag 浮窗标签
         */
        /****
         * Get the current floating window's filter set
         * @param tag Floating window tag
         */
        private fun getFilterSet(tag: String?) = getConfig(tag)?.filterSet
    }


    /**
     * 浮窗的属性构建类，支持链式调用
     */
    /****
     * Floating window attribute builder, support chained calls
     */
    class Builder(private val activity: Context) : OnPermissionResult {

        // 创建浮窗数据类，方便管理配置
        // Create the floating window data class to facilitate management of configuration
        private val config = FloatConfig()

        /**
         * 设置浮窗的吸附模式
         * @param sidePattern 浮窗吸附模式
         */
        /****
         * Set the floating window attaching mode
         * @param sidePattern Floating window attaching mode
         */
        fun setSidePattern(sidePattern: SidePattern) = apply { config.sidePattern = sidePattern }

        /**
         * 设置浮窗的显示模式
         * @param showPattern 浮窗显示模式
         */
        /****
         * Set the floating window display mode
         * @param showPattern Floating window display mode
         */
        fun setShowPattern(showPattern: ShowPattern) = apply { config.showPattern = showPattern }

        /**
         * 设置浮窗的布局文件，以及布局的操作接口
         * @param layoutId 布局文件的资源Id
         * @param invokeView 布局文件的操作接口
         */
        /****
         * Set the floating window layout file, and the layout operation interface
         * @param layoutId Layout file resource ID
         * @param invokeView Layout file operation interface
         */
        @JvmOverloads
        fun setLayout(layoutId: Int, invokeView: OnInvokeView? = null) = apply {
            config.layoutId = layoutId
            config.invokeView = invokeView
        }

        /**
         * 设置浮窗的对齐方式，以及偏移量
         * @param gravity 对齐方式
         * @param offsetX 目标坐标的水平偏移量
         * @param offsetY 目标坐标的竖直偏移量
         */
        /****
         * Set the floating window alignment, and offset
         * @param gravity Alignment
         * @param offsetX Target coordinate horizontal offset
         * @param offsetY Target coordinate vertical offset
         */
        @JvmOverloads
        fun setGravity(gravity: Int, offsetX: Int = 0, offsetY: Int = 0) = apply {
            config.gravity = gravity
            config.offsetPair = Pair(offsetX, offsetY)
        }

        /**
         * 设置浮窗的起始坐标，优先级高于setGravity
         * @param x 起始水平坐标
         * @param y 起始竖直坐标
         */
        /****
         * Set the floating window starting coordinates, priority is higher than setGravity
         * @param x Starting horizontal coordinate
         * @param y Starting vertical coordinate
         */
        fun setLocation(x: Int, y: Int) = apply { config.locationPair = Pair(x, y) }

        /**
         * 设置浮窗的拖拽边距值
         * @param left 浮窗左侧边距
         * @param top 浮窗顶部边距
         * @param right 浮窗右侧边距
         * @param bottom 浮窗底部边距
         */
        /****
         * Set the floating window drag margin value
         * @param left Floating window left margin
         * @param top Floating window top margin
         * @param right Floating window right margin
         * @param bottom Floating window bottom margin
         */
        @JvmOverloads
        fun setBorder(
            left: Int = 0,
            top: Int = -DisplayUtils.getStatusBarHeight(activity),
            right: Int = DisplayUtils.getScreenWidth(activity),
            bottom: Int = DisplayUtils.getScreenHeight(activity)
        ) = apply {
            config.leftBorder = left
            config.topBorder = top
            config.rightBorder = right
            config.bottomBorder = bottom
        }

        /**
         * 设置浮窗的标签：只有一个浮窗时，可以不设置；
         * 有多个浮窗必须设置不容的浮窗，不然没法管理，所以禁止创建相同标签的浮窗
         * @param floatTag 浮窗标签
         */
        /****
         * Set the floating window tag: only one floating window is set when there is only one floating window;
         * Multiple floating windows must be set to prevent management, so the creation of the same tag floating window is prohibited
         * @param floatTag Floating window tag
         */
        fun setTag(floatTag: String?) = apply { config.floatTag = floatTag }

        /**
         * 设置浮窗是否可拖拽
         * @param dragEnable 是否可拖拽
         */
        /****
         * Set whether the floating window can be dragged
         * @param dragEnable Whether it can be dragged
         */
        fun setDragEnable(dragEnable: Boolean) = apply { config.dragEnable = dragEnable }

        /**
         * 设置浮窗是否状态栏沉浸
         * @param immersionStatusBar 是否状态栏沉浸
         */
        /****
         * Set whether the floating window is immersion status bar
         * @param immersionStatusBar Whether the status bar is immersed
         */
        fun setImmersionStatusBar(immersionStatusBar: Boolean) =
            apply { config.immersionStatusBar = immersionStatusBar }

        /**
         * 浮窗是否包含EditText，浮窗默认不获取焦点，无法弹起软键盘，所以需要适配
         * @param hasEditText 是否包含EditText
         */
        /****
         * Set whether the floating window contains EditText, the floating window by default does not get focus,
         * and cannot pop up the soft keyboard, so it needs to be adapted
         * @param hasEditText Whether it contains EditText
         */
        fun hasEditText(hasEditText: Boolean) = apply { config.hasEditText = hasEditText }

        /**
         * 通过传统接口，进行浮窗的各种状态回调
         * @param callbacks 浮窗的各种事件回调
         */
        /****
         * Through the traditional interface, the various event callbacks of the floating window are set
         * @param callbacks Floating window event callbacks
         */
        fun registerCallbacks(callbacks: OnFloatCallbacks) = apply { config.callbacks = callbacks }

        /**
         * 针对kotlin 用户，传入带FloatCallbacks.Builder 返回值的 lambda，可按需回调
         * 为了避免方法重载时 出现编译错误的情况，更改了方法名
         * @param builder 事件回调的构建者
         */
        /****
         * For kotlin users, pass in a lambda with a FloatCallbacks.Builder return value, which can be called by demand
         * To avoid the situation of compilation errors when method overloading occurs, the method name has been changed
         * @param builder Event callback builder
         */
        fun registerCallback(builder: FloatCallbacks.Builder.() -> Unit) =
            apply { config.floatCallbacks = FloatCallbacks().apply { registerListener(builder) } }

        /**
         * 设置浮窗的出入动画
         * @param floatAnimator 浮窗的出入动画，为空时不执行动画
         */
        /****
         * Set the floating window entrance and exit animation
         * @param floatAnimator Floating window entrance and exit animation, empty is not executed
         */
        fun setAnimator(floatAnimator: OnFloatAnimator?) =
            apply { config.floatAnimator = floatAnimator }

        /**
         * 设置屏幕的有效显示高度（不包含虚拟导航栏的高度）
         * @param displayHeight 屏幕的有效高度
         */
        /****
         * Set the screen effective display height (not including the height of the virtual navigation bar)
         * @param displayHeight Screen effective height
         */
        fun setDisplayHeight(displayHeight: OnDisplayHeight) =
            apply { config.displayHeight = displayHeight }

        /**
         * 设置浮窗宽高是否充满屏幕
         * @param widthMatch 宽度是否充满屏幕
         * @param heightMatch 高度是否充满屏幕
         */
        /****
         * Set whether the floating window width and height are filled out of the screen
         * @param widthMatch Whether the width is filled out of the screen
         * @param heightMatch Whether the height is filled out of the screen
         */
        fun setMatchParent(widthMatch: Boolean = false, heightMatch: Boolean = false) = apply {
            config.widthMatch = widthMatch
            config.heightMatch = heightMatch
        }

        /**
         * 设置需要过滤的Activity类名，仅对系统浮窗有效
         * @param clazz 需要过滤的Activity类名
         */
        /****
         * Set the class name of the Activity to be filtered, only valid for system floating windows
         * @param clazz Class name of the Activity to be filtered
         */
        fun setFilter(vararg clazz: Class<*>) = apply {
            clazz.forEach {
                config.filterSet.add(it.name)
                if (activity is Activity) {
                    // 过滤掉当前Activity
                    // Filter out the current Activity
                    if (it.name == activity.componentName.className) config.filterSelf = true
                }
            }
        }

        /**
         * 创建浮窗，包括Activity浮窗和系统浮窗，如若系统浮窗无权限，先进行权限申请
         */
        /****
         * Create the floating window, including activity floating windows and system floating windows,
         * if the system floating window has no permission, first apply for permission approval
         */
        fun show() = when {
            // 未设置浮窗布局文件，不予创建
            // No layout file is set, do not create
            config.layoutId == null -> callbackCreateFailed(WARN_NO_LAYOUT)
            // 仅当页显示，则直接创建activity浮窗
            // Only when the page is displayed, then directly create the activity floating window
            config.showPattern == ShowPattern.CURRENT_ACTIVITY -> createFloat()
            // 系统浮窗需要先进行权限审核，有权限则创建app浮窗
            // System floating windows need to first go through permission audit, and create the app floating window
            FloatViewPermissionUtils.checkPermission(activity) -> createFloat()
            // 申请浮窗权限
            // Apply for floating window permission
            else -> requestPermission()
        }

        /**
         * 通过浮窗管理类，统一创建浮窗
         */
        /****
         * Through the floating window management class, create the floating window
         */
        private fun createFloat() = FloatingWindowManager.create(activity, config)

        /**
         * 通过Fragment去申请系统悬浮窗权限
         */
        /****
         * Through Fragment to apply for system floating window permission
         */
        private fun requestPermission() =
            if (activity is Activity) FloatViewPermissionUtils.requestPermission(activity, this)
            else callbackCreateFailed(WARN_CONTEXT_REQUEST)

        /**
         * 申请浮窗权限的结果回调
         * @param isOpen 悬浮窗权限是否打开
         */
        /****
         * The callback result of applying for floating window permission
         * @param isOpen Whether the floating window permission is opened
         */
        override fun permissionResult(isOpen: Boolean) =
            if (isOpen) createFloat() else callbackCreateFailed(WARN_PERMISSION)

        /**
         * 回调创建失败
         * @param reason 失败原因
         */
        /****
         * Callback creation failed
         * @param reason Failure reason
         */
        private fun callbackCreateFailed(reason: String) {
            config.callbacks?.createdResult(false, reason, null)
            config.floatCallbacks?.builder?.createdResult?.invoke(false, reason, null)
            Logger.w(reason)
            if (reason == WARN_NO_LAYOUT || reason == WARN_UNINITIALIZED || reason == WARN_CONTEXT_ACTIVITY) {
                // 针对无布局、未按需初始化、Activity浮窗上下文错误，直接抛异常
                // For no layout, uninitialized, Activity floating window context error, directly throw an exception
                throw Exception(reason)
            }
        }
    }

}