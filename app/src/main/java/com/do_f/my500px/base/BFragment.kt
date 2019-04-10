package com.do_f.my500px.base

import android.content.Context
import android.support.v4.app.Fragment
import com.do_f.my500px.R
import com.do_f.my500px.listener.OnSystemUIListener

open class BFragment : Fragment() {

    var systemUIListener: OnSystemUIListener? = null

    fun replace(fragment: Fragment) {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.content, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSystemUIListener) {
            systemUIListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        systemUIListener = null
    }
}