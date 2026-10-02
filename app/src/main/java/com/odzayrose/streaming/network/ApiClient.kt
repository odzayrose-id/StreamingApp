package com.odzayrose.streaming.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // GANTI DENGAN DOMAIN WEBSITE ANDA, pastikan diakhiri garis miring "/"
    private const val BASE_URL = "https://streaming.odzayrose.my.id/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}