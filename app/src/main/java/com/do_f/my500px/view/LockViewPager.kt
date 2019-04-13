package com.do_f.my500px.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class LockViewPager : ViewPager {

    private var isSwipeEnable = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return when(isSwipeEnable) {
            true -> super.onTouchEvent(ev)
            false -> false
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return when(isSwipeEnable) {
            true -> super.onInterceptTouchEvent(ev)
            false -> false
        }
    }

    /**
     * if true scroll will be enable else will not
     */
    fun isSwipeEnable(isSwipeEnable: Boolean) {
        this.isSwipeEnable = isSwipeEnable
    }
}