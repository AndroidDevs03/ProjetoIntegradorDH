package com.example.projetointegradordigitalhouse.model

sealed class ResponseApi {
    class Success(val data: Any?): ResponseApi()
    class Error(val message: String): ResponseApi()
}