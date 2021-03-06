package com.example.projetointegradordigitalhouse.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.viewModel.FavoritesViewModel

class CharacterAdapter(
    val viewModel: FavoritesViewModel,
    var character: List<CharacterResult>?) : RecyclerView.Adapter<CharacterAdapter.CharacterViewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterAdapter.CharacterViewholder {
        return CharacterViewholder(LayoutInflater.from(parent.context).inflate(R.layout.card_generic,parent,false))
    }

    override fun onBindViewHolder(holder: CharacterViewholder, position: Int) {
        holder.title.text = character?.get(position)?.name ?: ""
        Glide.with(holder.itemView).load(character?.get(position)?.thumbnail).into(holder.image)
    }

    override fun getItemCount(): Int {
        return character?.size ?: 0
    }
    class CharacterViewholder (itemview: View): RecyclerView.ViewHolder(itemview) {
        val image : ImageView = itemview.findViewById(R.id.cv_character_thumb)
        val title : TextView = itemview.findViewById(R.id.cv_character_name)
    }



}
