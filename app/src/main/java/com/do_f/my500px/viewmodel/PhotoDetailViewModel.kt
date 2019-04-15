package com.do_f.my500px.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.do_f.my500px.App
import com.do_f.my500px.BuildConfig
import com.do_f.my500px.api.model.VotesResponse
import com.do_f.my500px.api.service.PhotosService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhotoDetailViewModel: ViewModel() {
    val showUI : MutableLiveData<Boolean> = MutableLiveData()
    val title : ObservableField<String> = ObservableField()
    val author : ObservableField<String> = ObservableField()
    val datetime : ObservableField<String> = ObservableField()
    val commentsCounts : ObservableField<String> = ObservableField()
    val likesCounts : ObservableField<String> = ObservableField()

    val description : ObservableField<String> = ObservableField()
    val pulse : ObservableField<String> = ObservableField()
    val views : ObservableField<String> = ObservableField()
    val popular : ObservableField<String> = ObservableField()
    val brand : ObservableField<String> = ObservableField()
    val lens : ObservableField<String> = ObservableField()
    val exif : ObservableField<String> = ObservableField()

    val api : PhotosService

    init {
        api = App.retrofit.create(PhotosService::class.java)
    }

    fun getVotes(id: Int) : LiveData<VotesResponse> {
        val data : MutableLiveData<VotesResponse> = MutableLiveData()
        api.getVotes(id, BuildConfig.FIVEPX_API_KEY, 0).enqueue(object : Callback<VotesResponse> {
            override fun onFailure(call: Call<VotesResponse>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<VotesResponse>, response: Response<VotesResponse>) {
                data.value = response.body()
            }

        })

        return data
    }
}