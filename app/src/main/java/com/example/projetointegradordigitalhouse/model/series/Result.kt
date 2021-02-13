package com.example.projetointegradordigitalhouse.model.series

data class Result(
    val characters: Characters,
    val comics: Comics,
    var description: Any,
    val id: Int,
    val startYear: Int,
    val stories: Stories,
    val thumbnail: Thumbnail,
    val title: String

    //val creators: Creators,
    //val endYear: Int,
    //val events: Events,
    //val modified: String,
    //val next: Any,
    //val previous: Any,
    //val rating: String,
    //val resourceURI: String,
    //val type: String,
    //val urls: List<Url>
)