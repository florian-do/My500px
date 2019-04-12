package com.do_f.my500px.datasource

import androidx.paging.DataSource
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.api.service.PhotosService

class ShowcaseDataSourceFactory(api : PhotosService) : DataSource.Factory<Int, Photo>() {

    private val source = ShowcaseDataSource(api)

    override fun create(): DataSource<Int, Photo> {
        return source
    }
}