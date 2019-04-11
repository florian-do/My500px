package com.do_f.my500px.view

import android.content.Context
import android.support.annotation.IdRes
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View

class BackdropBehavior : CoordinatorLayout.Behavior<View> {

    private var backContainer : View ?= null
    private var frontContainer : View ?= null

    private var backContainerId : Int ?= null
    private var frontContainerId : Int ?= null

    constructor() : super()

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (backContainerId == null || frontContainerId == null) return false

        return when (dependency.id) {
            backContainerId -> true
            frontContainerId -> true
            else -> false
        }
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {

        when (dependency.id) {
            frontContainerId -> frontContainer = dependency
            backContainerId -> backContainer = dependency
        }

        if (frontContainer != null && backContainer != null) {

        }

        return super.onDependentViewChanged(parent, child, dependency)
    }

    fun attachBackContainer(@IdRes backContainerId : Int) {
        this.backContainerId = backContainerId
    }

    fun attachFrontContainer(@IdRes frontContainerId : Int) {
        this.frontContainerId = frontContainerId
    }

}