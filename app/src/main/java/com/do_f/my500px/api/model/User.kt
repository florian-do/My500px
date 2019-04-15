package com.do_f.my500px.api.model

import java.io.Serializable

data class User(
    val affection: Int,
    val avatars: Avatars,
    val city: String,
    val country: String,
    val cover_url: String,
    val firstname: String,
    val followers_count: Int,
    val fullname: String,
    val id: Int,
    val lastname: String,
    val store_on: Boolean,
    val upgrade_status: Int,
    val username: String,
    val userpic_https_url: String,
    val userpic_url: String,
    val usertype: Int
) : Serializable