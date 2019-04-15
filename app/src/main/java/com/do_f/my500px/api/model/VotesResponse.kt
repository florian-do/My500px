package com.do_f.my500px.api.model

data class VotesResponse(
    val current_page: Int,
    val users: List<User>,
    val total_items: Int,
    val total_pages: Int
)