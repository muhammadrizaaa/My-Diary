package com.riza0004.mydiary.network

import com.riza0004.mydiary.dataclass.ImgbbResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://api.imgbb.com/"
private const val API_KEY = "b616ca0113133aa8b857f993bec05083"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ImgbbApiService {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String = API_KEY,
        @Part image: MultipartBody.Part
    ): ImgbbResponse
}

object ImgbbApi{
    val services: ImgbbApiService by lazy {
        retrofit.create(ImgbbApiService::class.java)
    }
}