package com.lzf.easyfloat.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.lzf.easyfloat.core.FloatingWindowManager
import com.lzf.easyfloat.enums.ShowPattern
import java.lang.ref.WeakReference

/**
 * @author: liuzhenfeng
 * @function: 通过生命周期回调，判断系统浮窗的过滤信息，以及app是否位于前台，控制浮窗显隐  Determine the filtering information of the system floating window and whether the app is located in the foreground through the lifecycle callback, and control the floating window to show or hide.
 * @date: 2019-07-11  15:51
 */
internal object LifecycleUtils {

    lateinit var application: Application
    private var activityCount = 0
    private var mTopActivity: WeakReference<Activity>? = null

    fun getTopActivity(): Activity? = mTopActivity?.get()

    fun setLifecycleCallbacks(application: Application) {
        this.application = application
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                // 计算启动的activity数目  Calculate the number of started activities
                activity?.let { activityCount++ }
            }

            override fun onActivityResumed(activity: Activity) {
                activity?.let {
                    mTopActivity?.clear()
                    mTopActivity = WeakReference<Activity>(it)
                    // 每次都要判断当前页面是否需要显示  Every time you need to determine whether the current page needs to be displayed
                    checkShow(it)
                }
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                activity.let {
                    // 计算关闭的activity数目，并判断当前App是否处于后台  Calculate the number of closed activities and determine whether the App is in the background
                    activityCount--
                    checkHide(it)
                }
            }

            override fun onActivityDestroyed(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }

    /**
     * 判断浮窗是否需要显示  Determine whether the floating window needs to be displayed
     */
    private fun checkShow(activity: Activity) =
        FloatingWindowManager.windowMap.forEach { (tag, manager) ->
            manager.config.apply {
                when {
                    // 当前页面的浮窗，不需要处理  Current page's floating window, no need to handle
                    showPattern == ShowPattern.CURRENT_ACTIVITY -> return@apply
                    // 仅后台显示模式下，隐藏浮窗  Only background display mode, hide the floating window
                    showPattern == ShowPattern.BACKGROUND -> setVisible(false, tag)
                    // 如果没有手动隐藏浮窗，需要考虑过滤信息  If you don't manually hide the floating window, you need to consider the filtering information
                    needShow -> setVisible(activity.componentName.className !in filterSet, tag)
                }
            }
        }

    /**
     * 判断浮窗是否需要隐藏  Determine whether the floating window needs to be hidden
     */
    private fun checkHide(activity: Activity) {
        // 如果不是finish，并且处于前台，无需判断  If it is not finish, and in the foreground, no need to judge
        if (!activity.isFinishing && isForeground()) return
        FloatingWindowManager.windowMap.forEach { (tag, manager) ->
            // 判断浮窗是否需要关闭  Determine whether the floating window needs to be closed
            if (activity.isFinishing) manager.params.token?.let {
                // 如果token不为空，并且是当前销毁的Activity，关闭浮窗，防止窗口泄漏  If the token is not empty and the current destroyed Activity, close the floating window to prevent window leak
                if (it == activity.window?.decorView?.windowToken) {
                    FloatingWindowManager.dismiss(tag, true)
                }
            }

            manager.config.apply {
                if (!isForeground() && manager.config.showPattern != ShowPattern.CURRENT_ACTIVITY) {
                    // 当app处于后台时，全局、仅后台显示的浮窗，如果没有手动隐藏，需要显示  When the app is in the background, global, only background display floating windows, if they have not been manually hidden, they need to be displayed
                    setVisible(showPattern != ShowPattern.FOREGROUND && needShow, tag)
                }
            }
        }
    }

    fun isForeground() = activityCount > 0

    private fun setVisible(isShow: Boolean = isForeground(), tag: String?) =
        FloatingWindowManager.visible(isShow, tag)

}