package com.do_f.my500px.api.model

import java.io.Serializable

data class Photo(
    val aperture: String?,
    val camera: String?,
    val category: Int,
    val collections_count: Int,
    val comments: List<Any>,
    val comments_count: Int,
    val converted: Boolean,
    val converted_bits: Int,
    val created_at: String,
    val critiques_callout_dismissed: Boolean,
    val crop_version: Int,
    val description: String,
    val disliked: Boolean,
    val editors_choice: Boolean,
    val editors_choice_date: Any,
    val exclude_gads: Boolean,
    val favorites_count: Int,
    val feature: String,
    val feature_date: String,
    val focal_length: String?,
    val for_critique: Boolean,
    val for_sale: Boolean,
    val for_sale_date: Any,
    val height: Int,
    val hi_res_uploaded: Int,
    val highest_rating: Double,
    val highest_rating_date: String,
    val id: Int,
    val image_format: String,
    val image_url: List<String>,
    val images: List<Image>,
    val is_free_photo: Boolean,
    val iso: String?,
    val latitude: Any,
    val lens: String?,
    val license_requests_enabled: Boolean,
    val license_type: Int,
    val licensing_requested: Boolean,
    val licensing_status: Int,
    val licensing_suggested: Boolean,
    val liked: Boolean,
    val location: Any,
    val longitude: Any,
    val name: String,
    val nsfw: Boolean,
    val positive_votes_count: Int,
    val privacy: Boolean,
    val profile: Boolean,
    val purchased: Boolean,
    val rating: Double,
    val request_to_buy_enabled: Boolean,
    val sales_count: Int,
    val shutter_speed: String?,
    val status: Int,
    val store_height: Int,
    val store_width: Int,
    val tags: List<String>,
    val taken_at: Any,
    val times_viewed: Int,
    val url: String,
    val user: User,
    val user_id: Int,
    val voted: Boolean,
    val votes_count: Int,
    val watermark: Boolean,
    val width: Int
) : Serializable

data class Avatars(
    val default: Default,
    val large: Large,
    val small: Small,
    val tiny: Tiny
) : Serializable

data class Default(
    val https: String
) : Serializable

data class Tiny(
    val https: String
) : Serializable

data class Small(
    val https: String
) : Serializable

data class Large(
    val https: String
) : Serializable

data class Image(
    val format: String,
    val https_url: String,
    val size: Int,
    val url: String
) : Serializable