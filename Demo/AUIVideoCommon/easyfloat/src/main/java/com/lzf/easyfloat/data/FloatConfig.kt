package com.lzf.easyfloat.data

import android.view.View
import com.lzf.easyfloat.anim.DefaultAnimator
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.*
import com.lzf.easyfloat.utils.DefaultDisplayHeight

/**
 * @author: liuzhenfeng
 * @function: 浮窗的数据类，方便管理各属性
 * @date: 2019-07-29  10:14
 */
/****
 * @author: liuzhenfeng
 * @function: Floating window data classes for easy management of properties
 * @date: 2019-07-29  10:14
 */
data class FloatConfig(

    // 浮窗的xml布局文件
    // Floating window xml layout file
    var layoutId: Int? = null,
    var layoutView: View? = null,

    // 当前浮窗的tag
    // Current floating window tag
    var floatTag: String? = null,

    // 是否可拖拽
    // Whether it can be dragged
    var dragEnable: Boolean = true,
    // 是否正在被拖拽
    // Whether it is being dragged
    var isDrag: Boolean = false,
    // 是否正在执行动画
    // Whether it is executing animation
    var isAnim: Boolean = false,
    // 是否显示
    // Whether it is displayed
    var isShow: Boolean = false,
    // 是否包含EditText
    // Whether it contains EditText
    var hasEditText: Boolean = false,
    // 状态栏沉浸
    // Immersion status bar
    var immersionStatusBar: Boolean = false,

    // 浮窗的吸附方式（默认不吸附，拖到哪里是哪里）
    // Floating window adhesion (default does not adhere, where it is where it is)
    var sidePattern: SidePattern = SidePattern.DEFAULT,

    // 浮窗显示类型（默认只在当前页显示）
    // Floating window display type (default only displayed on the current page)
    var showPattern: ShowPattern = ShowPattern.CURRENT_ACTIVITY,

    // 宽高是否充满父布局
    // Whether the width and height are filled out of the parent layout
    var widthMatch: Boolean = false,
    var heightMatch: Boolean = false,

    // 浮窗的摆放方式，使用系统的Gravity属性
    // Floating window placement, using the system Gravity attribute
    var gravity: Int = 0,
    // 坐标的偏移量
    // Coordinate offset
    var offsetPair: Pair<Int, Int> = Pair(0, 0),
    // 固定的初始坐标，左上角坐标
    // Fixed initial coordinates, left upper corner coordinates
    var locationPair: Pair<Int, Int> = Pair(0, 0),
    // ps：优先使用固定坐标，若固定坐标不为原点坐标，gravity属性和offset属性无效
    // ps: Use fixed coordinates first, if the fixed coordinates are not the origin coordinates, the gravity attribute and offset attribute are invalid

    // 四周边界值
    // Four side boundaries
    var leftBorder: Int = 0,
    var topBorder: Int = -999,
    var rightBorder: Int = 9999,
    var bottomBorder: Int = 9999,

    // Callbacks
    var invokeView: OnInvokeView? = null,
    var callbacks: OnFloatCallbacks? = null,
    // 通过Kotlin DSL设置回调，无需复写全部方法，按需复写
    // Set callbacks through Kotlin DSL, no need to override all methods, by need to override
    var floatCallbacks: FloatCallbacks? = null,

    // 出入动画
    // Floating window animation
    var floatAnimator: OnFloatAnimator? = DefaultAnimator(),

    // 设置屏幕的有效显示高度（不包含虚拟导航栏的高度），仅针对系统浮窗，一般不用复写
    // Set the effective display height of the screen (not including the height of the virtual navigation bar), only for system floating windows, generally do not need to rewrite
    var displayHeight: OnDisplayHeight = DefaultDisplayHeight(),

    // 不需要显示系统浮窗的页面集合，参数为类名
    // The page collection that does not need to display the system floating window, the parameter is the class name
    val filterSet: MutableSet<String> = mutableSetOf(),
    // 是否设置，当前创建的页面也被过滤
    // Whether it is set, the current created page is also filtered
    internal var filterSelf: Boolean = false,
    // 是否需要显示，当过滤信息匹配上时，该值为false（用户手动调用隐藏，该值也为false，相当于手动过滤）
    // Whether it needs to be displayed, when the filtering information matches, the value is false (the user manually calls hide, the value is false, which is equivalent to manual filtering)
    internal var needShow: Boolean = true

)