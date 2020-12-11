package com.example.projetointegradordigitalhouse

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class MarvelXApplication :  MultiDexApplication(){
        override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }
}