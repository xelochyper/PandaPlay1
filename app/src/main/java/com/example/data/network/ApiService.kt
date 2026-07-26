package com.example.data.network

import com.example.data.model.DetailResponse
import com.example.data.model.GameItem
import com.example.data.model.HomeResponse
import com.example.data.model.LoginRequest
import com.example.data.model.LoginResponse
import com.example.data.model.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit REST API Service for Panda Play (CI3 Portal)
 */
interface ApiService {

    @POST("api/apk_login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("api/get_data")
    suspend fun getHomeData(
        @Header("Authorization") token: String? = null
    ): Response<HomeResponse>

    @GET("api/get_mods")
    suspend fun getMods(
        @Query("category") category: String? = null,
        @Header("Authorization") token: String? = null
    ): Response<List<GameItem>>

    @GET("api/get_liveries")
    suspend fun getLiveries(
        @Query("category") category: String? = null,
        @Header("Authorization") token: String? = null
    ): Response<List<GameItem>>

    @GET("api/detail/{id}")
    suspend fun getItemDetail(
        @Path("id") id: String,
        @Header("Authorization") token: String? = null
    ): Response<DetailResponse>

    @GET("api/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String? = null
    ): Response<ProfileResponse>
}
