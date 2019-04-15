package com.do_f.my500px.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.do_f.my500px.App
import com.do_f.my500px.api.model.User
import com.do_f.my500px.api.service.PhotosService

import com.do_f.my500px.datasource.VotesDataSourceFactory
import com.do_f.my500px.enumdir.State

class VotesViewModel : ViewModel() {
    lateinit var data : LiveData<PagedList<User>>

    private lateinit var dataSourceFactory : VotesDataSourceFactory
    fun init(id : Int) {
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        val api : PhotosService = App.retrofit.create(PhotosService::class.java)
        dataSourceFactory = VotesDataSourceFactory(api, id)

        data = LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun getState(): LiveData<State> = dataSourceFactory.source.state
}