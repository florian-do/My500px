package com.do_f.my500px.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.do_f.my500px.api.model.User
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.enumdir.State

class VotesDataSourceFactory(val api : PhotosService, val id : Int) : DataSource.Factory<Int, User>() {

    var source : VotesDataSource? = null
    var state: MutableLiveData<State> = MutableLiveData()

    override fun create(): DataSource<Int, User> {
        source = VotesDataSource(api, id, state)
        return source!!
    }
}