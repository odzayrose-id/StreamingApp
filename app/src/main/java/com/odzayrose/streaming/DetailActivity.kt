package com.odzayrose.streaming

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.odzayrose.streaming.model.DetailResponse
import com.odzayrose.streaming.model.Episode
import com.odzayrose.streaming.model.FilmDetail
import com.odzayrose.streaming.network.ApiClient
import com.odzayrose.streaming.network.TmdbApiClient
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.gson.JsonObject
import org.json.JSONArray // Tambahan untuk Parsing Google Translate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var playerContainer: FrameLayout
    private lateinit var playerView: PlayerView
    private lateinit var btnFullscreen: ImageView
    private var exoPlayer: ExoPlayer? = null

    private lateinit var tvJudul: TextView
    private lateinit var tvMeta: TextView
    private lateinit var tvDeskripsi: TextView

    private lateinit var layoutCast: LinearLayout
    private lateinit var rvCast: RecyclerView

    private lateinit var btnTelegram: Button
    private lateinit var btnDownload: Button

    private lateinit var layoutEpisodes: LinearLayout
    private lateinit var spinnerSeason: Spinner
    private lateinit var rvEpisodes: RecyclerView

    private var currentFilm: FilmDetail? = null
    private var isFullscreen = false
    private var currentVideoPosition: Long = 0

    private lateinit var imgArtwork: ImageView
    private lateinit var layoutQualityButtons: LinearLayout
    private lateinit var btn1080p: Button
    private lateinit var btn720p: Button
    private lateinit var btn480p: Button
    private lateinit var btn360p: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Untuk Android 14 ke bawah
        window.statusBarColor = android.graphics.Color.BLACK

        // 2. SOLUSI ANDROID 15 & 16: Ubah warna dasar jendela menjadi hitam
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.BLACK))

        setContentView(R.layout.activity_detail)

        playerContainer = findViewById(R.id.playerContainer)
        playerView = findViewById(R.id.playerView)
        btnFullscreen = findViewById(R.id.btnFullscreen)
        tvJudul = findViewById(R.id.tvDetailJudul)
        tvMeta = findViewById(R.id.tvDetailMeta)
        tvDeskripsi = findViewById(R.id.tvDetailDeskripsi)
        layoutCast = findViewById(R.id.layoutCast)
        rvCast = findViewById(R.id.rvCast)
        btnTelegram = findViewById(R.id.btnTelegram)
        btnDownload = findViewById(R.id.btnDownload)
        layoutEpisodes = findViewById(R.id.layoutEpisodes)
        spinnerSeason = findViewById(R.id.spinnerSeason)
        rvEpisodes = findViewById(R.id.rvEpisodes)
        imgArtwork = findViewById(R.id.imgArtwork)
        layoutQualityButtons = findViewById(R.id.layoutQualityButtons)
        btn1080p = findViewById(R.id.btn1080p)
        btn720p = findViewById(R.id.btn720p)
        btn480p = findViewById(R.id.btn480p)
        btn360p = findViewById(R.id.btn360p)

        val filmId = intent.getStringExtra("MOVIE_ID")
        if (filmId != null) fetchDetailData(filmId)

        btnFullscreen.setOnClickListener { toggleFullscreen() }
    }

    private fun fetchDetailData(id: String) {
        ApiClient.instance.getMovieDetail(id).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                if (response.isSuccessful) {
                    val detailResponse = response.body()
                    if (detailResponse != null) {
                        currentFilm = detailResponse.film
                        populateUI(detailResponse.film, detailResponse.episodes)
                    }
                }
            }
            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                Toast.makeText(this@DetailActivity, "Gagal koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateUI(film: FilmDetail, episodes: List<Episode>) {
        tvJudul.text = film.judul
        tvMeta.text = "${film.tahun} • ${film.asalNegara} • ${film.kategori}"
        tvDeskripsi.text = film.deskripsi

        // 1. PEMERAN & POPUP TMDB
        if (!film.pemeran.isNullOrEmpty()) {
            layoutCast.visibility = View.VISIBLE
            val actorsList = film.pemeran.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            rvCast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvCast.adapter = CastAdapter(actorsList) { actorName, personId, imgUrl ->
                showActorDialog(actorName, personId, imgUrl)
            }
        }

        // 2. KUALITAS VIDEO DEFAULT
        setupQualitiesMap(
            title = film.judul, telegramUrl = film.telegramUrl,
            vid1080 = film.video1080, vid720 = film.video720, vid480 = film.video480, vid360 = film.video360,
            dl1080 = film.download1080, dl720 = film.download720, dl480 = film.download480, dl360 = film.download360
        )

        // 3. DAFTAR EPISODE BERDASARKAN SEASON
        if (film.tipe == "Series" && episodes.isNotEmpty()) {
            layoutEpisodes.visibility = View.VISIBLE
            val episodesBySeason = episodes.groupBy { it.season }
            val seasonList = episodesBySeason.keys.toList().sorted()
            val seasonLabels = seasonList.map { "Season $it" }

            val seasonAdapter = ArrayAdapter(this, R.layout.spinner_item, seasonLabels)
            spinnerSeason.adapter = seasonAdapter

            spinnerSeason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedSeason = seasonList[position]
                    val epsForSeason = episodesBySeason[selectedSeason] ?: listOf()

                    rvEpisodes.layoutManager = LinearLayoutManager(this@DetailActivity)
                    rvEpisodes.adapter = EpisodeAdapter(epsForSeason) { selectedEps ->
                        val judulEps = "${film.judul} - Season ${selectedEps.season} Eps ${selectedEps.epsKe}"
                        tvJudul.text = judulEps
                        tvDeskripsi.text = if (!selectedEps.deskripsi.isNullOrEmpty()) selectedEps.deskripsi else film.deskripsi

                        currentVideoPosition = 0
                        setupQualitiesMap(
                            title = judulEps, telegramUrl = selectedEps.telegramUrl,
                            vid1080 = selectedEps.video1080, vid720 = selectedEps.video720, vid480 = selectedEps.video480, vid360 = selectedEps.video360,
                            dl1080 = selectedEps.download1080, dl720 = selectedEps.download720, dl480 = selectedEps.download480, dl360 = selectedEps.download360
                        )
                        Toast.makeText(this@DetailActivity, "Memutar Episode ${selectedEps.epsKe}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setupQualitiesMap(
        title: String, telegramUrl: String?,
        vid1080: String?, vid720: String?, vid480: String?, vid360: String?,
        dl1080: String?, dl720: String?, dl480: String?, dl360: String?
    ) {
        if (!telegramUrl.isNullOrEmpty()) {
            btnTelegram.visibility = View.VISIBLE
            btnTelegram.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))) }
        } else {
            btnTelegram.visibility = View.GONE
        }

        // Hitung berapa banyak kualitas video yang tersedia
        val availableCount = listOf(vid1080, vid720, vid480, vid360).count { !it.isNullOrEmpty() }

        if (availableCount == 0) {
            // === JIKA KOSONG (0 Video) ===
            playerView.visibility = View.GONE
            btnFullscreen.visibility = View.GONE
            layoutQualityButtons.visibility = View.GONE
            btnDownload.visibility = View.GONE

            imgArtwork.visibility = View.VISIBLE
            currentFilm?.thumbnailUrl?.let { Glide.with(this).load(it).into(imgArtwork) }
            exoPlayer?.stop()

        } else {
            // === JIKA ADA VIDEO (1 atau lebih) ===
            playerView.visibility = View.VISIBLE
            btnFullscreen.visibility = View.VISIBLE
            imgArtwork.visibility = View.GONE

            if (availableCount == 1) {
                // Sembunyikan deretan tombol jika hanya ada 1 link
                layoutQualityButtons.visibility = View.GONE
            } else {
                // Tampilkan deretan tombol jika link > 1
                layoutQualityButtons.visibility = View.VISIBLE

                // Atur visibilitas masing-masing tombol
                btn1080p.visibility = if (!vid1080.isNullOrEmpty()) View.VISIBLE else View.GONE
                btn720p.visibility = if (!vid720.isNullOrEmpty()) View.VISIBLE else View.GONE
                btn480p.visibility = if (!vid480.isNullOrEmpty()) View.VISIBLE else View.GONE
                btn360p.visibility = if (!vid360.isNullOrEmpty()) View.VISIBLE else View.GONE

                // Pasang Event Klik
                btn1080p.setOnClickListener { playVideo(vid1080!!); updateDownloadButton(dl1080) }
                btn720p.setOnClickListener { playVideo(vid720!!); updateDownloadButton(dl720) }
                btn480p.setOnClickListener { playVideo(vid480!!); updateDownloadButton(dl480) }
                btn360p.setOnClickListener { playVideo(vid360!!); updateDownloadButton(dl360) }
            }

            // Selalu Auto-Play kualitas terbaik yang tersedia
            when {
                !vid1080.isNullOrEmpty() -> { playVideo(vid1080); updateDownloadButton(dl1080) }
                !vid720.isNullOrEmpty() -> { playVideo(vid720); updateDownloadButton(dl720) }
                !vid480.isNullOrEmpty() -> { playVideo(vid480); updateDownloadButton(dl480) }
                !vid360.isNullOrEmpty() -> { playVideo(vid360); updateDownloadButton(dl360) }
            }
        }
    }

    private fun playVideo(videoUrl: String) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this).build()
            playerView.player = exoPlayer
        } else {
            currentVideoPosition = exoPlayer!!.currentPosition
        }
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer!!.setMediaItem(mediaItem)
        exoPlayer!!.prepare()
        if (currentVideoPosition > 0) exoPlayer!!.seekTo(currentVideoPosition)
        exoPlayer!!.play()
    }

    private fun updateDownloadButton(downloadUrl: String?) {
        if (downloadUrl.isNullOrEmpty()) {
            btnDownload.visibility = View.GONE
        } else {
            btnDownload.visibility = View.VISIBLE
            var finalUrl = downloadUrl
            val needsSafelink = listOf("drive.odzayrose.my.id").any { downloadUrl.contains(it) }
            if (needsSafelink) {
                val encodedUrl = Uri.encode(downloadUrl)
                finalUrl = "https://sfl.gl/st?api=91506c20055849a0d938a26cd9330c4afff233ca&url=$encodedUrl"
            }
            btnDownload.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))) }
        }
    }

    // =========================================================================
    // ==== FUNGSI POPUP INFO AKTOR + TRANSLATE ================================
    // =========================================================================
    private fun showActorDialog(actorName: String, personId: Int?, imgUrl: String?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_actor, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val pbLoading = dialogView.findViewById<ProgressBar>(R.id.pbLoading)
        val imgActor = dialogView.findViewById<ImageView>(R.id.imgActorPhoto)
        val tvName = dialogView.findViewById<TextView>(R.id.tvActorName)
        val tvBio = dialogView.findViewById<TextView>(R.id.tvActorBio)

        tvName.text = actorName

        // Jika Aktor tidak ada di TMDB
        if (personId == null) {
            pbLoading.visibility = View.GONE
            imgActor.visibility = View.VISIBLE
            tvBio.visibility = View.VISIBLE
            tvBio.text = "Pemeran ini tidak ditemukan di database TMDB."
            Glide.with(this).load(imgUrl).into(imgActor)
            return
        }

        // Sub-fungsi merender Teks ke UI Popup
        fun updatePopupUI(person: JsonObject, finalBio: String) {
            val birthday = if (person.has("birthday") && !person.get("birthday").isJsonNull) person.get("birthday").asString else "-"
            val placeOfBirth = if (person.has("place_of_birth") && !person.get("place_of_birth").isJsonNull) person.get("place_of_birth").asString else "-"
            var highResImg = imgUrl
            if (person.has("profile_path") && !person.get("profile_path").isJsonNull) {
                highResImg = "https://image.tmdb.org/t/p/w300" + person.get("profile_path").asString
            }

            Glide.with(this@DetailActivity).load(highResImg).into(imgActor)
            tvBio.text = "Lahir: $birthday\nTempat: $placeOfBirth\n\n$finalBio"

            pbLoading.visibility = View.GONE
            imgActor.visibility = View.VISIBLE
            tvBio.visibility = View.VISIBLE
        }

        val tmdbApiKey = "3a9352e90da3de0d31bf12c4c5e6b2e8"

        // LANGKAH 1: Ambil Biografi Bahasa Indonesia
        TmdbApiClient.instance.getPersonDetail(personId, tmdbApiKey, "id-ID").enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    val person = response.body()!!
                    val bioId = if (person.has("biography") && !person.get("biography").isJsonNull) person.get("biography").asString.trim() else ""

                    if (bioId.isNotEmpty()) {
                        // Jika ada terjemahan resmi, langsung render
                        updatePopupUI(person, bioId)
                    } else {
                        // LANGKAH 2: Jika kosong, Ambil Biografi Default (Bahasa Inggris)
                        TmdbApiClient.instance.getPersonDetail(personId, tmdbApiKey, "en-US").enqueue(object : Callback<JsonObject> {
                            override fun onResponse(call: Call<JsonObject>, responseEn: Response<JsonObject>) {
                                if (responseEn.isSuccessful && responseEn.body() != null) {
                                    val personEn = responseEn.body()!!
                                    val bioEn = if (personEn.has("biography") && !personEn.get("biography").isJsonNull) personEn.get("biography").asString.trim() else ""

                                    if (bioEn.isNotEmpty()) {
                                        // LANGKAH 3: Translate Teks Inggris ke Indonesia!
                                        translateText(bioEn) { translatedBio ->
                                            updatePopupUI(personEn, translatedBio)
                                        }
                                    } else {
                                        updatePopupUI(personEn, "Biografi belum tersedia di TMDB.")
                                    }
                                } else {
                                    updatePopupUI(person, "Biografi belum tersedia di TMDB.")
                                }
                            }
                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                updatePopupUI(person, "Gagal mengambil biografi.")
                            }
                        })
                    }
                } else {
                    pbLoading.visibility = View.GONE
                    tvBio.visibility = View.VISIBLE
                    tvBio.text = "Gagal memuat profil."
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoading.visibility = View.GONE
                tvBio.visibility = View.VISIBLE
                tvBio.text = "Gagal memuat profil."
            }
        })
    }

    // ==== FUNGSI GOOGLE TRANSLATE (BACKGROUND THREAD) UPDATE ====
    private fun translateText(text: String, callback: (String) -> Unit) {
        Thread {
            try {
                // 1. Batasi teks maksimal 4000 karakter agar tidak ditolak oleh Google
                val safeText = if (text.length > 4000) text.substring(0, 4000) + "..." else text

                // 2. Gunakan metode POST (Bukan disisipkan di URL parameter 'q')
                val url = java.net.URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=id&dt=t")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                // Tambahkan User-Agent palsu agar tidak dianggap robot/spam oleh Google
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")

                // 3. Kirim teks panjang melalui Body Data
                val postData = "q=${java.net.URLEncoder.encode(safeText, "UTF-8")}"
                connection.outputStream.write(postData.toByteArray(Charsets.UTF_8))

                // 4. Cek apakah server merespons dengan sukses (Kode 200 OK)
                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }

                    val jsonArray = org.json.JSONArray(response)
                    val chunks = jsonArray.getJSONArray(0)
                    var translatedResult = ""

                    for (i in 0 until chunks.length()) {
                        val chunk = chunks.getJSONArray(i)
                        translatedResult += chunk.getString(0)
                    }

                    runOnUiThread { callback(translatedResult) }
                } else {
                    // Jika server menolak (misal kuota limit), kembalikan teks Inggris
                    runOnUiThread { callback(text) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Jika terjadi error koneksi internet, kembalikan teks Inggris
                runOnUiThread { callback(text) }
            }
        }.start()
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            supportActionBar?.show()
            val heightDp = resources.displayMetrics.density * 230
            playerContainer.layoutParams.height = heightDp.toInt()
            isFullscreen = false
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            supportActionBar?.hide()
            playerContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            isFullscreen = true
        }
    }

    override fun onPause() { super.onPause(); exoPlayer?.pause() }
    override fun onDestroy() { super.onDestroy(); exoPlayer?.release(); exoPlayer = null }
}