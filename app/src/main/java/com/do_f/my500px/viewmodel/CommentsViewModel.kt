package com.do_f.my500px.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.do_f.my500px.App
import com.do_f.my500px.api.model.Comment
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.datasource.CommentsDataSourceFactory

class CommentsViewModel : ViewModel() {
    lateinit var data : LiveData<PagedList<Comment>>

    fun init(id : Int, startPage: Int) {
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        val api : PhotosService = App.retrofit.create(PhotosService::class.java)
        val dataSourceFactory = CommentsDataSourceFactory(api, id, startPage)

        data = LivePagedListBuilder(dataSourceFactory, config).build()
    }
}