package com.example.projetointegradordigitalhouse.model.series

data class Comics(
    val available: Int,
    val collectionURI: String,
    val items: List<ItemX>,
    val returned: Int
)