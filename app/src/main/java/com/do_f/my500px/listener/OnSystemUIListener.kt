package com.do_f.my500px.listener

import com.do_f.my500px.api.model.Photo

interface OnSystemUIListener {
    fun isSystemUIHidden(isHidden: Boolean)
}