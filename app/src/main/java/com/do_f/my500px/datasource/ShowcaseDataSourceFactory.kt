package com.do_f.my500px.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.enumdir.State

class ShowcaseDataSourceFactory(val api : PhotosService) : DataSource.Factory<Int, Photo>() {

    var source : ShowcaseDataSource? = null
    var state: MutableLiveData<State> = MutableLiveData()

    override fun create(): DataSource<Int, Photo> {
        source = ShowcaseDataSource(api, state)
        return source!!
    }
}