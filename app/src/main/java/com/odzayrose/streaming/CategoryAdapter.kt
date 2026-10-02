package com.odzayrose.streaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.odzayrose.streaming.model.Category

class CategoryAdapter(private var categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val rvMoviesHorizontal: RecyclerView = view.findViewById(R.id.rvMoviesHorizontal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.tvCategoryName.text = category.kategori

        val movieAdapter = MovieAdapter(category.films)
        holder.rvMoviesHorizontal.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.rvMoviesHorizontal.adapter = movieAdapter
    }

    override fun getItemCount(): Int = categories.size

    // ======== FUNGSI BARU UNTUK SEARCH ========
    fun updateData(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged() // Beritahu Android bahwa data berubah
    }
}