package com.do_f.my500px.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.BuildConfig
import com.do_f.my500px.api.model.Photo
import com.do_f.my500px.enumdir.State
import java.io.IOException

class ShowcaseDataSource(private val api: PhotosService, val state: MutableLiveData<State>) : PageKeyedDataSource<Int, Photo>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Photo>) {
        updateState(State.LOADING)
        callApi(1) { photos, next ->
            callback.onResult(photos, null, next)
            updateState(State.DONE)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        callApi(params.key) { photos, next ->
            callback.onResult(photos, next)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {

    }

    private fun callApi(page: Int, callback: (List<Photo>, nextPage: Int) -> Unit) {
        try {
            val response = api.getPhotos(BuildConfig.FIVEPX_API_KEY, "popular", 4, 1, page).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    callback(it.photos, it.current_page + 1)
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