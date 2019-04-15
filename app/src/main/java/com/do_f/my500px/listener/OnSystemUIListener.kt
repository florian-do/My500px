package com.do_f.my500px.listener

interface OnSystemUIListener {
    fun isSystemUIHidden(isHidden: Boolean)
    fun onDataReady()
}