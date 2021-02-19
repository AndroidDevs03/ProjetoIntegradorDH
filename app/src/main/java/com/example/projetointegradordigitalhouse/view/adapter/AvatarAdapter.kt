package com.example.projetointegradordigitalhouse.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.view.PopUpWindow
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class AvatarAdapter(private val listAvatar: List<Int>,
        private val mcontext: Context) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private lateinit var popUpWindow : PopUpWindow

    private val firebaseAuth by lazy{ Firebase.auth }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.avatar_item, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            holder.addImage(firebaseAuth.currentUser, position)
            mcontext as PopUpWindow
            mcontext.finish()
        }
        Glide.with(holder.itemView.context).load(listAvatar[position]).into(holder.image)

    }

    override fun getItemCount(): Int {
        return listAvatar.size
    }


    class AvatarViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {
        private val firebaseFirestore by lazy {
            Firebase.firestore
        }

        val image = itemView.findViewById<CircleImageView>(R.id.civAvatar)

        fun addImage(firebaseUser: FirebaseUser?, position:Int){
            firebaseUser?.uid?.let {
                firebaseFirestore.collection("users").document(it).update("avatar_id",position)
                    .addOnSuccessListener {
                        Toast.makeText(itemView.context, "Foto salva", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(itemView.context, "Não salvou", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

}

