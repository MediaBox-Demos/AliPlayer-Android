package com.lzf.easyfloat.interfaces

import com.lzf.easyfloat.widget.BaseSwitchView

/**
 * @author: liuzhenfeng
 * @date: 2020/10/25  20:25
 * @Package: com.lzf.easyfloat.interfaces
 * @Description: 区域触摸事件回调 Area touch event callbacks
 */
interface OnTouchRangeListener {

    /**
     * 手指触摸到指定区域 Finger touching the designated area
     */
    fun touchInRange(inRange: Boolean, view: BaseSwitchView)

    /**
     * 在指定区域抬起手指 Lift your finger in the designated area
     */
    fun touchUpInRange()

}