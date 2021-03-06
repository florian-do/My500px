package com.do_f.my500px.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.do_f.my500px.BuildConfig
import com.do_f.my500px.api.model.Comment
import com.do_f.my500px.api.service.PhotosService
import com.do_f.my500px.enumdir.State
import java.io.IOException

class CommentsDataSource(
    val api: PhotosService,
    val id: Int,
    private val startPage: Int,
    val state: MutableLiveData<State>
) : PageKeyedDataSource<Int, Comment>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Comment>) {
        updateState(State.LOADING)
        callApi(startPage) { items, nextPage ->
            callback.onResult(items, null, nextPage)
            updateState(State.DONE)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {
        if (params.key > 0) {
            callApi(params.key) { items, nextPage ->
                callback.onResult(items, nextPage)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Comment>) {

    }

    private fun callApi(page: Int, completion: (List<Comment>, nextPage: Int) -> Unit) {
        try {
            val response = api.getComments(id, BuildConfig.FIVEPX_API_KEY, page).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    val tmp : MutableList<Comment> = it.comments.toMutableList()
                    tmp.reverse()
                    completion(tmp, page - 1)
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