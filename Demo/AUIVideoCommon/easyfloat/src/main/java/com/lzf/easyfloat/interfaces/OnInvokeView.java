package com.lzf.easyfloat.interfaces;

import android.view.View;

/**
 * @author: liuzhenfeng
 * @function: 设置浮窗内容的接口，由于kotlin暂不支持SAM，所以使用Java接口 The interface to set the content of the floating window, since kotlin does not support SAM for the time being, the Java interface is used.
 * @date: 2019-06-30  14:19
 */
public interface OnInvokeView {

    /**
     * 设置浮窗布局的具体内容 Setting the specifics of the floating window layout
     *
     * @param view 浮窗布局 Floating Window Layout
     */
    void invoke(View view);
}
