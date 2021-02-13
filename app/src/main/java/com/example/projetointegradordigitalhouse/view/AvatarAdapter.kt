package com.example.projetointegradordigitalhouse.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import de.hdodenhof.circleimageview.CircleImageView

class AvatarAdapter(private val listAvatar: List<Int>,
        private val mcontext: Context) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private lateinit var popUpWindow : PopUpWindow

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.avatar_item, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            mcontext as PopUpWindow
            mcontext.finish()
        }
        Glide.with(holder.itemView.context).load(listAvatar[position]).into(holder.image)

    }

    override fun getItemCount(): Int {
        return listAvatar.size
    }

    class AvatarViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<CircleImageView>(R.id.civAvatar)
    }

}

