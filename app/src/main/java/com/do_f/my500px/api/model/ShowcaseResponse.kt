package com.do_f.my500px.api.model

data class ShowcaseResponse(
    val current_page: Int,
    val feature: String,
    val photos: List<Photo>,
    val total_items: Int,
    val total_pages: Int
)
