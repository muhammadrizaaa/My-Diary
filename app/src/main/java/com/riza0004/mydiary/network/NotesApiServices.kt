package com.riza0004.mydiary.network

import com.riza0004.mydiary.dataclass.Notes
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "https://68416148d48516d1d35b5151.mockapi.io/"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()
interface NotesApiServices {
    @GET("notes")
    suspend fun getNotes(): List<Notes>
}

object NotesApi{
    val services: NotesApiServices by lazy {
        retrofit.create(NotesApiServices::class.java)
    }
}
 enum class ApiStatus{LOADING, SUCCESS, FAILED}