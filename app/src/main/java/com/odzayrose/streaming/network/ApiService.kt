package com.odzayrose.streaming.network

import com.odzayrose.streaming.model.DetailResponse
import com.odzayrose.streaming.model.HomeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api_home.php")
    fun getHomeMovies(): Call<HomeResponse>

    @GET("api_detail.php")
    fun getMovieDetail(@Query("id") id: String): Call<DetailResponse>
}