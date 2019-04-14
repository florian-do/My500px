package com.do_f.my500px.api.model

data class Comment(
    val body: String,
    val created_at: String,
    val id: Int,
    val parent_id: Int,
    val to_whom_user_id: Int,
    val user: User,
    val user_id: Int
)