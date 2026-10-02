package com.odzayrose.streaming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.odzayrose.streaming.network.TmdbApiClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class CastAdapter(
    private val actors: List<String>,
    private val onActorClick: (String, Int?, String?) -> Unit
) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    private val tmdbApiKey = "3a9352e90da3de0d31bf12c4c5e6b2e8"
    private val cacheData = mutableMapOf<String, Pair<Int?, String?>>() // Cache agar tidak fetch ulang

    class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCast: ImageView = view.findViewById(R.id.imgCast)
        val tvCastName: TextView = view.findViewById(R.id.tvCastName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val actorName = actors[position].trim()
        holder.tvCastName.text = actorName
        val fallbackUrl = "https://ui-avatars.com/api/?name=${URLEncoder.encode(actorName, "UTF-8")}&background=2b2b2b&color=ffffff&size=225"

        // Jika data sudah di-cache
        if (cacheData.containsKey(actorName)) {
            val data = cacheData[actorName]
            val imgUrl = data?.second ?: fallbackUrl
            Glide.with(holder.itemView.context).load(imgUrl).into(holder.imgCast)
            holder.itemView.setOnClickListener { onActorClick(actorName, data?.first, imgUrl) }
        } else {
            // Ambil profil dari TMDB
            TmdbApiClient.instance.searchPerson(tmdbApiKey, actorName).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    var personId: Int? = null
                    var profilePath: String? = null

                    if (response.isSuccessful) {
                        val results = response.body()?.getAsJsonArray("results")
                        if (results != null && results.size() > 0) {
                            val person = results[0].asJsonObject
                            personId = person.get("id").asInt
                            if (!person.get("profile_path").isJsonNull) {
                                profilePath = "https://image.tmdb.org/t/p/w185" + person.get("profile_path").asString
                            }
                        }
                    }

                    cacheData[actorName] = Pair(personId, profilePath)
                    val finalImg = profilePath ?: fallbackUrl
                    Glide.with(holder.itemView.context).load(finalImg).into(holder.imgCast)
                    holder.itemView.setOnClickListener { onActorClick(actorName, personId, finalImg) }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Glide.with(holder.itemView.context).load(fallbackUrl).into(holder.imgCast)
                    holder.itemView.setOnClickListener { onActorClick(actorName, null, fallbackUrl) }
                }
            })
        }
    }

    override fun getItemCount(): Int = actors.size
}