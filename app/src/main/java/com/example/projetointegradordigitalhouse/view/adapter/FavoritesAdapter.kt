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
import com.example.projetointegradordigitalhouse.viewModel.ChipSearchViewModel
import com.example.projetointegradordigitalhouse.viewModel.FavoritesViewModel
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FavoritesAdapter(
    val viewModel: FavoritesViewModel,
    private val listResult: MutableList<GeneralResult>,
    private val tabPosition: Int,
    private val itemClicked: (Int) -> Unit,
    private val favoriteClicked: (Boolean, Int) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.LocalViewHolder>(){

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
        return LocalViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_generic,parent,false))
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val genResult = listResult[position]
        Log.i("RecyclerView", "Criando view ${position}")
        Glide.with(holder.itemView.context)
            .load(genResult.thumbnail)
            .placeholder(R.drawable.logo_app)
            .into(holder.cardImage)
        holder.cardName.text = genResult.name
        holder.cardBackground.setOnClickListener {
            itemClicked(position)
        }
        firebaseAuth.currentUser?.let{
            Log.i("RecyclerView", "Usuário identificado")
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