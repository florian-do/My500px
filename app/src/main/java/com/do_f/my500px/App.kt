package com.do_f.my500px

import android.app.Application
import com.do_f.my500px.api.ApiClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    companion object {
        val retrofit : Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ApiClient.HOSTNAME)
            .client(ApiClient().get())
            .build()
        var defaultSystemUiVisibility: Int = -1
    }
}