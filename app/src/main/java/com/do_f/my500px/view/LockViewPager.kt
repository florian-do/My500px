package com.do_f.my500px.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class LockViewPager : ViewPager {

    private var isScrollEnable = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return when(isScrollEnable) {
            true -> super.onTouchEvent(ev)
            false -> false
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return when(isScrollEnable) {
            true -> super.onInterceptTouchEvent(ev)
            false -> false
        }
    }

    /**
     * if true scroll will be enable else will not
     */
    fun isScrollEnable(isScrollEnable: Boolean) {
        this.isScrollEnable = isScrollEnable
    }
}