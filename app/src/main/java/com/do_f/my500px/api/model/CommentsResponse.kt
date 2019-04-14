package com.do_f.my500px.api.model

data class CommentsResponse(
    val comments: List<Comment>,
    val current_page: Int,
    val media_type: String,
    val total_items: Int,
    val total_pages: Int
)