package com.example.projetointegradordigitalhouse.model.comics

data class Result(
    val characters: Characters,
    val collectedIssues: List<CollectedIssue>,
    val dates: List<Date>,
    var description: String,
    val id: Int,
    val issueNumber: Int,
    val pageCount: Int,
    val prices: List<Price>,
    val series: Series,
    val thumbnail: Thumbnail,
    val title: String,
    val variantDescription: String

    //val collections: List<Any>,
    //val creators: Creators,
    //val diamondCode: String,
    //val digitalId: Int,
    //val ean: String,
    //val events: Events,
    //val format: String,
    //val images: List<Image>,
    //val isbn: String,
    //val issn: String,
    //val modified: String,
    //val resourceURI: String,
    //val stories: Stories,
    //val textObjects: List<TextObject>,
    //val upc: String,
    //val urls: List<Url>,
    //val variants: List<Variant>
)