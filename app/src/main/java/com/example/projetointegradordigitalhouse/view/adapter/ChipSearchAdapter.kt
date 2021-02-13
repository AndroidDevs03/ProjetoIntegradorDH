package com.example.projetointegradordigitalhouse.view.adapter

import android.content.Context
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
import com.example.projetointegradordigitalhouse.view.ChipSearchActivity
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChipSearchAdapter(
        private val charactersList: MutableList<CharacterResult>,
        private val itemClicked: (Int) -> Unit
    ): RecyclerView.Adapter<ChipSearchAdapter.LocalViewHolder>() {

        val viewModel by lazy { ChipSearchViewModel(ChipSearchActivity()) }
        private val firebaseAuth by lazy { Firebase.auth }

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
            holder.charBackground.setOnClickListener { itemClicked(position) }
            holder.charSearch.setOnClickListener { addTag(charactersList[position].name) }
            firebaseAuth.currentUser?.let{
                Log.i("Tela 1", "Entrei 1")
                holder.charFavorite.setOnClickListener { addFav(charactersList[position]) }
                holder.charFavorite.isSelected = true
            }?: run{
                Log.i("Tela 1", "Entrei 2")
                holder.charFavorite.isSelected = false
                holder.charFavorite.isEnabled = false
            }

            Log.i("Tela 1", "View ${position} criada")
        }

        override fun getItemCount(): Int {
            Log.i("Tela 1", "Lista com ${charactersList.size} elementos")
            return charactersList.size
        }
        fun addTag(tag: String){
            viewModel.addSearchTag(tag)
        }
        private fun addFav(characterResult: CharacterResult) {
            viewModel.addFavoriteChar(characterResult)
        }

}