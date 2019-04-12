package com.do_f.my500px

import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import android.view.View

@BindingMethods(
    BindingMethod(
        type = View::class,
        attribute = "android:visibility",
        method = "setVisibility"
    )
)
class DatabindingAdapter {
    companion object {

        @JvmStatic
        @BindingAdapter("android:visibility")
        fun View.setVisibility(b : Boolean) {
            when(b) {
                true -> visibility = View.VISIBLE
                false -> visibility = View.GONE
            }
        }
    }
}