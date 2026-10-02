package com.odzayrose.streaming.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    // 1. Mencari ID Person berdasarkan nama
    @GET("search/person")
    fun searchPerson(@Query("api_key") apiKey: String, @Query("query") query: String): Call<JsonObject>

    // 2. Mengambil Detail & Biografi berdasarkan ID (bahasa Indonesia)
    @GET("person/{person_id}")
    fun getPersonDetail(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "id-ID"
    ): Call<JsonObject>
}

object TmdbApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    val instance: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }
}