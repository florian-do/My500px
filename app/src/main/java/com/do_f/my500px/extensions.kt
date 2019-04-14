package com.do_f.my500px

import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.do_f.my500px.api.model.Photo
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

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

fun String.parseDate(r : Resources) : String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val commentDate = LocalDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val date = LocalDate.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val period = Period.between(date, LocalDate.now())

        if (period.isZero) {
            val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            val oldDate = timeFormat.parse(commentDate.format(timeFormatter))
            val currentDate = Date()

            val diff = currentDate.time - oldDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60

            return if (hours == 0L) {
                r.getQuantityString(R.plurals.time_minute, minutes.toInt(), minutes)
            } else {
                r.getQuantityString(R.plurals.time_hour, hours.toInt(), hours)
            }
        } else {
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
            return commentDate.format(formatter)
        }
    }
    return ""
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