package com.do_f.my500px.datasource

import androidx.paging.DataSource
import com.do_f.my500px.api.model.Comment
import com.do_f.my500px.api.service.PhotosService

class CommentsDataSourceFactory(api : PhotosService, id : Int, startPage : Int) : DataSource.Factory<Int, Comment>() {

    private val source = CommentsDataSource(api, id, startPage)

    override fun create(): CommentsDataSource = source
}