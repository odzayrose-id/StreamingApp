package com.odzayrose.streaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.odzayrose.streaming.model.Episode

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val onEpisodeClicked: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEpsNumber: TextView = view.findViewById(R.id.tvEpsNumber)
        val tvEpsTitle: TextView = view.findViewById(R.id.tvEpsTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val eps = episodes[position]
        holder.tvEpsNumber.text = eps.epsKe
        holder.tvEpsTitle.text = "Episode ${eps.epsKe} " + (if (!eps.judulEps.isNullOrEmpty()) "- ${eps.judulEps}" else "")

        holder.itemView.setOnClickListener {
            onEpisodeClicked(eps)
        }
    }

    override fun getItemCount(): Int = episodes.size
}