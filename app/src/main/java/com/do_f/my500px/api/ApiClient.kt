package com.do_f.my500px.api

import okhttp3.OkHttpClient

class ApiClient {

    companion object {
        const val HOSTNAME = "https://api.500px.com/v1/"
    }

    fun get() : OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}