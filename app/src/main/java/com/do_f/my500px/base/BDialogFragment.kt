package com.do_f.my500px.base

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.do_f.my500px.R

open class BDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()

        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Theme_Holo_Light
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Material)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = getDialogAnimation()

        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog?.window?.statusBarColor = ContextCompat.getColor(context!!, getStatusBarColor())
    }

    open fun getDialogAnimation() : Int = R.style.DialogAnimation
    open fun getStatusBarColor() : Int = R.color.white
}