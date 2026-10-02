package com.odzayrose.streaming.model

import com.google.gson.annotations.SerializedName

data class DetailResponse(
    val status: String,
    val film: FilmDetail,
    val episodes: List<Episode>
)

data class FilmDetail(
    val id: String,
    val judul: String,
    val deskripsi: String,
    val tahun: String,
    @SerializedName("asal_negara") val asalNegara: String,
    val kategori: String,
    val tipe: String,
    val pemeran: String?, // <--- TAMBAHKAN INI
    @SerializedName("thumbnail_url") val thumbnailUrl: String,
    @SerializedName("telegram_url") val telegramUrl: String?,

    @SerializedName("video_url_360") val video360: String?,
    @SerializedName("video_url_480") val video480: String?,
    @SerializedName("video_url_720") val video720: String?,
    @SerializedName("video_url_1080") val video1080: String?,

    @SerializedName("download_url_360") val download360: String?,
    @SerializedName("download_url_480") val download480: String?,
    @SerializedName("download_url_720") val download720: String?,
    @SerializedName("download_url_1080") val download1080: String?
)

data class Episode(
    val id: String,
    @SerializedName("film_id") val filmId: String,
    val season: String,
    @SerializedName("eps_ke") val epsKe: String,
    @SerializedName("judul_eps") val judulEps: String?,
    val deskripsi: String?,
    @SerializedName("telegram_url") val telegramUrl: String?,

    @SerializedName("video_url_360") val video360: String?,
    @SerializedName("video_url_480") val video480: String?,
    @SerializedName("video_url_720") val video720: String?,
    @SerializedName("video_url_1080") val video1080: String?,

    @SerializedName("download_url_360") val download360: String?,
    @SerializedName("download_url_480") val download480: String?,
    @SerializedName("download_url_720") val download720: String?,
    @SerializedName("download_url_1080") val download1080: String?
)