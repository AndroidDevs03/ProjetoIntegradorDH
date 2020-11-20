package com.example.projetointegradordigitalhouse.model.characters

data class Series(
    val available: Int,
    val collectionURI: String,
    val items: List<ItemXX>,
    val returned: Int
)