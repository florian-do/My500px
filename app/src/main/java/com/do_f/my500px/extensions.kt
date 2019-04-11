package com.do_f.my500px

import android.content.res.Resources
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import com.do_f.my500px.api.model.Photo

fun View.setImageSizeFromRatioByWidth(max: Float, item: Photo)
        : ViewGroup.LayoutParams? = layoutParams.apply {
    val imageRatio = item.getRatio()

    if (item.width > item.height) {
        height = (max / imageRatio).toInt()
        width = max.toInt()
    } else {
        height = (max * imageRatio).toInt()
        width = max.toInt()
    }
}

fun View.setImageSizeFromRatioByHeight(max: Float, item: Photo)
        : ViewGroup.LayoutParams? = layoutParams.apply {
    val imageRatio = item.getRatio()

    if (item.width > item.height) {
        height = max.toInt()
        width = (max * imageRatio).toInt()
    } else {
        height = max.toInt()
        width = (max * imageRatio).toInt()
    }
}

fun Photo.getRatio(): Float {
    return if (width > height)
        (width.toFloat() / height.toFloat())
    else
        (height.toFloat() / width.toFloat())
}

inline fun <T: TextView> T.afterMeasured(crossinline onFinished: T.() -> Unit)  {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (lineCount != -1) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                onFinished()
            }
        }
    })
}


val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * from https://github.com/Semper-Viventem/Material-backdrop
 */

fun <T : CoordinatorLayout.Behavior<*>> View.findBehavior(): T = layoutParams.run {
    if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

    (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
        ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
}