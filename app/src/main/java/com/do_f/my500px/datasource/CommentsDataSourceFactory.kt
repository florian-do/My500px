package com.do_f.my500px.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.do_f.my500px.api.model.Comment
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.enumdir.State

class CommentsDataSourceFactory(val api : PhotosService, val id : Int, private val startPage : Int) : DataSource.Factory<Int, Comment>() {

    var source : CommentsDataSource? = null
    var state: MutableLiveData<State> = MutableLiveData()

    override fun create(): DataSource<Int, Comment> {
        source = CommentsDataSource(api, id, startPage, state)
        return source!!
    }
}