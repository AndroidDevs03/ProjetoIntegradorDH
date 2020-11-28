package com.example.projetointegradordigitalhouse.model.comics

data class Stories(
    val available: Int,
    val collectionURI: String,
    val items: List<ItemXX>,
    val returned: Int
)