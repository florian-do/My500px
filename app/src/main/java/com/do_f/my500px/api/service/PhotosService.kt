package com.do_f.my500px.api.service

import com.do_f.my500px.api.model.CommentsResponse
import com.do_f.my500px.api.model.ShowcaseResponse
import com.do_f.my500px.api.model.VotesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotosService {

    @GET("photos")
    fun getPhotos(@Query("consumer_key") key: String,
                  @Query("feature") feature: String,
                  @Query("image_size") imageSize: Int,
                  @Query("tags") tag: Int,
                  @Query("page") page: Int) : Call<ShowcaseResponse>

    @GET("photos/{id}/comments")
    fun getComments(@Path("id") id: Int,
                    @Query("consumer_key") key: String,
                    @Query("page") page: Int) : Call<CommentsResponse>

    @GET("photos/{id}/votes")
    fun getVotes(@Path("id") id: Int,
                    @Query("consumer_key") key: String,
                    @Query("page") page: Int) : Call<VotesResponse>
}