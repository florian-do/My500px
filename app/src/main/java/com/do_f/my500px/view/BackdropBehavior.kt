package com.do_f.my500px.view

import android.content.Context
import android.support.annotation.IdRes
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.do_f.my500px.R
import kotlinx.android.synthetic.main.activity_main.view.*

class BackdropBehavior : CoordinatorLayout.Behavior<View> {

    private val TAG = "BackdropBehavior"

    private var backContainer : View ?= null
    private var pictureViewerBackground : View ?= null
    private var child : View ?= null

    private var backContainerId : Int ?= null
    private var pictureViewerBackgroundId : Int ?= null

    constructor() : super()

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (backContainerId == null || pictureViewerBackgroundId == null) return false
        return when (dependency.id) {
            backContainerId -> true
            pictureViewerBackgroundId -> true
            else -> false
        }
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        this.child = child
        when (dependency.id) {
            pictureViewerBackgroundId -> {
                Log.d(TAG, "onDependentViewChanged: pictureViewerBackgroundId")
                pictureViewerBackground = dependency
            }
            backContainerId -> {
                Log.d(TAG, "onDependentViewChanged: backContainerId")
                backContainer = dependency
            }
        }

//        if (child.id == R.id.frontContainer) {
//            Log.d(TAG, "frontContainerID: ")
//        }
//
//        if (dependency.id == R.id.backContainer) {
//            Log.d(TAG, "backContainer: ")
//        }
//
//        if (dependency.id == R.id.pictureViewerBackground) {
//            Log.d(TAG, "pictureBackground: ")
//        }

        if (pictureViewerBackground != null && backContainer != null) {
            Log.d(TAG, "LOAD")
        }

        return super.onDependentViewChanged(parent, child, dependency)
    }

    fun attachBackContainer(@IdRes backContainerId : Int) {
        this.backContainerId = backContainerId
    }

    fun attachPictureViewerBackground(@IdRes pictureViewerBackground : Int) {
        this.pictureViewerBackgroundId = pictureViewerBackground
    }

}