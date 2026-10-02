# 🎬 Native Android Streaming App

Aplikasi native Android berbasis **Kotlin** untuk platform streaming film dan serial TV. Aplikasi ini dirancang sebagai *frontend* (*client-side*) yang terintegrasi dengan *backend* web **PHP dan MySQL** milik Anda, memberikan pengalaman menonton (*UI/UX*) premium yang mulus menyerupai platform *streaming* modern dengan mode gelap (*Dark Theme*).

## ✨ Fitur Utama

*   📺 **Pemutar Video Native (ExoPlayer):** Memutar video secara stabil dengan rasio dinamis (16:9), fitur *Fullscreen*, dan transisi antar episode tanpa harus memuat ulang halaman.
*   🎛️ **Pilihan Resolusi Dinamis:** Tombol otomatis untuk memilih kualitas video (1080p, 720p, 480p, 360p) yang hanya muncul jika tautan (*link*) tersedia.
*   🔍 **Live Search & Filter Kategori:** Pencarian judul *real-time* tanpa *loading*, dilengkapi tombol filter (*Movies* / *Series*) yang langsung menyortir *RecyclerView*.
*   🎭 **Integrasi API TMDB (Pemeran):** Menampilkan daftar aktor beserta pasfoto. Jika diklik, akan memunculkan *pop-up* profil dan biografi. Terintegrasi dengan **Google Translate API** di *background thread* untuk menerjemahkan biografi bahasa Inggris ke Bahasa Indonesia secara otomatis jika data lokal kosong.
*   🎬 **Manajemen Season & Episode:** Tampilan interaktif khusus untuk *Series*, lengkap dengan *dropdown* pilihan *Season* (desain kapsul/kotak elegan) dan daftar episode di bawahnya.
*   📥 **Bypass Safelink:** Fitur *Download* cerdas yang otomatis membungkus *link* asli ke dalam Safelink.
*   📱 **Navigation Drawer:** Menu samping (*Hamburger Menu*) yang interaktif berisi *Changelog* dan *About* dengan tampilan *pop-up* modern.
*   🎨 **UI/UX Premium:** Antarmuka dengan garis melengkung (*CardView radius*), *header* menyatu dengan *Status Bar* (hitam pekat), dan *Artwork Poster* otomatis jika video tidak tersedia.

## 🛠️️ Teknologi & Library

*   **Bahasa Pemrograman:** Kotlin
*   **Arsitektur Jaringan:** REST API (PHP JSON Endpoint)
*   **Networking:** [Retrofit2](https://square.github.io/retrofit/) & Gson Converter
*   **Image Loading:** [Glide](https://github.com/bumptech/glide)
*   **Media Player:** [ExoPlayer](https://github.com/google/ExoPlayer) oleh Google
*   **External API:** TMDB API (The Movie Database), Google Translate API, UI-Avatars (Fallback Profile)

## 📸 Tangkapan Layar (Screenshots)

<!-- Ganti link gambar di bawah dengan link screenshot aplikasi Anda di Github -->
| Beranda & Kategori | Filter Live Search | Detail Film & Pemutar | Pop-up Info Aktor |
| :---: | :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/odzayrose-id/StreamingApp/refs/heads/master/screenshot/01.png" width="200"> | <img src="https://raw.githubusercontent.com/odzayrose-id/StreamingApp/refs/heads/master/screenshot/02.png" width="200"> | <img src="https://raw.githubusercontent.com/odzayrose-id/StreamingApp/refs/heads/master/screenshot/03.png" width="200"> | <img src="https://raw.githubusercontent.com/odzayrose-id/StreamingApp/refs/heads/master/screenshot/04.png" width="200"> |
