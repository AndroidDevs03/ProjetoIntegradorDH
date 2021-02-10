package com.example.projetointegradordigitalhouse.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.model.CharacterResult
import com.example.projetointegradordigitalhouse.model.characters.Result
import com.google.android.material.card.MaterialCardView

    class ChipSearchAdapter(
        private val charactersList: MutableList<CharacterResult>,
        private val itemClicked: (Int) -> Unit
    ): RecyclerView.Adapter<ChipSearchAdapter.LocalViewHolder>() {

        inner class LocalViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var charImage = itemView.findViewById<ImageView>(R.id.cv_character_thumb)
            var charName = itemView.findViewById<TextView>(R.id.cv_character_name)
            var charBackground = itemView.findViewById<MaterialCardView>(R.id.cv_character_background)
            var charFavorite = itemView.findViewById<ImageButton>(R.id.cv_character_favorite)
            var charSearch = itemView.findViewById<ImageButton>(R.id.cv_character_search)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
            Log.i("Tela 1", "Inflando")
            return LocalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_generic,parent,false))
        }
        override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
            Log.i("Tela 1", "Criando view ${position}")
            Glide.with(holder.itemView.context)
                .load(charactersList[position].thumbnail)
                .fitCenter()
                .placeholder(R.drawable.button)
                .into(holder.charImage)

            holder.charName.text = charactersList[position].name
            holder.charBackground.setOnClickListener {
                itemClicked(position)
            }
            Log.i("Tela 1", "View ${position} criada")
        }
        override fun getItemCount(): Int {
            Log.i("Tela 1", "Lista com ${charactersList.size} elementos")
            return charactersList.size
        }

}