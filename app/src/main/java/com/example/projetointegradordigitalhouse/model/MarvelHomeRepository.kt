package com.example.projetointegradordigitalhouse.model

class MarvelHomeRepository {
    suspend fun getCharacters(pageNumber: Int): ResponseApi {
        return try {
            val response = ApiService.marvelApi.Characters(pageNumber)

            if (response.isSuccessful) {
                ResponseApi.Success(response.body())
            } else {
                if (response.code() == 404) {
                    ResponseApi.Error("Dado não encontrado")
                } else {
                    ResponseApi.Error("Erro ao carregar os dados")
                }
            }
        } catch (exception: Exception) {
            ResponseApi.Error("Erro ao carregar os dados")
        }
    }
    suspend fun getMovies(pageNumber: Int): ResponseApi {
        return try {
            val response = ApiService.marvelApi.Characters(pageNumber)

            if (response.isSuccessful) {
                ResponseApi.Success(response.body())
            } else {
                if (response.code() == 404) {
                    ResponseApi.Error("Dado não encontrado")
                } else {
                    ResponseApi.Error("Erro ao carregar os dados")
                }
            }
        } catch (exception: Exception) {
            ResponseApi.Error("Erro ao carregar os dados")
        }
    }
    suspend fun getComics(pageNumber: Int): ResponseApi {
        return try {
            val response = ApiService.marvelApi.Comics(pageNumber)

            if (response.isSuccessful) {
                ResponseApi.Success(response.body())
            } else {
                if (response.code() == 404) {
                    ResponseApi.Error("Dado não encontrado")
                } else {
                    ResponseApi.Error("Erro ao carregar os dados")
                }
            }
        } catch (exception: Exception) {
            ResponseApi.Error("Erro ao carregar os dados")
        }
    }
    suspend fun getSeries(pageNumber: Int): ResponseApi {
        return try {
            val response = ApiService.marvelApi.Characters(pageNumber)

            if (response.isSuccessful) {
                ResponseApi.Success(response.body())
            } else {
                if (response.code() == 404) {
                    ResponseApi.Error("Dado não encontrado")
                } else {
                    ResponseApi.Error("Erro ao carregar os dados")
                }
            }
        } catch (exception: Exception) {
            ResponseApi.Error("Erro ao carregar os dados")
        }
    }
}