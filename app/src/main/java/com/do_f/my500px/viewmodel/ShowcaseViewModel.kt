package com.do_f.my500px.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.do_f.my500px.App
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.datasource.ShowcaseDataSourceFactory
import com.do_f.my500px.enumdir.State


class ShowcaseViewModel: ViewModel() {

    var data : LiveData<PagedList<Photo>>
    val dataSourceFactory : ShowcaseDataSourceFactory

    init {
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        val api : PhotosService = App.retrofit.create(PhotosService::class.java)
        dataSourceFactory = ShowcaseDataSourceFactory(api)

        data = LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun getState(): LiveData<State> = dataSourceFactory.state
}