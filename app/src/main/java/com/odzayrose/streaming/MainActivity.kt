package com.odzayrose.streaming

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.odzayrose.streaming.model.Category
import com.odzayrose.streaming.model.HomeResponse
import com.odzayrose.streaming.network.ApiClient
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageView

    private lateinit var rvCategory: RecyclerView
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var categoryAdapter: CategoryAdapter

    private var allCategories: List<Category> = listOf()
    private var currentTypeFilter: String? = null // Menyimpan status filter aktif (Movie / Series)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.BLACK
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnMenu = findViewById(R.id.btnMenu)

        rvCategory = findViewById(R.id.rvCategory)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        searchView = findViewById(R.id.searchView)

        rvCategory.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(allCategories)
        rvCategory.adapter = categoryAdapter

        swipeRefreshLayout.setOnRefreshListener {
            fetchCategories()
        }

        fetchCategories()
        setupSearchView()

        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_all -> {
                    currentTypeFilter = null
                    applyFilters(searchView.query.toString(), currentTypeFilter)
                }
                R.id.nav_movies -> {
                    currentTypeFilter = "Movie"
                    applyFilters(searchView.query.toString(), currentTypeFilter)
                }
                R.id.nav_series -> {
                    currentTypeFilter = "Series"
                    applyFilters(searchView.query.toString(), currentTypeFilter)
                }
                R.id.nav_changelog -> showChangelogDialog()
                R.id.nav_about -> showAboutDialog()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun showChangelogDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_changelog, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        btnClose?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showAboutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_about, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun fetchCategories() {
        ApiClient.instance.getHomeMovies().enqueue(object : Callback<HomeResponse> {
            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val categories = response.body()?.data
                    if (categories != null) {
                        allCategories = categories
                        applyFilters(searchView.query.toString(), currentTypeFilter)
                    }
                }
            }
            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(this@MainActivity, "Tidak dapat terhubung ke jaringan", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                // Terapkan filter gabungan saat mengetik
                applyFilters(newText, currentTypeFilter)
                return true
            }
        })
    }

    // ==== FUNGSI PENYARINGAN GABUNGAN (PENCARIAN + TIPE) ====
    private fun applyFilters(query: String?, type: String?) {
        val lowerCaseQuery = query?.lowercase() ?: ""
        val filteredList = mutableListOf<Category>()

        for (category in allCategories) {
            val filteredMovies = category.films.filter { movie ->
                val matchQuery = movie.judul.lowercase().contains(lowerCaseQuery)
                // Cek apakah tipe cocok. Jika tombol filter tidak aktif (null), anggap selalu cocok.
                val matchType = if (type == null) true else movie.tipe.equals(type, ignoreCase = true)

                matchQuery && matchType
            }

            if (filteredMovies.isNotEmpty()) {
                filteredList.add(Category(category.kategori, filteredMovies))
            }
        }
        categoryAdapter.updateData(filteredList)
    }
}
