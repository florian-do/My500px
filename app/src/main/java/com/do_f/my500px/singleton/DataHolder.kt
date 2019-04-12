package com.do_f.my500px.singleton

import androidx.paging.PagedList
import com.do_f.my500px.api.model.Photo

class DataHolder {
    lateinit var data : PagedList<Photo>

    companion object {
        val instance = DataHolder()
    }
}