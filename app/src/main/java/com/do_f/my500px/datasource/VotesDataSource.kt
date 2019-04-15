package com.do_f.my500px.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.do_f.my500px.BuildConfig
import com.do_f.my500px.api.model.User
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.enumdir.State
import java.io.IOException

class VotesDataSource(val api : PhotosService, val id: Int) : PageKeyedDataSource<Int, User>() {

    var state: MutableLiveData<State> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, User>) {
        callApi(1) { photos, next ->
            callback.onResult(photos, null, next)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {
        updateState(State.LOADING)
        callApi(params.key) { photos, next ->
            callback.onResult(photos, next)
            updateState(State.DONE)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, User>) {

    }

    private fun callApi(page: Int, callback: (List<User>, nextPage: Int) -> Unit) {
        try {
            val response = api.getVotes(id, BuildConfig.FIVEPX_API_KEY, page).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    callback(it.users, it.current_page + 1)
                }
            } else {
                updateState(State.ERROR)
            }
        } catch (e: IOException) {
            updateState(State.ERROR)
        }
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }
}