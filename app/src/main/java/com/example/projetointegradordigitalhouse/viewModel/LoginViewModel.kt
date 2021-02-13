package com.example.projetointegradordigitalhouse.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetointegradordigitalhouse.model.User
import com.github.cesar1287.desafiopicpayandroid.model.home.MarvelXRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel(
    context: Context
) : ViewModel() {

    private val repository: MarvelXRepository by lazy { MarvelXRepository(context) }

//    var homeLoginUser: MutableLiveData<List<User>> = MutableLiveData()




    fun registerUser(firebaseUser: FirebaseUser?) {
        Log.i("HomeViewModel", "Registro de usuário")
        firebaseUser?.let {
            if (!it.isAnonymous) {
                val tempUser = User(
                    firebaseUser.uid,
                    0,
                    it.displayName.toString(),
                    it.email.toString(),
                    null,
                    "Data"
                )
                viewModelScope.launch {
                    repository.setUser(tempUser)
//                    homeLoginUser.postValue(tempList)
                }
            }
        }
    }


}