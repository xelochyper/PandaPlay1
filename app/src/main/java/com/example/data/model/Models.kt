package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data model for Login Request
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "password") val password: String,
    @Json(name = "device_id") val deviceId: String
)

/**
 * Data model for Login Response
 */
@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "status") val status: Boolean,
    @Json(name = "message") val message: String?,
    @Json(name = "token") val token: String?,
    @Json(name = "user") val user: UserProfile?
)

/**
 * Generic item representation for MODs & Liveries
 */
@JsonClass(generateAdapter = true)
data class GameItem(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "type") val type: String = "MOD", // "MOD" or "Livery"
    @Json(name = "category") val category: String = "APK Free",
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "creator") val creator: String = "Panda Studio",
    @Json(name = "rating") val rating: Float = 4.8f,
    @Json(name = "downloads_count") val downloadsCount: Int = 1250,
    @Json(name = "is_premium") val isPremium: Boolean = false,
    @Json(name = "price") val price: String? = "Rp 0",
    @Json(name = "description") val description: String? = null
)

/**
 * Banner Item for Carousel
 */
@JsonClass(generateAdapter = true)
data class BannerItem(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "link_url") val linkUrl: String? = null,
    @Json(name = "target_id") val targetId: String? = null
)

/**
 * Home API Response payload
 */
@JsonClass(generateAdapter = true)
data class HomeResponse(
    @Json(name = "status") val status: Boolean = true,
    @Json(name = "banners") val banners: List<BannerItem> = emptyList(),
    @Json(name = "recommendations") val recommendations: List<GameItem> = emptyList(),
    @Json(name = "latest_liveries") val latestLiveries: List<GameItem> = emptyList()
)

/**
 * Download Link model for multi-server links
 */
@JsonClass(generateAdapter = true)
data class DownloadLink(
    @Json(name = "server_name") val serverName: String,
    @Json(name = "url") val url: String,
    @Json(name = "file_size") val fileSize: String? = "25 MB",
    @Json(name = "is_apk") val isApk: Boolean = true
)

/**
 * Item Detail Response
 */
@JsonClass(generateAdapter = true)
data class DetailResponse(
    @Json(name = "status") val status: Boolean = true,
    @Json(name = "data") val data: ItemDetail? = null
)

@JsonClass(generateAdapter = true)
data class ItemDetail(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "type") val type: String = "MOD",
    @Json(name = "category") val category: String = "APK Free",
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "creator") val creator: String = "Panda Studio",
    @Json(name = "rating") val rating: Float = 4.8f,
    @Json(name = "downloads_count") val downloadsCount: Int = 1250,
    @Json(name = "is_premium") val isPremium: Boolean = false,
    @Json(name = "is_purchased") val isPurchased: Boolean = false,
    @Json(name = "price") val price: String? = "Rp 15.000",
    @Json(name = "description") val description: String? = "MOD Bus Simulator Indonesia dengan detail HD, animasi custom, dan sound gahar.",
    @Json(name = "download_links") val downloadLinks: List<DownloadLink> = emptyList()
)

/**
 * User Profile & Purchase History
 */
@JsonClass(generateAdapter = true)
data class PurchaseItem(
    @Json(name = "id") val id: String,
    @Json(name = "item_title") val itemTitle: String,
    @Json(name = "date") val date: String,
    @Json(name = "price") val price: String,
    @Json(name = "status") val status: String = "SUCCESS"
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "profile_pic") val profilePic: String? = null,
    @Json(name = "is_verified") val isVerified: Boolean = true,
    @Json(name = "purchases") val purchases: List<PurchaseItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ProfileResponse(
    @Json(name = "status") val status: Boolean = true,
    @Json(name = "user") val user: UserProfile? = null
)
