package com.do_f.my500px

import android.view.View
import android.view.ViewGroup
import com.do_f.my500px.api.model.Photo

fun View.setSizeFromRatio(windowWidth: Float, item: Photo)
        : ViewGroup.LayoutParams? = layoutParams.apply {
    val imageRatio = item.getRatio()
    if (item.width > item.height) {
        height = (windowWidth / imageRatio).toInt()
        width = windowWidth.toInt()
    } else {
        height = (windowWidth * imageRatio).toInt()
        width = windowWidth.toInt()
    }
}

fun Photo.getRatio(): Float {
    return if (width > height)
        (width.toFloat() / height.toFloat())
    else
        (height.toFloat() / width.toFloat())
}