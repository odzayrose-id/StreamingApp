package com.odzayrose.streaming

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.odzayrose.streaming.model.Movie

class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.tvJudul.text = movie.judul

        Glide.with(holder.itemView.context)
            .load(movie.thumbnailUrl)
            .into(holder.imgPoster)

        // ==== TAMBAHKAN BLOK INI ====
        // Efek klik untuk membuka DetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            // Kirim ID Film ke halaman sebelah
            intent.putExtra("MOVIE_ID", movie.id)
            context.startActivity(intent)
        }
        // ===========================
    }

    override fun getItemCount(): Int = movies.size
}