package com.example.projetointegradordigitalhouse.view

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projetointegradordigitalhouse.R
import com.example.projetointegradordigitalhouse.databinding.ActivityPopUpWindowBinding
import com.example.projetointegradordigitalhouse.model.Avatar


@Suppress("DEPRECATION")
class PopUpWindow : Activity() {

    private var listAvatar = listOf<Int>()

    private lateinit var binding: ActivityPopUpWindowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopUpWindowBinding.inflate(layoutInflater)
        overridePendingTransition(0, 0)
        setContentView(binding.root)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((width*.8).toInt(), (height*.5).toInt())

        val params : WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER

        listAvatar = Avatar.avatar

        val recyclerView = binding.recyclerView

        val avatarAdapter = AvatarAdapter(listAvatar, this)
        recyclerView.layoutManager = GridLayoutManager(this,4)
        recyclerView.adapter = avatarAdapter
        avatarAdapter.notifyDataSetChanged()
    }

}