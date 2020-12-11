package com.example.projetointegradordigitalhouse.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import com.example.projetointegradordigitalhouse.model.characters.Result

//class HomeAdapter(private val onCharacterClicked: (id:Int) -> Unit): PagedListAdapter<Result, HomeAdapter.ViewHolder>(Result.DIFF_CALLBACK) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
//        TODO("Not yet implemented")
//    }

//    class ViewHolder(
//        private val binding: MainListItemBinding
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(user: UsersItem, onItemClicked: (Int) -> Unit) = with(itemView) {
//
//            Glide.with(itemView.context).load(user.img).into(binding.ivMainItemAvatar)
//            binding.tvMainItemUser.text = user.username
//            binding.tvMainItemName.text = user.name
//
//            binding.vgMainItemContainer.setOnClickListener {
//                onItemClicked(this@ViewHolder.adapterPosition)
//            }
//        }
//    }

//}