package com.do_f.my500px.datasource

import androidx.paging.DataSource
import com.do_f.my500px.api.model.User
import com.do_f.my500px.api.service.PhotosService

class VotesDataSourceFactory(api : PhotosService, id : Int) : DataSource.Factory<Int, User>() {

    val source = VotesDataSource(api, id)

    override fun create(): DataSource<Int, User> = source
}