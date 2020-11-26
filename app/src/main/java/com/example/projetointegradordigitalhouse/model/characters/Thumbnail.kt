package com.example.projetointegradordigitalhouse.model.characters

data class Thumbnail(
    val extension: String,
    val path: String
){
    fun getThumb():String{
        return "$path.$extension"
    }
}