package com.example.projetointegradordigitalhouse.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.ResponseApi
import com.github.cesar1287.desafiopicpayandroid.model.home.HomeBusiness
import kotlinx.coroutines.launch

internal class HomeViewModel: ViewModel() {

    private val homeBusiness = HomeBusiness()

    fun getCharacters() {
        viewModelScope.launch {
            when (val response = homeBusiness.getCharacters()) {
                is ResponseApi.Success -> {
//                    usersLiveData.postValue(
////                        response.data as? Users
//                    )
                }
                is ResponseApi.Error -> {
//                    errorMessageLiveData.postValue(
//                        response.message
//                    )
                }
            }
        }
    }
}