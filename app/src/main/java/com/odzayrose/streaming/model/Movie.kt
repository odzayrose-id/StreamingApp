package com.odzayrose.streaming.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val status: String,
    val data: List<Category>
)

data class Category(
    val kategori: String,
    val films: List<Movie>
)

data class Movie(
    val id: String,
    val judul: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String,
    val tipe: String,
    val tahun: String
)