package com.example.projetointegradordigitalhouse.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.model.ComicResult
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.example.projetointegradordigitalhouse.viewModel.FavoritesViewModel

class ComicAdapter (
    val viewModel: FavoritesViewModel,
    var comic: List<ComicResult>?): RecyclerView.Adapter<ComicAdapter.ComicViewholder>(){


    class ComicViewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val image : ImageView = itemview.findViewById(R.id.cv_character_thumb)
        val title : TextView = itemview.findViewById(R.id.cv_character_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewholder {
        return ComicViewholder(LayoutInflater.from(parent.context).inflate(R.layout.card_generic,parent,false))
    }

    override fun onBindViewHolder(holder: ComicViewholder, position: Int) {
        holder.title.text = comic?.get(position)?.name ?: ""
        Glide.with(holder.itemView).load(comic?.get(position)?.thumbnail).into(holder.image)
    }

    override fun getItemCount(): Int {
        return comic?.size ?: 0
    }
}
