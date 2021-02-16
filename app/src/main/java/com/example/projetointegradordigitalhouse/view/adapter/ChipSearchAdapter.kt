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
import com.example.projetointegradordigitalhouse.model.GeneralResult
import com.example.projetointegradordigitalhouse.util.Constants
import com.example.projetointegradordigitalhouse.util.Constants.SharedPreferences.PREFIX_CHAR
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChipSearchAdapter(
    val viewModel: ChipSearchViewModel,
    private val listResult: MutableList<GeneralResult>,
    private val itemClicked: (Int) -> Unit,
    private val searchClicked: (Boolean, Int) -> Unit,
    private val favoriteClicked: (Boolean, Int) -> Unit
    ): RecyclerView.Adapter<ChipSearchAdapter.LocalViewHolder>() {

        private val firebaseAuth by lazy { Firebase.auth }

        inner class LocalViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var cardImage = itemView.findViewById<ImageView>(R.id.cv_character_thumb)
            var cardName = itemView.findViewById<TextView>(R.id.cv_character_name)
            var cardBackground = itemView.findViewById<MaterialCardView>(R.id.cv_character_background)
            var cardFavorite = itemView.findViewById<ImageButton>(R.id.cv_character_favorite)
            var cardSearch = itemView.findViewById<ImageButton>(R.id.cv_character_search)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
            Log.i("RecyclerView", "Inflando")
            return LocalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_generic2,parent,false))
        }
        override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
            val genResult = listResult[position]
            Log.i("RecyclerView", "Criando view ${position}")
            Glide.with(holder.itemView.context)
                .load(genResult.thumbnail)
                .into(holder.cardImage)
            holder.cardName.text = genResult.name
            holder.cardBackground.setOnClickListener { itemClicked(position) }
            holder.cardSearch.isSelected = genResult.searchTagFlag
            holder.cardSearch.setOnClickListener {
                if (holder.cardFavorite.isSelected){
                    searchClicked(false, position) // true = adicionar, false = remover
                    holder.cardSearch.isSelected = false
                } else {
                    searchClicked(true, position) // true = adicionar, false = remover
                    holder.cardSearch.isSelected = true
                }
             }
            firebaseAuth.currentUser?.let{
                Log.i("RecyclerView", "Usuário identificado")
                holder.cardFavorite.isSelected = genResult.favoriteTagFlag
                holder.cardFavorite.setOnClickListener {
                    if (holder.cardFavorite.isSelected){
                        favoriteClicked(false, position) // true = adicionar, false = remover
                        holder.cardFavorite.isSelected = false
                    } else {
                        favoriteClicked(true, position) // true = adicionar, false = remover
                        holder.cardFavorite.isSelected = true
                    }
                }
                holder.cardFavorite.isEnabled = true
            }?: run{
                Log.i("RecyclerView", "Usuário anônimo")
                holder.cardFavorite.isEnabled = false
            }

            Log.i("RecyclerView", "View ${position} criada")
        }
        override fun getItemCount(): Int {
            Log.i("RecyclerView", "Lista com ${listResult.size} elementos")
            return listResult.size
        }
}