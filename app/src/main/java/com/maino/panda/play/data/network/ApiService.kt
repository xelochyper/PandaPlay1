package com.maino.panda.play.data.network

import com.maino.panda.play.data.model.AppUpdateResponse
import com.maino.panda.play.data.model.DetailResponse
import com.maino.panda.play.data.model.GameItem
import com.maino.panda.play.data.model.HomeResponse
import com.maino.panda.play.data.model.LoginRequest
import com.maino.panda.play.data.model.LoginResponse
import com.maino.panda.play.data.model.ProfileResponse
import com.maino.panda.play.data.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @PUT("api/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
        @Header("Authorization") token: String? = null
    ): Response<ProfileResponse>

    @GET("api/check_update")
    suspend fun checkUpdate(
        @Query("version_code") versionCode: Int
    ): Response<AppUpdateResponse>
}
